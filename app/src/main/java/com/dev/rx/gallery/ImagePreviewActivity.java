package com.dev.rx.gallery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dev.rx.R;
import com.github.chrisbanes.photoview.PhotoView;

public class ImagePreviewActivity extends AppCompatActivity {

    // Declarar variables para ImageView y botones
    private PhotoView photoView;
    private String imagePath;
    private ImageButton closeButton, deleteButton, rotateButton;

    private int currentRotation = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        // Obtener la ruta de la imagen de los extras
        imagePath = getIntent().getStringExtra("imagePath");

        // Obtener una referencia al ImageView
        photoView = findViewById(R.id.preView);

        // Cargar la imagen en el PhotoView
        Glide.with(this).load(imagePath).into(photoView);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(ImagePreviewActivity.this);
                builder.setTitle("Confirmar eliminación");
                builder.setMessage("¿Está seguro de que desea eliminar esta imagen?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Eliminar la imagen
                        new Gallery().deleteImageFile(imagePath);
                        // Volver a cargar la actividad Gallery
                        Intent intent = new Intent(ImagePreviewActivity.this, Gallery.class);
                        startActivity(intent);
                        // Establecer el resultado de la actividad y finalizarla
                        setResult(RESULT_OK);
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();

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
                .into(photoView);
    }


}
