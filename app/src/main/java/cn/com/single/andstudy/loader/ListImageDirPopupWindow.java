package cn.com.single.andstudy.loader;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import cn.com.single.andstudy.R;
import cn.com.single.andstudy.bean.FolderBean;

/**
 * @author li
 *         Create on 2018/6/22.
 * @Description
 */

public class ListImageDirPopupWindow extends PopupWindow {

    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private ListView mListView;
    private List<FolderBean> mDatas;


    public interface OnDirSelectedListener{
        /**
         * 选中
         * @param bean
         */
        void onSelected(FolderBean bean);
    }

    private OnDirSelectedListener mOnDirSelectedListener;

    public void setOnDirSelectedListener(OnDirSelectedListener onDirSelectedListener){
        this.mOnDirSelectedListener = onDirSelectedListener;
    }


    public ListImageDirPopupWindow(Context context,List<FolderBean> datas){
        calWidthAndHeight(context);

        mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_main,null);
        mDatas = datas;

        setContentView(mConvertView);

        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initViews(context);
        initEvent();
    }


    private void initViews(Context context) {
        mListView = (ListView) mConvertView.findViewById(R.id.id_list_dir);
        mListView.setAdapter(new ListDirAdapter(context,mDatas));
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mOnDirSelectedListener != null){
                    mOnDirSelectedListener.onSelected(mDatas.get(position));
                }
            }
        });
    }


    /**
     * 计算宽度和高
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        mWidth = metrics.widthPixels;
        mHeight = (int) (metrics.heightPixels * 0.8);
    }

    private class ListDirAdapter extends ArrayAdapter<FolderBean>{

        private LayoutInflater mInflater;

        public ListDirAdapter(Context context, List<FolderBean> objects) {
            super(context, 0, objects);

            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.item_popup_main,null);
                viewHolder = new ViewHolder();

                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
                viewHolder.mDirName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
                viewHolder.mDirCount = (TextView) convertView.findViewById(R.id.id_dir_item_count);

                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            FolderBean folderBean = mDatas.get(position);
            //重置
            viewHolder.mImg.setImageResource(R.drawable.pictures_no);

            viewHolder.mDirName.setText(folderBean.getName());
            viewHolder.mDirCount.setText(String.valueOf(folderBean.getCount()));
            ImageLoader.getInstance().loadImage(folderBean.getFirstImgPath(),viewHolder.mImg);



            return convertView;
        }

        private class ViewHolder{
            ImageView mImg;
            TextView mDirName;
            TextView mDirCount;
        }
    }

}
