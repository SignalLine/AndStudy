package cn.com.single.andstudy.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author li
 *         Create on 2018/6/28.
 * @Description
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "download.db";
    private static final int VERSION = 1;
    private static final String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement, " +
            "thread_id integer,url text,start integer,end integer,finished integer);";

    private static final String SQL_DROP = "drop table if exists thread_info";

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    private static DBHelper sDBHelper;

    public static DBHelper getDBHelper(Context context){
        if(sDBHelper == null){
//            synchronized (DBHelper.class){
//                if(sDBHelper == null){
                    sDBHelper = new DBHelper(context);
//                }
//            }
        }

        return sDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }



}
