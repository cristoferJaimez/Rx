package com.dev.rx.gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.rx.R;
import com.dev.rx.ftp.FtpUpload;
import com.dev.rx.pytorch.ObjectDetectionActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gallery extends AppCompatActivity {
    private GridView gridView;
    private List<String> imagePaths = new ArrayList<>();
    private ImageButton btnBackCamera, btnFTP, btnDelete;
    private List<Integer> selectedPositions = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);
        btnBackCamera = findViewById(R.id.btnBackCamera);
        btnFTP = findViewById(R.id.btnFTP);
        btnDelete = findViewById(R.id.btnDelete);

        //obtener parametro auto subida al ftp
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean switchState = prefs.getBoolean("switch2_state", false);

        // Obtener las rutas de las imágenes
        File directory = new File(Environment.getExternalStorageDirectory() + "/Pictures/");
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                imagePaths.add(file.getAbsolutePath());
            }
        }

        TextView textView = findViewById(R.id.numeroRx); // Reemplaza "text_view_id" con el ID de tu TextView
        int numFotos = imagePaths.size();
        String texto = "Número de Rx: " + numFotos;
        textView.setText(texto);
        // Ordenar la lista de forma decreciente
        Collections.sort(imagePaths, Collections.reverseOrder());
        // Crear un adaptador para el GridView
        ImageAdapter adapter = new ImageAdapter(Gallery.this, imagePaths);
        gridView.setAdapter(adapter);

        btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Gallery.this);
            builder.setMessage("¿Está seguro de que desea eliminar las imágenes seleccionadas?")
                    .setPositiveButton("Sí", (dialog, id) -> {
                        // Eliminar los elementos seleccionados
                        Collections.sort(selectedPositions, Collections.reverseOrder());
                        for (int position : selectedPositions) {
                            String imagePath = imagePaths.get(position);
                            deleteImageFile(imagePath);
                            imagePaths.remove(position);
                        }
                        int newText = imagePaths.size();
                        textView.setText("Número de Rx: "+newText);
                        selectedPositions.clear();
                        gridView.invalidateViews();
                        btnDelete.setVisibility(View.GONE);
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        // Cancelar la eliminación de los elementos seleccionados
                        dialog.dismiss();
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // Obtener la ruta de la imagen seleccionada
            String imagePath = imagePaths.get(position);
            // Crear un intent para abrir la vista previa
            Intent intent = new Intent(this, ImagePreviewActivity.class);
            intent.putExtra("imagePath", imagePath);
            startActivity(intent);
        });

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (selectedPositions.contains(position)) {
                // Si el elemento ya está seleccionado, lo deseleccionamos y lo marcamos como no seleccionado
                selectedPositions.remove(Integer.valueOf(position));
                view.setBackgroundResource(0);
                // Recuperar imagen original del view y establecerla
                if (view.getTag() != null) {
                    Drawable originalImage = (Drawable) view.getTag();
                    ((ImageView) view).setImageDrawable(originalImage);
                    view.setTag(null);
                }
            } else {
                // Si el elemento no está seleccionado, lo seleccionamos y lo marcamos como seleccionado
                selectedPositions.add(position);
                // Guardar imagen original del view
                view.setTag(((ImageView) view).getDrawable());
                ((ImageView) view).setImageResource(R.drawable.selected_item_border);
            }

            // Mostrar el botón de eliminar si hay elementos seleccionados, ocultarlo de lo contrario
            if (selectedPositions.isEmpty()) {
                btnDelete.setVisibility(View.GONE);
            } else {
                btnDelete.setVisibility(View.VISIBLE);
            }
            return true;
        });
        // botones funcionalidad
        btnBackCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(Gallery.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });
        btnFTP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Gallery.this);
                builder.setMessage("¿Desea subir las imágenes seleccionadas al servidor FTP?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                for (String imagePath : imagePaths) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Subir el archivo al servidor FTP
                                            new FtpUpload(Gallery.this).uploadFileToFTP(imagePath, Gallery.this);

                                            // Eliminar la imagen una vez subida al FTP
                                            deleteImageFile(imagePath);


                                            // Eliminar la ruta de la imagen de la lista de rutas
                                            imagePaths.remove(imagePath);

                                            // Actualizar la vista de la galería

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    gridView.invalidateViews();
                                                }
                                            });
                                        }
                                    }).start();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Cancelar la acción de subir la imagen
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

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

            // Verificar si la lista tiene elementos y si la posición está dentro de los límites
            if (imagePaths != null && position >= 0 && position < imagePaths.size()) {
                String imagePath = imagePaths.get(position);
                Glide.with(context).load(imagePath).into(imageView);
            }

            return imageView;
        }

    }
    public void deleteImageFile(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
                  }
    }
}