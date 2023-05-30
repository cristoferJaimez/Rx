package com.dev.rx;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.rx.conditions.Conditions;
import com.dev.rx.db.Mysql;
import com.dev.rx.login.Login;

public class IndexActivity extends AppCompatActivity {

    private static final long WAIT_TIME_MS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        ImageView imageView = findViewById(R.id.imageView2);
        Animation appearAnimation = AnimationUtils.loadAnimation(this, R.anim.appear);
        TextView textView = findViewById(R.id.textView4);

        imageView.startAnimation(appearAnimation);
        textView.setAnimation(appearAnimation);



        new CountDownTimer(WAIT_TIME_MS, WAIT_TIME_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do nothing
            }

            @Override
            public void onFinish() {
                boolean hasAccepted = prefs.getBoolean("accept", false);
                Intent intent = hasAccepted ? new Intent(IndexActivity.this, Login.class)
                        : new Intent(IndexActivity.this, Conditions.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
