package com.dev.rx.gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dev.rx.R;
import com.dev.rx.ftp.FtpUpload;
import com.dev.rx.pytorch.ObjectDetectionActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {

    private GridView gridView;
    private List<String> imagePaths = new ArrayList<>();

    private ImageButton btnBackCamera, btnFTP;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);
        btnBackCamera = findViewById(R.id.btnBackCamera);
        btnFTP = findViewById(R.id.btnFTP);


        // Obtener las rutas de las imágenes
        File directory = new File(Environment.getExternalStorageDirectory() + "/Pictures/");
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                imagePaths.add(file.getAbsolutePath());
            }
        }

        // Crear un adaptador para el GridView
        ImageAdapter adapter = new ImageAdapter(Gallery.this, imagePaths);
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // Obtener la ruta de la imagen seleccionada
            String imagePath = imagePaths.get(position);

            // Crear un intent para abrir la vista previa
            Intent intent = new Intent(this, ImagePreviewActivity.class);
            intent.putExtra("imagePath", imagePath);
            startActivity(intent);
        });



        // En el método onCreate
        /*
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imagePath = imagePaths.get(position);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Subir el archivo al servidor FTP
                        new FtpUpload(Gallery.this).uploadFileToFTP(imagePath, Gallery.this);
                        //uploadFileToFTP(imagePath);
                    }
                }).start();
            }
        });
        */


        // botones funcionalidad
        btnBackCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(Gallery.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });

        btnFTP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (String imagePath : imagePaths) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Subir el archivo al servidor FTP
                            new FtpUpload(Gallery.this).uploadFileToFTP(imagePath, Gallery.this);
                            deleteImageFile(imagePath);

                            // Eliminar la imagen una vez subida al FTP
                            File file = new File(imagePath);
                            if (file.delete()) {
                                // Si se eliminó la imagen, actualizar la vista de la galería
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imagePaths.remove(imagePath);
                                        gridView.invalidateViews();
                                    }
                                });
                            }
                        }
                    }).start();
                }

                //

            }
        });



    }

    private static class ImageAdapter extends BaseAdapter {

        private final Context context;
        private final List<String> imagePaths;

        public ImageAdapter(Context context, List<String> imagePaths) {
            this.context = context;
            this.imagePaths = imagePaths;
        }

        @Override
        public int getCount() {
            return imagePaths.size();
        }

        @Override
        public Object getItem(int position) {
            return imagePaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            String imagePath = imagePaths.get(position);
            Glide.with(context).load(imagePath).into(imageView);

            return imageView;
        }
    }

    private void deleteImageFile(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
        }
    }
}