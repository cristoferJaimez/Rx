package com.dev.rx.gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.rx.R;
import com.dev.rx.ftp.FtpUpload;
import com.dev.rx.pytorch.ObjectDetectionActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.graphics.drawable.LayerDrawable;

public class Gallery extends AppCompatActivity {
    private GridView gridView;
    private List<String> imagePaths = new ArrayList<>();
    private List<String> originalPaths = new ArrayList<>();
    private ImageButton btnBackCamera, btnFTP, btnDelete;
    private List<Integer> selectedPositions = new ArrayList<>();
    private TextView textView;
    private ImageAdapter adapter;

    private String texto;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);
        btnBackCamera = findViewById(R.id.btnBackCamera);
        btnFTP = findViewById(R.id.btnFTP);
        btnDelete = findViewById(R.id.btnDelete);
        textView = findViewById(R.id.numeroRx); // Reemplaza "text_view_id" con el ID de tu TextView

        getImagesFiles();

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

                        if (imagePaths.isEmpty()){
                            // Crear un objeto ShapeDrawable para el color de fondo
                            ShapeDrawable shapeDrawable = new ShapeDrawable();
                            shapeDrawable.setShape(new RectShape());
                            shapeDrawable.getPaint().setColor(Color.parseColor("#ECEFF1"));

                            Drawable imageDrawable = getResources().getDrawable(R.drawable.image_missing_svgrepo_com_150x150);



                            // Crear un objeto LayerDrawable para combinar el color de fondo y el texto
                            Drawable[] layers = new Drawable[2];
                            layers[0] = shapeDrawable;
                            layers[1] = imageDrawable;
                            LayerDrawable layerDrawable = new LayerDrawable(layers);
                            layerDrawable.setLayerGravity(1, Gravity.CENTER);

                            // Establecer el objeto LayerDrawable como fondo del GridView
                            gridView.setBackground(layerDrawable);
                        }
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
            }
            else {
                // Si el elemento no está seleccionado, lo seleccionamos y lo marcamos como seleccionado
                selectedPositions.add(position);
                // Guardar imagen original del view
                Drawable originalImage = ((ImageView) view).getDrawable();
                view.setTag(originalImage);
                // Establecer borde de selección
                Resources res = getResources();
                Drawable borderDrawable = res.getDrawable(R.drawable.selected_item_border);
                Drawable[] layers = new Drawable[2];
                layers[0] = originalImage;
                layers[1] = borderDrawable;
                LayerDrawable layerDrawable = new LayerDrawable(layers);
                ((ImageView) view).setImageDrawable(layerDrawable);
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
                                final ProgressDialog[] progressDialog = new ProgressDialog[1];
                                Activity activity = Gallery.this;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog[0] = new ProgressDialog(Gallery.this);
                                        progressDialog[0].setMessage("Cargando archivo...");
                                        //progressDialog[0].setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        progressDialog[0].setIndeterminate(true);
                                        progressDialog[0].setMax(100);
                                        progressDialog[0].setCancelable(false);
                                        progressDialog[0].show();
                                    }
                                });

                                long totalBytes = 0;
                                for (String imagePath : imagePaths) {
                                    try {
                                        totalBytes += new File(imagePath).length();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                final long[] bytesReadTotal = {0};
                                final int updateThreshold = (int) (totalBytes * 0.01); // Update progress every 1% of the total bytes
                                final int[] progress = {0};
                                for (String imagePath : imagePaths) {
                                    long finalTotalBytes = totalBytes;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            InputStream inputStream = null;
                                            try {
                                                inputStream = new BufferedInputStream(new FileInputStream(imagePath));
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }

                                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                            byte[] buffer = new byte[1024];
                                            int bytesRead;

                                            while (true) {
                                                try {
                                                    if (!((bytesRead = inputStream.read(buffer)) != -1))
                                                        break;
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                outputStream.write(buffer, 0, bytesRead);
                                                bytesReadTotal[0] += bytesRead;

                                                if (bytesReadTotal[0] >= updateThreshold) { // Update progress only when enough bytes have been read
                                                    progress[0] = (int) ((bytesReadTotal[0] * 100) / finalTotalBytes);
                                                    activity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog[0].setProgress(progress[0]);
                                                            progressDialog[0].setMessage("Cargando archivo: " + imagePath);
                                                        }
                                                    });
                                                    bytesReadTotal[0] = 0; // Reset the byte count
                                                }
                                            }

                                            // Realiza la operación de red en un hilo separado utilizando AsyncTask
                                            new AsyncTask<String, Void, Boolean>() {
                                                @Override
                                                protected Boolean doInBackground(String... params) {
                                                    return new FtpUpload(Gallery.this).uploadFileToFTP(params[0], Gallery.this);
                                                }

                                                @Override
                                                protected void onPostExecute(Boolean result) {
                                                    if (result == true) {
                                                        deleteImageFile(imagePath);
                                                        imagePaths.remove(imagePath);
                                                        progressDialog[0].dismiss();
                                                        activity.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                gridView.invalidateViews();
                                                            }
                                                        });
                                                    }else{
                                                        progressDialog[0].dismiss();
                                                    }
                                                }
                                            }.execute(imagePath);
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
    private void getImagesFiles() {
        // Obtener las rutas de las imágenes
        File directory = new File(Environment.getExternalStorageDirectory() + "/Pictures/");
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String filePath = file.getAbsolutePath();
                if (isImageEncrypted(filePath)) {
                    // Display a black image
                    String data = decryptImage(filePath);
                    imagePaths.add(data);
                } else {
                    imagePaths.add(filePath);
                    originalPaths.add(filePath);
                }
            }
        }

        int numFotos = imagePaths.size();
        texto = "Número de Rx: " + numFotos;
        textView.setText(texto);
        // Ordenar la lista de forma decreciente
        Collections.sort(imagePaths, Collections.reverseOrder());
        // Crear un adaptador para el GridView
        if(imagePaths.isEmpty()){
            // Crear un objeto ShapeDrawable para el color de fondo
            ShapeDrawable shapeDrawable = new ShapeDrawable();
            shapeDrawable.setShape(new RectShape());
            shapeDrawable.getPaint().setColor(Color.parseColor("#ECEFF1"));

            Drawable imageDrawable = getResources().getDrawable(R.drawable.image_missing_svgrepo_com_150x150);



            // Crear un objeto LayerDrawable para combinar el color de fondo y el texto
            Drawable[] layers = new Drawable[2];
            layers[0] = shapeDrawable;
            layers[1] = imageDrawable;
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            layerDrawable.setLayerGravity(1, Gravity.CENTER);

            // Establecer el objeto LayerDrawable como fondo del GridView
            gridView.setBackground(layerDrawable);



        }else {
            adapter = new ImageAdapter(Gallery.this, imagePaths);
            gridView.setAdapter(adapter);
        }
    }
    private boolean isImageEncrypted(String filePath) {
        return filePath.endsWith("_enc.jpg");
    }
    private String decryptImage(String filePath) {
        try {
            // Decrypt the image using AES encryption
            byte[] key = "mySecretKey12345".getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            File encryptedFile = new File(filePath);
            FileInputStream inputStream = new FileInputStream(encryptedFile);
            byte[] encryptedBytes = new byte[(int) encryptedFile.length()];
            inputStream.read(encryptedBytes);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            File decryptedFile = new File(getCacheDir(), encryptedFile.getName());
            FileOutputStream outputStream = new FileOutputStream(decryptedFile);
            outputStream.write(decryptedBytes);
            outputStream.flush();
            outputStream.close();

            return decryptedFile.getAbsolutePath();

        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            Log.e("Gallery", "Error decrypting image: " + filePath, e);
            Toast.makeText(this, "Error decrypting image: " + filePath, Toast.LENGTH_SHORT).show();
            return filePath;
        }
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

        if(file.exists()){
            boolean isEncrypted = isImageEncrypted(imagePath);
            if (isEncrypted) {
                String fileName = new File(imagePath).getName();
                // Si la imagen está encriptada, eliminar la versión encriptada
                String encryptedImagePath = imagePath.replace(".jpg", "_enc.jpg");
                String URL =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/" + fileName;
                File encryptedFile = new File(URL);

                if (encryptedFile.exists()) {
                    boolean success = encryptedFile.delete();
                    if (success) {

                    } else {

                    }
                } else {

                }
            } else {
                // Si la imagen no está encriptada, eliminar la versión original
                if (file.exists()) {
                    boolean success = file.delete();
                    if (success) {

                    } else {
                      ;
                    }
                } else {

                }
            }
        }
    }
}