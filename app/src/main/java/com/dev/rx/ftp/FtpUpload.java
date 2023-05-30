package com.dev.rx.ftp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

public class FtpUpload {
    private Context mContext;
    private boolean isUpload = false;
    public FtpUpload(Context context) {
        mContext = context;
    }
    public boolean uploadFileToFTP(String filePath, Context context) {
        // Recuperar las preferencias compartidas
        SharedPreferences prefs = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE);
        // Obtener el valor actual de la clave "ftp" (usando una cadena vacía como valor predeterminado si no se encuentra la clave)
        String ftp = prefs.getString("ftp", "");
        Activity activity = (Activity) mContext;
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
            ftpClient.connect("190.145.95.242", 21);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
                throw new IOException("No se ha podido conectar al servidor FTP");
            }

            // Iniciar sesión en el servidor FTP
            if (!ftpClient.login("ftp_app","SddS")) {
                ftpClient.disconnect();
                if (ftpClient.getReplyCode() == 530) {
                    throw new IOException("Inicio de sesión fallido: usuario o contraseña incorrectos");
                } else {
                    throw new IOException("Inicio de sesión fallido");
                }
            }

            // Verificar el puerto
            int replyCode = ftpClient.sendCommand("PASV");
            if (replyCode != 227) {
                ftpClient.disconnect();
                throw new IOException("No se puede conectar al servidor FTP: puerto incorrecto");
            }

            //semana actual
            // Obtener la fecha actual
            Calendar calendar = Calendar.getInstance();

            // Obtener el número de la semana actual
            int semanaActual = calendar.get(Calendar.WEEK_OF_YEAR);

            // Especifica la ruta de la carpeta remota a crear en el servidor FTP
            String remoteDirPath = "/App/" + ftp + "semana_"+ semanaActual+"/";
            if (remoteDirPath.contains("S/N")) {
                remoteDirPath = remoteDirPath.replace("S/N/", "");
            }

            // Verificar si la carpeta ya existe en el servidor FTP
            boolean directoryExists = false;
            FTPFile[] remoteDirectories = ftpClient.listDirectories(remoteDirPath);
            for (FTPFile remoteDirectory : remoteDirectories) {
                if (remoteDirectory.isDirectory() && remoteDirectory.getName().equals(remoteDirPath)) {
                    directoryExists = true;
                    break;
                }
            }

            // Si la carpeta no existe, crearla en el servidor FTP
            if (!directoryExists) {
                if (!ftpClient.makeDirectory(remoteDirPath)) {
                    //throw new IOException("No se pudo crear la carpeta en el servidor FTP");
                    //Toast.makeText(activity, "Carpeta Encontrada", Toast.LENGTH_SHORT).show();
                }else{
                    ftpClient.makeDirectory(remoteDirPath);
                }
            }

            ftpClient.changeWorkingDirectory(remoteDirPath);

            //tiempo
            ftpClient.setSoTimeout(10000);
            // Configurar el modo de transferencia de archivos.
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // Abrir un stream de entrada para el archivo que se va a subir
            FileInputStream inputStream = new FileInputStream(new File(filePath));
            // Subir el archivo al servidor FTP
            ftpClient.storeFile(name, inputStream);
            // Mostrar un Toast en el hilo principal de la aplicación
            String finalName = name;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Archivo: " + finalName + " cargado exitosamente", Toast.LENGTH_SHORT).show();

                }
            });
            isUpload = true;
            // Cerrar el stream de entrada y la conexión FTP
            inputStream.close();
            ftpClient.logout();
            ftpClient.disconnect();

        }
        catch (IOException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Error al subir el archivo!" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
            isUpload = false;
            e.printStackTrace();
        }
        return isUpload;
    }


}

