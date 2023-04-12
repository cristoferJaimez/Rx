package com.dev.rx.ftp;

import android.content.Context;
import android.os.Environment;
import android.widget.GridView;
import android.widget.Toast;

import com.dev.rx.R;
import com.dev.rx.gallery.Gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FtpAuto {
    private List<String> imagePaths = new ArrayList<>();

    public void ftpAuto(Context context) {
        // Obtener las rutas de las imágenes
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    imagePaths.add(file.getAbsolutePath());
                }
            }
        }

        if (imagePaths.isEmpty()) {
            Toast.makeText(context, "No hay imágenes para subir", Toast.LENGTH_SHORT).show();
        } else {
            for (String imagePath : imagePaths) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Subir el archivo al servidor FTP
                            new FtpUpload(context).uploadFileToFTP(imagePath, context);

                            // Eliminar la imagen una vez subida al FTP
                            deleteImageFile(imagePath);

                            // Eliminar la ruta de la imagen de la lista de rutas
                            imagePaths.remove(imagePath);

                            // Actualizar la vista de la galería

                        }
                    }).start();

            }
        }




    }
    public void deleteImageFile(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
