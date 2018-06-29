package cn.com.single.andstudy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * @author li
 *         Create on 2018/6/20.
 * @Description
 */

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{

    private Context mContext;
    private List<NewsBean> data;
    private LayoutInflater mInflater;

    private int mStart,mEnd;
    public static String[] URLS;
    private final ImageLoader mImageLoader;
    //首次显示预加载
    private boolean mFirstIn;

    public NewsAdapter(Context context, List<NewsBean> data, ListView listView) {
        this.mContext = context;
        this.data = data;
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader(listView);

        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            URLS[i] = data.get(i).getUrl();
        }
        mFirstIn = true;
        listView.setOnScrollListener(this);
    }

    public void onDataChange(List<NewsBean> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_item_news,null);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.item_iv_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.item_tv_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.item_tv_content);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        String url = data.get(position).getUrl();
        viewHolder.ivIcon.setTag(url);

        mImageLoader.showImageByAsyncTask(viewHolder.ivIcon,url);

        viewHolder.tvTitle.setText(data.get(position).getTitle());
        viewHolder.tvContent.setText(data.get(position).getContent());

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE){
            //加载可显项
            mImageLoader.loadImages(mStart,mEnd);
        }else {
            //停止任务
            mImageLoader.cancelAllTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        if(mFirstIn && visibleItemCount > 0){

            mImageLoader.loadImages(mStart,mEnd);

            mFirstIn = false;
        }
    }

    class ViewHolder{
        public TextView tvTitle,tvContent;
        private ImageView ivIcon;
    }
}
