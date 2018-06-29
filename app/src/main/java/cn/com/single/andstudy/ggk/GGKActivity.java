package cn.com.single.andstudy.ggk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import cn.com.single.andstudy.R;

public class GGKActivity extends AppCompatActivity {

    private GuaGuaKa mGuaGuaKa;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ggk);
        
        mGuaGuaKa = (GuaGuaKa) findViewById(R.id.ggk);
        mGuaGuaKa.setCompleteListener(new GuaGuaKa.OnGGKCompleteListener() {
            @Override
            public void complete() {
                Toast.makeText(GGKActivity.this, "刮出结果...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
