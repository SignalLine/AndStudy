package cn.com.single.andstudy;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * @author li
 *         Create on 2018/6/5.
 * @Description
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
