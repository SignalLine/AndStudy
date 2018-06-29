package cn.com.single.andstudy.download;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author li
 *         Create on 2018/6/28.
 * @Description
 *      下载任务类
 */

public class DownloadTask {

    private Context mContext;
    private FileInfo mFileInfo;

    private ThreadDAO mDao;

    private int mFinished = 0;

    public boolean isPause = false;
    /**
     * 线程数量
     */
    private int mThreadCount = 1;
    /**
     * 线程集合
     */
    private List<DownloadThread> mThreadList;
    /**
     * 线程池
     */
    public static ExecutorService sExecutorService =
            Executors.newCachedThreadPool();

    public DownloadTask(Context context, FileInfo fileInfo,int threadCount) {
        mContext = context;
        mFileInfo = fileInfo;
        mThreadCount = threadCount;

        mDao = new ThreadDAOImpl(context);
    }

    public void download(){
        //读取数据库的线程信息
        List<ThreadInfo> list = mDao.getThreads(mFileInfo.getUrl());
//        ThreadInfo threadInfo = null;
//        if(list == null || list.size() == 0){
//            threadInfo = new ThreadInfo(0,mFileInfo.getUrl(),0,mFileInfo.getLength(),0);
//        }else {
//            threadInfo = list.get(0);
//        }
//        //创建子线程
//        new DownloadThread(threadInfo).start();

        if(list == null || list.size() == 0){

            if(list == null){
                list = new ArrayList<>();
            }

            //获取每个线程下载的长度
            long lengthH = mFileInfo.getLength() / mThreadCount;
            for (int i = 0; i < mThreadCount; i++) {
                ThreadInfo threadInfo = new ThreadInfo(i,mFileInfo.getUrl()
                        ,lengthH*i,(i+1)*lengthH - 1,0);
                if(i == mThreadCount - 1){
                    threadInfo.setEnd(mFileInfo.getLength());
                }

                list.add(threadInfo);

                //数据库插入线程信息
                mDao.insertThread(threadInfo);
            }
        }

        //启动多个线程，进行下载
        mThreadList = new ArrayList<>();
        for (ThreadInfo info : list) {
            DownloadThread thread = new DownloadThread(info);
            DownloadTask.sExecutorService.execute(thread);
            //添加线程到集合
            mThreadList.add(thread);
        }
    }

    /**
     * 判断所有线程都执行完毕
     */
    private synchronized void checkAllThreadFinished(){
        boolean allFinished = true;
        for (DownloadThread d : mThreadList) {
            if (!d.isFinished){
                allFinished = false;
                break;
            }
        }

        if(allFinished){

            //删除线程信息
            mDao.deleteThread(mFileInfo.getUrl());

            //发送广播通知UI下载任务结束
            Intent intent = new Intent(DownloadService.ACTION_FINISH);
            intent.putExtra("fileInfo",mFileInfo);
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * 下载线程
     */
    class DownloadThread extends Thread{

        private ThreadInfo mThreadInfo;
        /**
         * 线程是否结束
         */
        public boolean isFinished = false;

        public DownloadThread(ThreadInfo threadInfo) {
            mThreadInfo = threadInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream is = null;
            try {
                URL url = new URL(mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                //设置下载位置
                long start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                conn.setRequestProperty("Range","bytes=" + start + "-" + mThreadInfo.getEnd());

                //设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH,mFileInfo.getFileName());
                raf = new RandomAccessFile(file,"rwd");
                raf.seek(start);

                Intent intent = new Intent(DownloadService.ACTION_UPDATE);

                //开始下载
                mFinished += mThreadInfo.getFinished();
                //部分下载PARTIAL_CONTENT
                if(conn.getResponseCode() == 206){
                    //读取数据
                    is = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;

                    long time = System.currentTimeMillis();

                    while ((len = is.read(buffer)) != -1){
                        //写入文件
                        raf.write(buffer,0,len);
                        //把下载进度发送广播给activity
                        mFinished += len;
                        //累加每个线程完成的进度
                        mThreadInfo.setFinished(mThreadInfo.getFinished() + len);
                        if(System.currentTimeMillis() - time > 800){
                            time = System.currentTimeMillis();
                            intent.putExtra("finished",mFinished * 100 / mFileInfo.getLength());
                            intent.putExtra("id",mThreadInfo.getId());

                            mContext.sendBroadcast(intent);
                        }

                        //在下载暂停时，保存下载进度
                        if(isPause){
                            mDao.updateThread(mThreadInfo.getUrl()
                                    ,mThreadInfo.getId()
                                    ,mThreadInfo.getFinished());
                            return;
                        }
                    }
                }
                //标识线程执行完毕
                isFinished = true;

                checkAllThreadFinished();

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (conn != null) {
                    conn.disconnect();
                }

                try {
                    if (raf != null) {
                        raf.close();
                    }

                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
