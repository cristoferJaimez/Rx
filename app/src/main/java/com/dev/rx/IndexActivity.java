package com.dev.rx;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IndexActivity.this, ObjectDetectionActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000); // espera 5 segundos (5000 milisegundos) antes de iniciar la SegundaActivity

    }
}