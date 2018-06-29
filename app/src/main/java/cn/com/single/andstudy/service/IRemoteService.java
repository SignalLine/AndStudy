package cn.com.single.andstudy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import cn.com.single.andstudy.Chatone;

/**
 * @author li
 *         Create on 2018/6/21.
 * @Description
 */

public class IRemoteService extends Service {
    /**
     * 当客户端绑定到该服务的时候，会执行
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mIBinder;
    }

    private IBinder mIBinder = new Chatone.Stub(){

        @Override
        public int getPid(int chat) throws RemoteException {

            Log.i("TAG","chat--->" + chat);

            return chat;
        }

        @Override
        public int add(int num1, int num2) throws RemoteException {

            Log.i("TAG","num1--->" + num1 + ",num2-->" + num2);

            return num1 + num2;
        }
    };
}
