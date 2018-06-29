package cn.com.single.andstudy.activity;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.com.single.andstudy.R;

/**
 * @author li
 */
public class CustomCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private Camera mCamera;

    private SurfaceView mPreview;
    private SurfaceHolder mHolder;

    /**
     * 拍照成功，照片的数据
     */
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File tempFile = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/temp.jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tempFile);
                fos.write(data);
                fos.flush();

                Intent intent = new Intent(CustomCameraActivity.this,ResultActivity.class);
                intent.putExtra("picPath",tempFile.getAbsolutePath());
                startActivity(intent);

                CustomCameraActivity.this.finish();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

                try {
                    if(fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);

        mPreview = (SurfaceView) findViewById(R.id.preview);
        mHolder = mPreview.getHolder();

        mHolder.addCallback(this);
        //点击屏幕，自动对焦
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });
    }

    /**
     * 拍照点击事件
     *
     * @param view
     */
    public void capture(View view){

        Camera.Parameters parameters = mCamera.getParameters();
        //设置拍照格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        //设置预览大小
        parameters.setPreviewSize(800,400);
        //设置对焦  自动对焦
        parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){
                    mCamera.takePicture(null,null,mPictureCallback);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCamera == null){
            mCamera = getCamera();

            if(mHolder != null){
                setStartPreview(mCamera,mHolder);
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseCamera();
    }

    /**
     * 开始相机预览
     */
    private void setStartPreview(Camera camera,SurfaceHolder holder){
        try {
            if(camera != null && holder != null) {
                camera.setPreviewDisplay(holder);
                //默认横屏,转为竖屏
                camera.setDisplayOrientation(90);
                camera.startPreview();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera(){
        if(mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();

            mCamera = null;
        }
    }

    public Camera getCamera() {
        Camera camera;
        try {
            camera = Camera.open();
        }catch (Exception e){
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(mCamera,holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //先停止
        mCamera.stopPreview();

        setStartPreview(mCamera,holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //释放资源
        releaseCamera();
    }
}
