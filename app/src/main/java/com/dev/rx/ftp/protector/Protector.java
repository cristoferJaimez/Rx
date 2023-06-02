package com.dev.rx.ftp.protector;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.dev.rx.R;
import com.dev.rx.db.Mysql;

public class Protector extends AppCompatActivity {

    private EditText txtKey;
    private Button btnKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protector);

        txtKey = findViewById(R.id.textKey);
        btnKey = findViewById(R.id.btnEnviarKey);


        btnKey.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String texto = txtKey.getText().toString();
                Mysql mysql = new Mysql();
                mysql.enviarKeyWord(Protector.this, texto);
            }
        });


    }
}