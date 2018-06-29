package cn.com.single.andstudy.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.com.single.andstudy.NewsAdapter;
import cn.com.single.andstudy.NewsBean;
import cn.com.single.andstudy.R;
import cn.com.single.andstudy.ReFlashListView;

/**
 * @author li
 *  handler负责发送消息，Looper负责接收Handler发送的信息，并直接把消息回传给handler自己
 *  MessageQueue就是一个存储消息的容器
 */
public class ReFlashListViewActivity extends AppCompatActivity implements ReFlashListView.IReflashListener{

    private ReFlashListView mListView;
    private NewsAdapter mAdapter;
    private List<NewsBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);

        mListView = (ReFlashListView) findViewById(R.id.listView);

        mData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mData.add(new NewsBean("标题-->" + (i + 1),"内容-->" + i,"url-->" + i));
        }

        mAdapter = new NewsAdapter(this,mData,mListView);

        mListView.setInterface(this);

    }

    @Override
    public void onReflash() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取最新数据
                setReflashData();
                //通知界面显示数据
                showList(mData);
                //通知listView刷新数据完成
                mListView.reflashComplete();
            }
        },2000);


    }

    @Override
    public void onLoad() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取更多数据
                loadMore();
                //更新listview更新页面
                showList(mData);

                mListView.loadComplete();
            }
        },2000);


    }

    private void loadMore(){
        for (int i = 1; i < 11; i++) {
            mData.add(new NewsBean("标题-更多->" + (i + 1),"内容-更多->" + i,"url-更多->" + i));
        }
    }

    private void setReflashData(){
        mData.clear();
        for (int i = 10; i < 30; i++) {
            mData.add(new NewsBean("标题-刷新->" + (i + 1),"内容-刷新->" + i,"url-刷新->" + i));
        }
    }

    private void showList(List<NewsBean> data){
        if(mAdapter == null){
            mListView = (ReFlashListView) findViewById(R.id.listView);
            mListView.setInterface(this);
            mAdapter = new NewsAdapter(this,data,mListView);
        }else{
            mAdapter.onDataChange(mData);
        }
    }
}
