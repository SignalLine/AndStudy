package cn.com.single.andstudy.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.com.single.andstudy.R;

/**
 * @author li
 */
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //可能为空
        String picPath = getIntent().getStringExtra("picPath");
        Log.i("ResultActivity","picPath--->" + picPath);
        ImageView ivPic = (ImageView) findViewById(R.id.iv_pic);

        //图像旋转
        FileInputStream fis = null;
        try {
            fis =  new FileInputStream(picPath);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight()
                    ,matrix,true);

            ivPic.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
//        ivPic.setImageBitmap(bitmap);
    }
}
