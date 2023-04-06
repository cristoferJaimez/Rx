package com.dev.rx.register;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dev.rx.R;
import com.dev.rx.R.*;
import com.dev.rx.login.Login;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;

public class Geo extends AppCompatActivity {

    private Button btnBack, btnNext;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);

        btnBack = findViewById(id.btnBack);
        btnNext = findViewById(id.btnNext);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Geo.this, Login.class);
                startActivity(intent);
            }
        });



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Geo.this, DataPharma.class);
                startActivity(intent);
            }
        });




    }
}