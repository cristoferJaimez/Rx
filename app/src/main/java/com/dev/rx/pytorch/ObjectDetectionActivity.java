package com.dev.rx.pytorch;

import static android.graphics.Color.GREEN;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.exifinterface.media.ExifInterface;

import com.dev.rx.config.Config;
import com.dev.rx.gallery.Gallery;
import com.dev.rx.R;
import com.dev.rx.gallery.ViewPicture;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ObjectDetectionActivity extends AbstractCameraXActivity<ObjectDetectionActivity.AnalysisResult> {
    private Module mModule = null;

    private ResultView mResultView;

    private ImageButton imageButton, galleryButton, configButton;

    private Bitmap bitmap ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageButton = findViewById(R.id.btnTakePicture);

        //images front notifications
        ImageView imageView = findViewById(R.id.imageViewFront);
        ImageView imageFtp = findViewById(R.id.imageFTPFront);
        ImageView imageEncryt = findViewById(R.id.imageEncrytFront);

        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean switchState1 = prefs.getBoolean("switch1_state", false);
        boolean switchState2 = prefs.getBoolean("switch2_state", false);
        boolean switchState3 = prefs.getBoolean("switch3_state", false);

        if(switchState1 == true) {
            // Establece el Drawable en la ImageView
            imageView.setBackgroundColor(GREEN);
        }
        if(switchState2 == true) {
            // Establece el Drawable en la ImageView
            imageFtp.setBackgroundColor(GREEN);
        }
        if(switchState3 == true) {
            // Establece el Drawable en la ImageView
            imageEncryt.setBackgroundColor(GREEN);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bitmap resultBitmap = mResultView.getResultBitmap();
                    if (resultBitmap != null) {
                        try {
                            int orientation = getOrientation(bitmap); // Obtener orientación de la imagen original
                            Toast.makeText(ObjectDetectionActivity.this, "Angulo: " + orientation, Toast.LENGTH_SHORT).show();
                            Bitmap resizedResultBitmap = Bitmap.createScaledBitmap(resultBitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                            Bitmap mixedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                            Canvas canvas = new Canvas(mixedBitmap);
                            Matrix matrix = new Matrix();
                            float scaleX = (float)canvas.getWidth() / bitmap.getWidth();
                            float scaleY = (float)canvas.getHeight() / bitmap.getHeight();
                            float scale = Math.min(scaleX, scaleY);
                            matrix.setScale(scale, scale);
                            matrix.postTranslate(
                                    (canvas.getWidth() - bitmap.getWidth() * scale) / 2f,
                                    (canvas.getHeight() - bitmap.getHeight() * scale) / 2f
                            );
                            canvas.drawBitmap(bitmap, matrix, null);
                            matrix.postRotate(orientation); // Rotar la imagen mezclada según la orientación de la original
                            canvas.drawBitmap(resizedResultBitmap, matrix, null);
                            // Reducir la calidad de la imagen
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            mixedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // 50 es el nivel de calidad, puedes ajustarlo según tus necesidades
                            byte[] byteArray = outputStream.toByteArray();

                            final Intent intent = new Intent(ObjectDetectionActivity.this, ViewPicture.class);
                            intent.putExtra("mixedBitmap", byteArray);
                            // Inicia la actividad ViewPicture
                            startActivity(intent);

                        } catch (NullPointerException e) {
                            // Handle the case where a NullPointerException is thrown
                            e.printStackTrace();
                        }
                    }
                }

        });


        galleryButton = findViewById(R.id.btnGallery);

        galleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ObjectDetectionActivity.this, Gallery.class);
                startActivity(intent);
            }
        });
        configButton = findViewById(R.id.btnConfig);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Código que se ejecutará en el hilo secundario
                        Intent intent = new Intent(ObjectDetectionActivity.this, Config.class);
                        startActivity(intent);
                    }
                });
                thread.start();
            }
        });
    }


    static class AnalysisResult {
        private final ArrayList<Result> mResults;

        public AnalysisResult(ArrayList<Result> results) {
            mResults = results;
        }
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_object_detection;
    }

    @Override
    protected TextureView getCameraPreviewTextureView() {
        mResultView = findViewById(R.id.resultView);
        return ((ViewStub) findViewById(R.id.object_detection_texture_view_stub))
                .inflate()
                .findViewById(R.id.object_detection_texture_view);
    }

    @Override
    protected void applyToUiAnalyzeImageResult(AnalysisResult result) {
        mResultView.setResults(result.mResults);
        mResultView.invalidate();


    }

    private Bitmap imgToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    @Override
    @WorkerThread
    @Nullable
    protected AnalysisResult analyzeImage(ImageProxy image, int rotationDegrees) {

        try {
            if (mModule == null) {
                mModule = LiteModuleLoader.load(MainActivity.assetFilePath(getApplicationContext(), "best.torchscript_opt.ptl"));
            }
        } catch (IOException e) {
            Log.e("Object Detection", "Error reading assets", e);
            return null;
        }
        bitmap = imgToBitmap(image.getImage());
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);

        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, PrePostProcessor.NO_MEAN_RGB, PrePostProcessor.NO_STD_RGB);
        IValue[] outputTuple = mModule.forward(IValue.from(inputTensor)).toTuple();
        final Tensor outputTensor = outputTuple[0].toTensor();
        final float[] outputs = outputTensor.getDataAsFloatArray();

        float imgScaleX = (float)bitmap.getWidth() / PrePostProcessor.mInputWidth;
        float imgScaleY = (float)bitmap.getHeight() / PrePostProcessor.mInputHeight;
        float ivScaleX = (float)mResultView.getWidth() / bitmap.getWidth();
        float ivScaleY = (float)mResultView.getHeight() / bitmap.getHeight();

        final ArrayList<Result> results = PrePostProcessor.outputsToNMSPredictions(outputs, imgScaleX, imgScaleY, ivScaleX, ivScaleY, 0, 0);



        return new AnalysisResult(results);
    }





    // This method returns a Bitmap of the screen capture
    private Bitmap getScreenCapture(View view) {
        Bitmap screenCapture = null;

        try {
            // Create a bitmap the size of the screen
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            screenCapture = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            // Create a canvas with the bitmap
            Canvas canvas = new Canvas(screenCapture);

            // Get the background of the view and draw it to the canvas
            Drawable background = view.getBackground();
            if (background != null) {
                background.draw(canvas);
            } else {
                canvas.drawColor(Color.WHITE);
            }

            // Draw the view to the canvas
            view.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return screenCapture;
    }

    private int getOrientation(Bitmap bitmap) {
        try {
            ExifInterface exifInterface = new ExifInterface(new ByteArrayInputStream(getBytesFromBitmap(bitmap)));
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }



}
