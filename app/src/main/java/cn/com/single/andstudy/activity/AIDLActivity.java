package cn.com.single.andstudy.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.com.single.andstudy.Chatone;
import cn.com.single.andstudy.R;

public class AIDLActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etNum1,etNum2;
    private TextView tvResult;
    private Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);

        initView();

        bindService();
    }

    private void initView() {
        etNum1 = (EditText) findViewById(R.id.et_num1);
        etNum2 = (EditText) findViewById(R.id.et_num2);

        tvResult = (TextView) findViewById(R.id.tv_result);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int num1 = Integer.parseInt(etNum1.getText().toString());
        int num2 = Integer.parseInt(etNum2.getText().toString());
        try {
            int ret = mChatone.add(num1,num2);

            tvResult.setText(String.valueOf(ret));

        } catch (RemoteException e) {
            e.printStackTrace();
            tvResult.setText("出错了，我也不知道为啥");
        }
    }

    private Chatone mChatone;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定上服务时候
            mChatone = Chatone.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //断开服务时
            mChatone = null;
        }
    };

    private void bindService() {
        //获取服务
        Intent intent = new Intent();
        //新版本 必须显示Intent 启动绑定服务
        intent.setComponent(new ComponentName("cn.com.single.andstudy"
                ,"cn.com.single.andstudy.service.IRemoteService"));
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
