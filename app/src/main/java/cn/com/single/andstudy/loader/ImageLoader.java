package cn.com.single.andstudy.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author li
 *         Create on 2018/6/22.
 * @Description
 *      图片加载类
 */

public class ImageLoader {

    private static ImageLoader mInstance;

    /**
     * 图片缓存的核心对象
     */
    private LruCache<String,Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    private static final int DEFAULT_THREAD_COUNT = 1;
    /**
     * 队列的调度方式
     */
    private static Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler;
    /**
     * 信号量 决定先后顺序
     */
    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;

    public enum Type{
        FIFO,LIFO;
    }

    /**
     * 单例模式
     * @return
     */
    public static ImageLoader getInstance(){
        if(mInstance == null){
            synchronized (ImageLoader.class){
                if(mInstance == null){
                    mInstance = new ImageLoader(DEFAULT_THREAD_COUNT,mType);
                }
            }
        }

        return mInstance;
    }

    public static ImageLoader getInstance(int threadCount,Type type){
        if(mInstance == null){
            synchronized (ImageLoader.class){
                if(mInstance == null){
                    mInstance = new ImageLoader(threadCount,type);
                }
            }
        }

        return mInstance;
    }

    private ImageLoader(int threadCount, Type type){
        init(threadCount,type);
    }

    /**
     * 初始化操作
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        //后台轮询线程
        mPoolThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池取出一个任务去执行
                        mThreadPool.execute(getTask());

                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //释放一个信号量
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };

        mPoolThread.start();
        //获取应用的最大内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {

                return value.getRowBytes() * value.getHeight();
            }
        };

        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<>();

        mType = type;

        mSemaphoreThreadPool = new Semaphore(threadCount);

    }

    /**
     * 利用信号量控制队列取出 前一个执行完，再取下一个
     * @return
     */
    public Runnable getTask() {
        if(mType == Type.FIFO){
            return mTaskQueue.removeFirst();
        }else if(mType == Type.LIFO) {
            return mTaskQueue.removeLast();
        }
        return null;
    }



    /**
     * 根据path 为imageView设置图片  增加boolean值，判断是否是网络图片
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView){
        imageView.setTag(path);
        if(mUIHandler == null){
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    //获取得到的图片，为imageView回调设置图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView iv = holder.imageView;
                    String p = holder.path;
                    //将path与getTag存储路径进行比较
                    if(iv.getTag().toString().equals(p)){
                        iv.setImageBitmap(bm);
                    }
                }
            };
        }

        Bitmap bm = getBitmapFromLruCache(path);

        if(bm != null){
            refreshBitmap(bm, imageView, path);
        }else {
            addTask(new Runnable(){
                @Override
                public void run() {
                    //加载图片
                    //图片压缩
                    //获取图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageView);
                    //压缩图片
                    Bitmap bm = decodeSampledBitmapFromPath(imageSize.width,imageSize.height,path);

                    //把图片加入到缓存
                    addBitmapToLruCache(bm,path);

                    refreshBitmap(bm, imageView, path);

                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    /**
     * 加载图片 回调
     * @param bm
     * @param imageView
     * @param path
     */
    private void refreshBitmap(Bitmap bm, ImageView imageView, String path) {
        Message message = Message.obtain();

        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bm;
        holder.imageView = imageView;
        holder.path = path;

        message.obj = holder;
        mUIHandler.sendMessage(message);
    }

    /**
     * 将图片 加入缓存
     * @param bm
     * @param path
     */
    private void addBitmapToLruCache(Bitmap bm, String path) {
        if(getBitmapFromLruCache(path) == null){
            if(bm != null){
                mLruCache.put(path,bm);
            }
        }
    }

    /**
     * 根据图片需要显示的宽和高 对图片进行压缩
     * @param width
     * @param height
     * @param path
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(int width, int height, String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        //获取图片的宽高,并不把图片加载到内存中
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        options.inSampleSize = caculateInSampleSize(options,width,height);
        //使用获取到的inSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);

        return bitmap;
    }

    /**
     * 根据需求的宽高，和图片实际的宽高计算sampleSize
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;
        if(width > reqWidth || height > reqHeight){
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio,heightRadio);
        }

        return inSampleSize;
    }


    /**
     * 根据imageView获取适当的压缩的宽和高
     * @param imageView
     * @return
     */

    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();

        DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();

        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        //获取imageView的实际宽度
        int width = imageView.getWidth();
        if(width <= 0){
            //获取imageView在layout中声明的宽度
            width = lp.width;
        }
        if(width <= 0){
            //检查最大值 通过反射获取，满足最低版本低于16的要求
//            width = imageView.getMaxWidth();
            width = getImageFieldValue(imageView,"mMaxWidth");
        }

        if(width <= 0){
           width = metrics.widthPixels;
        }


        int height = imageView.getHeight();

        if(height <= 0){
            //获取imageView在layout中声明的宽度
            height = lp.height;
        }
        if(height <= 0){
            //检查最大值
            height = getImageFieldValue(imageView,"mMaxHeight");
        }

        if(height <= 0){
            height = metrics.heightPixels;
        }

        imageSize.width = width;
        imageSize.height = height;

        return imageSize;
    }

    /**
     * 通过反射获取imageView的某个属性值
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageFieldValue(Object object,String fieldName){
        int value = 0;

        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            int fieldValue = field.getInt(object);
            if(fieldValue > 0 && fieldValue < Integer.MAX_VALUE){
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private class ImageSize{
        int width;
        int height;
    }

    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);

        //if(mPoolThreadHandler == null)wait();
        try {
            if(mPoolThreadHandler == null){
                mSemaphorePoolThreadHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }

    private class ImgBeanHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;

        public ImgBeanHolder(Bitmap bitmap, ImageView imageView, String path) {
            this.bitmap = bitmap;
            this.imageView = imageView;
            this.path = path;
        }

        public ImgBeanHolder() {
        }
    }

}
