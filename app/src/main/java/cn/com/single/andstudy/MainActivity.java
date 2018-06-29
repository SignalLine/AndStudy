package cn.com.single.andstudy;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.com.single.andstudy.activity.CustomCameraActivity;
import cn.com.single.andstudy.download.DownloadActivity;
import cn.com.single.andstudy.ggk.GGKActivity;
import cn.com.single.andstudy.image.ZoomImageActivity;
import cn.com.single.andstudy.loader.ImageLoaderActivity;
import cn.com.single.andstudy.activity.WeixinActivity;
import cn.com.single.andstudy.node.TreeNodeActivity;
import cn.com.single.andstudy.pintu.GamePintuActivity;
import cn.com.single.andstudy.server.ServerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 点击事件
     * @param view
     */
    public void startCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);

    }

    public void start(View view) {
        Intent intent = new Intent(MainActivity.this, CustomCameraActivity.class);
        startActivity(intent);
    }

    public void startWX(View view) {

        startActivity(new Intent(MainActivity.this,WeixinActivity.class));

    }

    public void startHandler(View view) {
        startActivity(new Intent(MainActivity.this, ImageLoaderActivity.class));
    }

    public void startTreeNode(View view) {
        startActivity(new Intent(MainActivity.this, TreeNodeActivity.class));
    }

    public void startServer(View view) {
        startActivity(new Intent(MainActivity.this, ServerActivity.class));
    }

    public void startGame(View view) {
        startActivity(new Intent(MainActivity.this, GamePintuActivity.class));
    }

    public void startImage(View view) {
        startActivity(new Intent(MainActivity.this, ZoomImageActivity.class));
    }

    public void startDown(View view) {
        startActivity(new Intent(MainActivity.this, DownloadActivity.class));
    }

    public void startGGK(View view) {
        startActivity(new Intent(MainActivity.this, GGKActivity.class));
    }
}
