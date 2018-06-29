package cn.com.single.andstudy.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.single.andstudy.R;

/**
 * @author li
 */
public class DownloadActivity extends AppCompatActivity {

    private ListView mListView;
    private List<FileInfo> mFileInfos;
    private FileListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        mListView = (ListView) findViewById(R.id.listView);
        mFileInfos = new ArrayList<>();

        //创建文件信息对象
//        final FileInfo fileInfo = new FileInfo(0,"http://dlsw.baidu.com/sw-search-sp/soft/1a/11798/kugou_v7.6.85.17344_setup.1427079848.exe"
//                        ,"kugou_v7.6.85.17344_setup.1427079848.exe",0,0);

        for (int i = 0; i < 5; i++) {
            FileInfo fileInfo = new FileInfo(i,"http://www.imooc.com/mobile/imooc.apk"
                    ,"imooc.apk",0,0);

            mFileInfos.add(fileInfo);
        }

        mAdapter = new FileListAdapter(this,mFileInfos);


        mListView.setAdapter(mAdapter);



        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_FINISH);
        registerReceiver(mReceiver,filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 更新UI广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownloadService.ACTION_UPDATE.equals(intent.getAction())){
                int finished = intent.getIntExtra("finished",0);
                int id = intent.getIntExtra("id",0);

                mAdapter.updateProgress(id,finished);

            }else if(DownloadService.ACTION_FINISH.equals(intent.getAction())){
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                if(fileInfo != null){
                    //更新进度为0
                    mAdapter.updateProgress(fileInfo.getId(),100);
                    Toast.makeText(context, fileInfo.getFileName() + "下载成功！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
