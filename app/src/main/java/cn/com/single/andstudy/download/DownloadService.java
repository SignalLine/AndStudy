package cn.com.single.andstudy.download;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author li
 *         Create on 2018/6/28.
 * @Description
 */

public class DownloadService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_FINISH = "ACTION_FINISH";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";

    public static final int MSG_INIT = 12;

    private Map<Integer,DownloadTask> mTasks = new LinkedHashMap<>();

    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/downloads/";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(ACTION_START.endsWith(intent.getAction())){
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("SERVICE","start-->" + fileInfo.toString());

            //启动初始化线程
            InitThread initThread = new InitThread(fileInfo);
            DownloadTask.sExecutorService.execute(initThread);

        }else if(ACTION_STOP.equals(intent.getAction())){
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("SERVICE","stop-->" + fileInfo.toString());
            //从集合中取出下载
            DownloadTask task = mTasks.get(fileInfo.getId());
            if(task != null){
                //停止下载
                task.isPause = true;
            }
        }



        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_INIT:

                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.i("SERVICE","thread-->" + fileInfo.toString());
                    //启动下载任务
                    DownloadTask task = new DownloadTask(DownloadService.this,fileInfo,3);
                    task.download();
                    //把下载任务添加到集合中
                    mTasks.put(fileInfo.getId(),task);

                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 初始化子线程
     */
    class InitThread extends Thread{

        private FileInfo mFileInfo;
        public InitThread(FileInfo fileInfo){
            mFileInfo = fileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try{

                //连接网络文件
                URL url = new URL(mFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");

                int length = -1;

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    //获得文件长度
                    length = conn.getContentLength();
                }

                if(length <= 0){
                    return;
                }

                //在本地创建文件
                File dir = new File(DOWNLOAD_PATH);
                if(!dir.exists()){
                    dir.mkdirs();
                }
                File file = new File(dir,mFileInfo.getFileName());
                raf = new RandomAccessFile(file,"rwd");

                //设置文件长度
                raf.setLength(length);

                mFileInfo.setLength(length);

                Message message = mHandler.obtainMessage();
                message.what = MSG_INIT;
                message.obj = mFileInfo;

                mHandler.sendMessage(message);

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (conn != null) {
                    conn.disconnect();
                }

                try {
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
