package cn.com.single.andstudy.loader;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.com.single.andstudy.R;
import cn.com.single.andstudy.adapter.ImageAdapter;
import cn.com.single.andstudy.bean.FolderBean;

/**
 * @author li
 */
public class ImageLoaderActivity extends AppCompatActivity {


    private static final int SCAN_COMPLETE_CODE = 0X111;

    private GridView mGridView;
    private RelativeLayout mBottomLy;
    private TextView mDirName;
    private TextView mDirCount;

    private File mCurrentDir;
    private int mMaxCount;

    private List<FolderBean> mFolderBeans = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    private ImageAdapter mAdapter;
    private List<String> mImgs;


    private ListImageDirPopupWindow mDirPopupWindow;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(SCAN_COMPLETE_CODE == msg.what){
                mProgressDialog.dismiss();

                data2View();

                initDirPopupWindow();
            }
        }
    };

    private void initDirPopupWindow() {
        mDirPopupWindow = new ListImageDirPopupWindow(this,mFolderBeans);
        mDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //背景变亮
                lightOn();
            }
        });

        mDirPopupWindow.setOnDirSelectedListener(new ListImageDirPopupWindow.OnDirSelectedListener() {
            @Override
            public void onSelected(FolderBean bean) {
                mCurrentDir = new File(bean.getDir());
                mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if(name.endsWith(".jpg") || name.endsWith(".jpeg")
                                || name.endsWith(".png")){
                            return true;
                        }
                        return false;
                    }
                }));

                mAdapter = new ImageAdapter(ImageLoaderActivity.this,mImgs,mCurrentDir.getAbsolutePath());

                mGridView.setAdapter(mAdapter);

                mDirName.setText(bean.getName());
                mDirCount.setText(String.valueOf(mImgs.size()));

                mDirPopupWindow.dismiss();
            }
        });
    }

    /**
     * 内容区域变亮
     */
    private void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    /**
     * 内容区域变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader);

        initView();
        initData();
        initEvent();

    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.id_gridView);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

        mDirName = (TextView) findViewById(R.id.id_dir_name);
        mDirCount = (TextView) findViewById(R.id.id_dir_count);
    }

    private void initData() {
        //ContentProvider扫描所有图片
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用!", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread() {
            @Override
            public void run() {
                //TODO:6.0以上需要权限验证
                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = ImageLoaderActivity.this.getContentResolver();
                Cursor cursor = cr.query(mImgUri, null, MediaStore.Images.Media.MIME_TYPE + " = ? or "
                                + MediaStore.Images.Media.MIME_TYPE + " = ? "
                        , new String[]{"image/jpeg", "image/png"}
                        , MediaStore.Images.Media.DATE_MODIFIED);
                //防止重复遍历
                Set<String> mDirPaths = new HashSet<String>();

                while (cursor.moveToNext()){
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();
                    if(parentFile == null){
                        continue;
                    }

                    String dirPath = parentFile.getAbsolutePath();
                    FolderBean folderBean = null;

                    if(mDirPaths.contains(dirPath)){
                        continue;
                    }else {
                        mDirPaths.add(dirPath);

                        folderBean = new FolderBean();
                        folderBean.setDir(dirPath);

                        folderBean.setFirstImgPath(path);
                    }

                    if(parentFile.list() == null){
                        continue;
                    }

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            if(name.endsWith(".jpg") || name.endsWith(".jpeg")
                                    || name.endsWith(".png")){
                                return true;
                            }
                            return false;
                        };
                    }).length;

                    folderBean.setCount(picSize);


                    mFolderBeans.add(folderBean);

                    if(picSize > mMaxCount){
                        mMaxCount = picSize;
                        mCurrentDir = parentFile;
                    }
                }

                cursor.close();
                //扫描完成 释放临时变量内存
                mDirPaths = null;
                //通知扫描完成
                mHandler.sendEmptyMessage(SCAN_COMPLETE_CODE);
            }
        }.start();
    }

    private void initEvent() {

        mBottomLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置显示位置
                mDirPopupWindow.setAnimationStyle(R.style.DirPopupWindowAnim);
                mDirPopupWindow.showAsDropDown(mBottomLy,0,0);

                lightOff();
            }

        });

    }




    private void data2View() {
        if(mCurrentDir == null){
            Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
            return;
        }

        mImgs = Arrays.asList(mCurrentDir.list());

        mAdapter = new ImageAdapter(this,mImgs,mCurrentDir.getAbsolutePath());
        mGridView.setAdapter(mAdapter);

        mDirCount.setText(mMaxCount + "");
        mDirName.setText(mCurrentDir.getName());

    }
}
