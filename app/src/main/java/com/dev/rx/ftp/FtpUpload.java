package com.dev.rx.ftp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpUpload {

    private Context mContext;

    public FtpUpload(Context context) {
        mContext = context;
    }

    public void uploadFileToFTP(String filePath, Context context) {
        FTPClient ftpClient = new FTPClient();
        String name = null;
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1); // Obtiene "Rx_IMG_20230403_080801.jpg"
        if (fileName.indexOf("_") >= 0) {
            name = fileName.substring(fileName.indexOf("_") + 1); // Obtiene "IMG_20230403_080801.jpg"
        } else {
            // Manejar el caso en el que el nombre de archivo no tiene el formato esperado
        }

        try {
            // Establecer la conexión con el servidor FTP
            ftpClient.connect("181.188.248.23", 21);
            ftpClient.login("COSTA", "ii5D4XGYcoXzB9EF");

            //opten direccion de fichero ftp de cache
            SharedPreferences prefs = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE);
            String ftp = prefs.getString("ftp", ""); // el segundo parámetro es un valor predeterminado si no se encuentra la clave en las SharedPreferences
            ftpClient.makeDirectory("/COSTA/"+ftp);
            // Cambiar al directorio donde se van a subir los archivos
            ftpClient.changeWorkingDirectory("/COSTA/"+ftp);

            //tiempo
            ftpClient.setSoTimeout(10000);

            // Configurar el modo de transferencia de archivos
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Abrir un stream de entrada para el archivo que se va a subir
            FileInputStream inputStream = new FileInputStream(new File(filePath));

            // Subir el archivo al servidor FTP
            ftpClient.storeFile(name, inputStream);
            // Mostrar un Toast en el hilo principal de la aplicación
            Activity activity = (Activity) mContext;
            String finalName = name;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Archivo: " + finalName + " cargado exitosamente", Toast.LENGTH_SHORT).show();
                }
            });



            // Cerrar el stream de entrada y la conexión FTP
            inputStream.close();
            ftpClient.logout();
            ftpClient.disconnect();

        }
        catch (IOException e) {
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Error al subir el archivo!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }
}
