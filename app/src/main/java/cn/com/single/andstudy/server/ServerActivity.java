package cn.com.single.andstudy.server;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import cn.com.single.andstudy.R;

/**
 * @author li
 */
public class ServerActivity extends AppCompatActivity {

    private SimpleHttpServer server;

    private ImageView ivShowImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        WebConfiguration config = new WebConfiguration();
        config.setPort(8088);
        config.setMaxParallels(50);

        ivShowImage = (ImageView) findViewById(R.id.id_show_image);

        server = new SimpleHttpServer(config);

        server.registerResourceHandler(new ResourceInAssetsHandler(this));
        server.registerResourceHandler(new UploadImageHandler(){
            @Override
            protected void onImageLoaded(String path) {
                super.onImageLoaded(path);

                showImage(path);

            }
        });

        server.startAsync();

    }

    private void showImage(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                ivShowImage.setImageBitmap(bitmap);

                Toast.makeText(ServerActivity.this, "图片上传成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            server.stopAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("server",e.toString());
        }
    }
}
