package com.dev.rx.ftp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dev.rx.db.Mysql;

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
    private int contador = 0;
    private int contadorInicial = 0;
    public void ftpAuto(Context context) {
        // Recuperar las preferencias compartidas
        SharedPreferences prefs = context.getSharedPreferences("myPrefs", MODE_PRIVATE);
        int idF = prefs.getInt("fkPharma", 0);

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
        } else if (isImageEncrypted(imagePaths.toString())) {
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

                    // Verificar si el archivo temporal existe antes de subirlo al servidor S3
                    if (tempFile != null && tempFile.exists()) {
                        File finalTempFile = tempFile;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new AsyncTask<String, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(String... params) {
                                        S3Upload s3Upload = new S3Upload(context);
                                        S3Upload.UploadCallback callback = new S3Upload.UploadCallback() {
                                            @Override
                                            public void onUploadComplete(boolean isSuccess) {
                                                // Manejar la respuesta de carga (true o false) aquí
                                                if (isSuccess) {
                                                    deleteImageFile(imagePath);
                                                    imagePaths.remove(imagePath);
                                                    contador++; // Incrementar el contador

                                                    Log.d("contadorInit", String.valueOf(contador));
                                                }
                                            }
                                        };

                                        s3Upload.setUploadCallback(callback);
                                        s3Upload.uploadFileToS3(finalTempFile.getAbsolutePath());

                                        // Devolver null aquí, ya que la respuesta se manejará en el método onUploadComplete
                                        return null;
                                    }

                                    private void onPostExecute() {
                                        Log.d("contador", String.valueOf(contador));
                                        // Actualizar el contador solo una vez que se hayan cargado todos los archivos
                                        if (contador > contadorInicial) {
                                            contadorInicial = contador; // Actualizar el contador inicial
                                            //textView.setText("Número de Rx: " + contador);
                                            new Mysql().enviarContador(context, idF, contador);
                                        }



                                    }
                                }.execute(imagePath);
                            }

                        }).start();
                    }
                }
            }
        } else {
            for (String imagePath : imagePaths) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new AsyncTask<String, Void, Void>() {
                            @Override
                            protected Void doInBackground(String... params) {
                                S3Upload s3Upload = new S3Upload(context);
                                S3Upload.UploadCallback callback = new S3Upload.UploadCallback() {
                                    @Override
                                    public void onUploadComplete(boolean isSuccess) {
                                        // Manejar la respuesta de carga (true o false) aquí
                                        if (isSuccess) {
                                            deleteImageFile(imagePath);
                                            imagePaths.remove(imagePath);
                                            contador++; // Incrementar el contador

                                            Log.d("contadorInit", String.valueOf(contador));
                                        }
                                    }
                                };

                                s3Upload.setUploadCallback(callback);
                                s3Upload.uploadFileToS3(params[0]);

                                // Devolver null aquí, ya que la respuesta se manejará en el método onUploadComplete
                                return null;
                            }

                            private void onPostExecute() {
                                Log.d("contador", String.valueOf(contador));
                                // Actualizar el contador solo una vez que se hayan cargado todos los archivos
                                if (contador > contadorInicial) {
                                    contadorInicial = contador; // Actualizar el contador inicial

                                    new Mysql().enviarContador(context, idF, contador);
                                }
                            }
                        }.execute(imagePath);
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