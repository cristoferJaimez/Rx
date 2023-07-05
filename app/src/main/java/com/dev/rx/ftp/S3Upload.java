package com.dev.rx.ftp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.util.Calendar;

public class S3Upload {
    private Context mContext;
    private UploadCallback mCallback;
    private ProgressDialog progressDialog;
    private TransferObserver uploadObserver;
    private TransferUtility transferUtility;
    private boolean isUploadCompleted = false;

    public S3Upload(Context context) {
        mContext = context;
    }

    public void setUploadCallback(UploadCallback callback) {
        mCallback = callback;
    }

    public void uploadFileToS3(String filePath) {
        // Show the ProgressDialog
        Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Cargando archivo...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setProgress(0);
                progressDialog.setCancelable(false);

                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                        // Cancel the file upload
                        if (uploadObserver != null) {
                            transferUtility.cancel(uploadObserver.getId());
                        }
                    }
                });

                progressDialog.show();
            }
        });

        SharedPreferences prefs = mContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        int idF = prefs.getInt("fkPharma", 0);
        String bucketName = prefs.getString("bucketName", "panels3");
        String ftp = prefs.getString("ftp", "");
        String accessKey = ""; // Replace with your AWS access credentials
        String secretKey = ""; // Replace with your AWS secret credentials
        Calendar calendar = Calendar.getInstance();
        int semanaActual = calendar.get(Calendar.WEEK_OF_YEAR);
        int annoActual = calendar.get(Calendar.YEAR);

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = new AmazonS3Client(credentials);

        transferUtility = TransferUtility.builder()
                .context(mContext)
                .s3Client(s3Client)
                .build();

        if (ftp.contains("/S/N")) {
            ftp = ftp.replace("/S/N", "");
        }

        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String s3Key = "app/" + ftp + "semana_" + semanaActual + "_" + annoActual + "/" + fileName;

        File file = new File(filePath);

        uploadObserver = transferUtility.upload(bucketName, s3Key, file);

        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    handleUploadCompleted(fileName);
                } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                    handleUploadFailed();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int progress = (int) (bytesCurrent * 100 / bytesTotal);

                // Update the progress on the UI thread
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setProgress(progress);
                    }
                });
            }

            @Override
            public void onError(int id, Exception ex) {
                handleUploadError(ex);
            }
        });
    }

    private void handleUploadCompleted(String fileName) {
        Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "Archivo: " + fileName + " cargado exitosamente", Toast.LENGTH_SHORT).show();

            }
        });

        if (mCallback != null) {
            mCallback.onUploadComplete(true);
        }

        isUploadCompleted = true;
        setButtonAccept();

        // Cierra la ventana de diálogo
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                isUploadCompleted = true;
                e.printStackTrace();
                // Aquí puedes agregar el manejo de la excepción, como mostrar un mensaje de error o realizar alguna otra acción apropiada.
            }
        }
    }

    private void handleUploadFailed() {
        Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.err.println("Error al subir el archivo a Amazon S3");
                Toast.makeText(mContext, "Error al subir el archivo a Amazon S3", Toast.LENGTH_SHORT).show();
            }
        });

        if (mCallback != null) {
            mCallback.onUploadComplete(false);
        }
    }

    private void handleUploadError(Exception ex) {
        Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.err.println("Error al subir el archivo a Amazon S3: " + ex.getMessage());
                Log.d("errS3", ex.getMessage());
                Toast.makeText(mContext, "Error al subir el archivo a Amazon S3: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (mCallback != null) {
            mCallback.onUploadComplete(false);
        }
        ex.printStackTrace();
    }

    private void setButtonAccept() {
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
    }

    public interface UploadCallback {
        void onUploadComplete(boolean isSuccess);
    }
}

