package com.dev.rx.conditions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dev.rx.IndexActivity;
import com.dev.rx.R;
import com.dev.rx.login.Login;

public class Conditions extends AppCompatActivity {

    private Button btn, salir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        btn = findViewById(R.id.btnAcept);
        salir = findViewById(R.id.btnSalir);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("accept", true);
                editor.apply();

                Intent intent = new Intent(Conditions.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}