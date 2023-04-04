package com.dev.rx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dev.rx.login.Login;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        //new MongoConnect(this).connect();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IndexActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 5000); // espera 5 segundos (5000 milisegundos) antes de iniciar la SegundaActivity

    }
}