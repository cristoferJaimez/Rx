package com.dev.rx.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dev.rx.R;

public class DataPharma extends AppCompatActivity {

    private Button btnBack2, btnEnd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_pharma);

        btnBack2 = findViewById(R.id.btnback2);
        btnEnd = findViewById(R.id.btnEnd);

        btnBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataPharma.this, Geo.class);
                startActivity(intent);
            }
        });
    }
}