package com.dev.rx;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private PreviewView previewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);


}
}