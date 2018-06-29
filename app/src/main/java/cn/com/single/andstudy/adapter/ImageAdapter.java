package cn.com.single.andstudy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.com.single.andstudy.R;
import cn.com.single.andstudy.loader.ImageLoader;

/**
 * @author li
 *         Create on 2018/6/22.
 * @Description
 */

public class ImageAdapter extends BaseAdapter {

    private static Set<String> mSelectedImg = new HashSet<>();

    private Context context;
    private List<String> mDatas;
    private String dirPath;

    private LayoutInflater inflater;

    private int mScreenWidth;

    public ImageAdapter(Context context,List<String> mDatas,String dirPath){
        this.context = context;
        this.mDatas = mDatas;
        this.dirPath = dirPath;

        inflater = LayoutInflater.from(context);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        mScreenWidth = metrics.widthPixels;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_gridview,null);

            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_item_image);
            viewHolder.mSelect = (ImageButton) convertView.findViewById(R.id.id_item_select);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //重置状态
        viewHolder.mImg.setImageResource(R.drawable.pictures_no);
        viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
        viewHolder.mImg.setColorFilter(null);

//        viewHolder.mImg.setMaxWidth(mScreenWidth / 3);

        ImageLoader.getInstance(3, ImageLoader.Type.LIFO)
                .loadImage(dirPath + "/" + mDatas.get(position),viewHolder.mImg);

        final String filePath = dirPath + "/" + mDatas.get(position);

        viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedImg.contains(filePath)){

                    mSelectedImg.remove(filePath);
                    viewHolder.mImg.setColorFilter(null);
                    viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
                }else {
                    mSelectedImg.add(filePath);
                    viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
                    viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
                }
                //出现闪屏问题
//                notifyDataSetChanged();
            }
        });

        //二次选择
        if(mSelectedImg.contains(filePath)){
            viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
        }

        return convertView;
    }

    private class ViewHolder{
        ImageView mImg;
        ImageButton mSelect;
    }


}
