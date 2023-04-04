package com.dev.rx.gallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dev.rx.R;

public class ImagePreviewActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageButton imageButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        imageButton = findViewById(R.id.btnClose);

        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(ImagePreviewActivity.this, Gallery.class);
                startActivity(intent);
            }
        });

        // Obtener la ruta de la imagen de los extras
        String imagePath = getIntent().getStringExtra("imagePath");

        // Obtener una referencia al ImageView
        imageView = findViewById(R.id.preView);

        // Cargar la imagen en el ImageView
        Glide.with(this).load(imagePath).into(imageView);
    }
}
