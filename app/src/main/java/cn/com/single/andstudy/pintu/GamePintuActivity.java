package cn.com.single.andstudy.pintu;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import cn.com.single.andstudy.R;

public class GamePintuActivity extends AppCompatActivity {

    private GamePintuLayout mPintuLayout;

    private TextView tvLevel,tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pintu);

        mPintuLayout = (GamePintuLayout) findViewById(R.id.game_pintu);

        tvLevel = (TextView) findViewById(R.id.id_level);
        tvTime = (TextView) findViewById(R.id.id_time);

        mPintuLayout.setTimeEnabled(true);

        mPintuLayout.setOnGamePintuListener(new GamePintuLayout.GamePintuListener() {
            @Override
            public void nextLevel(final int nextLevel) {
                new AlertDialog.Builder(GamePintuActivity.this)
                        .setTitle("Game Info")
                        .setMessage("Level Up!")
                        .setPositiveButton("Next Level", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPintuLayout.nextLevel(-1);
                                tvLevel.setText("" + nextLevel);
                            }
                        })
                        .show();

            }

            @Override
            public void timeChanged(int currentTime) {
                tvTime.setText(currentTime + "");
            }

            @Override
            public void gameOver(final int level) {
                new AlertDialog.Builder(GamePintuActivity.this)
                        .setTitle("Game Info")
                        .setMessage("Game Over!")
                        .setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPintuLayout.restart();
                            }
                        }).setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GamePintuActivity.this.finish();
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPintuLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPintuLayout.resume();
    }
}
