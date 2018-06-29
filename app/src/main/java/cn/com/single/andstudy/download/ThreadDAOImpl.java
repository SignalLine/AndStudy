package cn.com.single.andstudy.download;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li
 *         Create on 2018/6/28.
 * @Description
 *      数据访问接口的实现
 */

public class ThreadDAOImpl implements ThreadDAO {

    private DBHelper mHelper;

    public ThreadDAOImpl(Context context){
        mHelper = DBHelper.getDBHelper(context);
    }

    @Override
    public synchronized void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if(threadInfo == null){
            Log.i("ThreadDao","---> threadInfo = null");
            return;
        }
        db.execSQL("insert into thread_info(thread_id,url,start,end,finished) values(?,?,?,?,?)",
                new Object[]{threadInfo.getId(),threadInfo.getUrl()
                        ,threadInfo.getStart(),threadInfo.getEnd()
                        ,threadInfo.getFinished()});

        db.close();
    }

    @Override
    public synchronized void deleteThread(String url) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if(TextUtils.isEmpty(url)){
            Log.i("ThreadDao","---> url = null");
            return;
        }
        db.execSQL("delete from thread_info where url = ?",
                new Object[]{url});

        db.close();
    }

    @Override
    public synchronized void updateThread(String url, int thread_id, int finished) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if(TextUtils.isEmpty(url)){
            Log.i("ThreadDao","---> url = null");
            return;
        }
        db.execSQL("update thread_info set finished = ? where url = ? and thread_id = ?",
                new Object[]{finished,url,thread_id});

        db.close();
    }

    @Override
    public List<ThreadInfo> getThreads(String url) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        if(TextUtils.isEmpty(url)){
            Log.i("ThreadDao","---> url = null");
            return null;
        }

        List<ThreadInfo> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from thread_info where url = ?",
                new String[]{url});

        while (cursor.moveToNext()){
            ThreadInfo info = new ThreadInfo();
            info.setId(cursor.getInt(cursor.getColumnIndex("thread_info")));
            info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            info.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            info.setEnd(cursor.getLong(cursor.getColumnIndex("end")));
            info.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));

            list.add(info);
        }

        cursor.close();
        db.close();

        return list;
    }

    @Override
    public boolean isExists(String url, int thread_id) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        if(TextUtils.isEmpty(url)){
            Log.i("ThreadDao","---> url = null");
            return false;
        }


        Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?",
                new String[]{url,thread_id + ""});

       boolean exists = cursor.moveToNext();

        cursor.close();
        db.close();

        return exists;
    }
}
