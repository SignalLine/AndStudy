package cn.com.single.andstudy.download;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.com.single.andstudy.R;

/**
 * @author li
 *         Create on 2018/6/28.
 * @Description
 *      文件列表适配器
 */

public class FileListAdapter extends BaseAdapter {

    private Context mContext;
    private List<FileInfo> mFileInfos;
    private LayoutInflater mInflater;

    public FileListAdapter(Context context, List<FileInfo> fileInfos) {
        mContext = context;
        mFileInfos = fileInfos;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mFileInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final FileInfo fileInfo = mFileInfos.get(position);

        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_item_download, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvFileName = (TextView) convertView.findViewById(R.id.tvFileName);
            viewHolder.tvPercent = (TextView) convertView.findViewById(R.id.tvPercent);

            viewHolder.btnStart = (Button) convertView.findViewById(R.id.btnStart);
            viewHolder.btnStop = (Button) convertView.findViewById(R.id.btnStop);
            viewHolder.pbProgress = (ProgressBar) convertView.findViewById(R.id.pbProgress);
            //减缓刷新次数
            viewHolder.pbProgress.setMax(100);
            viewHolder.tvFileName.setText(fileInfo.getFileName());

            viewHolder.btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //通过Intent传递参数给service
                    Intent intent = new Intent(mContext,DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);

                    intent.putExtra("fileInfo",fileInfo);

                    mContext.startService(intent);
                }
            });

            viewHolder.btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //通过Intent传递参数给service
                    Intent intent = new Intent(mContext,DownloadService.class);
                    intent.setAction(DownloadService.ACTION_STOP);

                    intent.putExtra("fileInfo",fileInfo);

                    mContext.startService(intent);
                }
            });

            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvPercent.setText(fileInfo.getFinished() + "%");
        viewHolder.pbProgress.setProgress(fileInfo.getFinished());


        return convertView;
    }

    /**
     * 更新列表中某个item下载进度
     * @param id
     * @param progress
     */
    public void updateProgress(int id,int progress){
        FileInfo fileInfo = mFileInfos.get(id);
        fileInfo.setFinished(progress);

        notifyDataSetChanged();
    }

    static class ViewHolder{
        public TextView tvFileName,tvPercent;
        public ProgressBar pbProgress;
        public Button btnStop,btnStart;
    }
}
