package com.dev.rx.gallery;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dev.rx.R;
import com.dev.rx.ftp.FtpAuto;
import com.dev.rx.pytorch.ObjectDetectionActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class ViewPicture extends AppCompatActivity {


    private final Object bitmap;

    public ViewPicture() {
        bitmap = null;
    }
    public ViewPicture(Object bitmap) {
        // Aquí se pueden inicializar las variables necesarias
        this.bitmap = bitmap;
    }


    private PhotoView imageView;
    private ImageButton btnSave, btnDelete;

    private     boolean switchState, switchState2;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        // Obtener el array de bytes de la imagen de los extras
        byte[] byteArray = getIntent().getByteArrayExtra("mixedBitmap");

        // Convertir el array de bytes en un objeto Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        switchState = prefs.getBoolean("switch1_state", false);
        switchState2 = prefs.getBoolean("switch2_state", false);
        //Toast.makeText(getApplicationContext(), "Mensaje a mostrar " + switchState, Toast.LENGTH_SHORT).show();


        if (switchState == true) {

            // Obtener una referencia al ImageView
            imageView = findViewById(R.id.viewPicture);

            // Mostrar la imagen en el ImageView
            imageView.setImageBitmap(bitmap);

            btnDelete = findViewById(R.id.btnDelete);
            btnSave = findViewById(R.id.btnSave);

            //saveImageToGallery(mixedBitmap);
            SharedPreferences prefs2 = getSharedPreferences("myPrefs", MODE_PRIVATE);
            switchState2 = prefs2.getBoolean("switch2_state", false);

            //btn save and delete
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Crear un diálogo de alerta para confirmar la eliminación
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewPicture.this);
                    builder.setTitle("Confirmación");
                    builder.setMessage("¿Está seguro de que desea eliminar esta imagen?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Si el usuario confirma la eliminación, inicia la actividad ObjectDetectionActivity
                            Intent intent = new Intent(ViewPicture.this, ObjectDetectionActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                }
            });
            //save
            btnSave.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Crear un diálogo de alerta para confirmar la guardado
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewPicture.this);
                    builder.setTitle("Confirmación");
                    builder.setMessage("¿Está seguro de que desea guardar esta imagen?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Si el usuario confirma el guardado, guarda la imagen en la galería
                            saveImageToGallery(bitmap);
                            // Cierra la actividad actual y vuelve a la actividad anterior
                            if (switchState2 == true) {
                                new FtpAuto().ftpAuto(ViewPicture.this);
                            }
                            finish();
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                }
            });

        } else {
            saveImageToGallery(bitmap);
            finish();
        }


        if (switchState2 == true) {
            new FtpAuto().ftpAuto(ViewPicture.this);
        }

    }


    private void saveImageToGallery(Bitmap bitmap) {
        String timeStamp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, imageFileName);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATA, Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + imageFileName);

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream out = getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(ViewPicture.this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e("ObjectDetectionActivity", "Error saving image to gallery", e);
            Toast.makeText(this, "Error saving image to gallery", Toast.LENGTH_SHORT).show();
        }
    }


}