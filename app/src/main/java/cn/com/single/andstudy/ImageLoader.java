package cn.com.single.andstudy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author li
 *         Create on 2018/6/20.
 * @Description
 */

public class ImageLoader {

    private ImageView mImageView;
    private String url;
    private ListView mListView;
    private Set<NewsAsyncTask> mTask;

    private LruCache<String ,Bitmap> mCaches;

    public ImageLoader(ListView listView){
        this.mListView = listView;
        mTask = new HashSet<>();

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mCaches = new LruCache<String ,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存时候调用
                return value.getByteCount();
            }
        };
    }

    public void addBitmap2Cache(String url,Bitmap bitmap){
        if(getBitmapFromCache(url) == null){
            mCaches.put(url, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String url){
        return mCaches.get(url);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mImageView.getTag().equals(url)){
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public void loadImages(int start,int end){
        for (int i = start; i < end; i++) {
            String url = NewsAdapter.URLS[i];
            Bitmap bitmap = getBitmapFromCache(url);
            if(bitmap == null){
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);

                mTask.add(task);
            }else {
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);

                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void  showImageByThread(ImageView imageView,final String url){
        this.url = url;
        this.mImageView = imageView;

        new Thread(){
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmapFromUrl(url);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    private Bitmap getBitmapFromUrl(String url) {
        InputStream is = null;
        Bitmap bitmap = null;
        try{
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showImageByAsyncTask(ImageView imageView,String url){

        Bitmap bitmap = getBitmapFromCache(url);
        if(bitmap == null){
            imageView.setImageResource(R.mipmap.ic_launcher);
        }else {
            imageView.setImageBitmap(bitmap);
        }

    }

    public void cancelAllTask() {
        if(mTask != null){
            for (NewsAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }

    private class NewsAsyncTask extends AsyncTask<String,Void,Bitmap>{

//        private ImageView mImageView;
        private String url;

        public NewsAsyncTask(String url){
//            this.mImageView = imageView;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String url = params[0];
            Bitmap bitmap = getBitmapFromUrl(url);
            if(bitmap != null){
                addBitmap2Cache(url,bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

//            addBitmap2Cache(url,bitmap);
//            mImageView.setImageBitmap(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(this.url);
            if(imageView != null && bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
