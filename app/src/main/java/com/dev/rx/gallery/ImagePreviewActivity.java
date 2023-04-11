package com.dev.rx.gallery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.dev.rx.R;

import java.io.File;

public class ImagePreviewActivity extends AppCompatActivity {

    // Declarar variables para ImageView y botones
    private ImageView imageView;
    private ImageButton closeButton, deleteButton, rotateButton;
    private String imagePath;
    private int currentRotation = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        // Obtener la ruta de la imagen de los extras
        imagePath = getIntent().getStringExtra("imagePath");

        // Obtener una referencia al ImageView
        imageView = findViewById(R.id.preView);

        // Cargar la imagen en el ImageView
        Glide.with(this).load(imagePath).into(imageView);

        // Obtener referencias a los botones
        closeButton = findViewById(R.id.btnClose);
        deleteButton = findViewById(R.id.btnDelete);
        rotateButton = findViewById(R.id.btnRotate);

        // Establecer onClickListener para el botón Cerrar
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(ImagePreviewActivity.this, Gallery.class);
                startActivity(intent);
            }
        });

        // Establecer onClickListener para el botón Eliminar
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Eliminar la imagen
                deleteImage();

                // Establecer el resultado de la actividad y finalizarla
                setResult(RESULT_OK);
                finish();
            }
        });


        // Establecer onClickListener para el botón Rotar
        rotateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Rotar la imagen y actualizar la vista
                currentRotation += 90;
                if (currentRotation >= 360) {
                    currentRotation = 0;
                }
                rotateImage(currentRotation);
            }
        });

    }

    // Función para rotar la imagen y actualizar la vista
    private void rotateImage(int angle) {
        Glide.with(this).load(imagePath)
                .transform(new RotateTransformation(this, angle))
                .into(imageView);
    }

    // Función para eliminar la imagen
    private void deleteImage() {
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
            // Actualizar la galería de imágenes para que la imagen eliminada desaparezca de la lista
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }
    }
}
