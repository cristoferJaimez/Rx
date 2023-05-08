package com.dev.rx.ftp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

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
        }
        else if (isImageEncrypted(imagePaths.toString())) {
            File tempFile = null; // declarar la variable fuera del bucle

            for (String imagePath : imagePaths) {
                if (isImageEncrypted(imagePath)) {
                    // Obtener bytes encriptados del archivo
                    byte[] encryptedBytes = null;
                    try {
                        FileInputStream inputStream = new FileInputStream(imagePath);
                        encryptedBytes = new byte[inputStream.available()];
                        inputStream.read(encryptedBytes);
                        inputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Desencriptar bytes
                    byte[] decryptedBytes = null;
                    try {
                        decryptedBytes = decryptImage(encryptedBytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tempFile = null;
                    // Obtener nombre y ruta del archivo original
                    File originalFile = new File(imagePath);
                    String originalFileName = originalFile.getName();
                    String originalFilePath = originalFile.getParent();
                    // Crear archivo temporal con la misma ruta y nombre del archivo original
                    try {
                        tempFile = File.createTempFile(originalFileName.split("\\.")[0], ".jpg", context.getCacheDir());
                        FileOutputStream outputStream = new FileOutputStream(tempFile);
                        outputStream.write(decryptedBytes);
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Verificar si el archivo temporal existe antes de subirlo al servidor FTP
                    if (tempFile != null && tempFile.exists()) {
                        File finalTempFile = tempFile;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Archivo temporal", "Se ha creado correctamente: " + finalTempFile.getAbsolutePath());
                              boolean  res = new FtpUpload(context).uploadFileToFTP(finalTempFile.getAbsolutePath(), context);
                               if(res == true){
                                   // Eliminar la imagen encriptada una vez subida al FTP
                                   deleteImageFile(imagePath);
                                   // Eliminar la ruta de la imagen de la lista de rutas
                                   imagePaths.remove(imagePath);
                                   // Eliminar archivo temporal después de subirlo al servidor FTP
                                   finalTempFile.delete();
                               }else{}
                            }
                        }).start();
                    }
                }
            }


        }
        else {
            for (String imagePath : imagePaths) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Subir el archivo al servidor FTP
                          boolean res =  new FtpUpload(context).uploadFileToFTP(imagePath, context);
                          if(res == true){
                              // Eliminar la imagen una vez subida al FTP
                              deleteImageFile(imagePath);

                              // Eliminar la ruta de la imagen de la lista de rutas
                              imagePaths.remove(imagePath);

                              // Actualizar la vista de la galería
                          }else{}


                        }
                    }).start();

            }
        }
    }

    public boolean isImageEncrypted(String imagePath) {
        File file = new File(imagePath);
        String fileName = file.getName();
        return fileName.contains("_enc");
    }
    public byte[] decryptImage(byte[] encryptedBytes) throws Exception {
        // Decrypt the image using AES decryption with the pre-defined key
        byte[] key = "mySecretKey12345".getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return decryptedBytes;
    }

    public void deleteImageFile(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
