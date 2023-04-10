package com.dev.rx.config;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.dev.rx.R;
import com.dev.rx.pytorch.ObjectDetectionActivity;

public class Config extends AppCompatActivity {

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        btnBack = findViewById(R.id.btn_back_camera);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });





    }
}