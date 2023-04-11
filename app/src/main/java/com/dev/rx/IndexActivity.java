package com.dev.rx;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.rx.login.Login;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        //new MongoConnect(this).connect();
        ImageView imageView = findViewById(R.id.imageView2);
        Animation appearAnimation = AnimationUtils.loadAnimation(this, R.anim.appear);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txt = findViewById(R.id.textView4);


        imageView.startAnimation(appearAnimation);
        txt.setAnimation(appearAnimation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IndexActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // espera 5 segundos (5000 milisegundos) antes de iniciar la SegundaActivity

    }
}