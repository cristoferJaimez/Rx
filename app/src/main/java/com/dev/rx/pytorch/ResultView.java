package com.dev.rx.pytorch;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class ResultView extends View {

    private final static int TEXT_X = 12;
    private final static int TEXT_Y = 10;
    private final static int TEXT_WIDTH = 100;
    private final static int TEXT_HEIGHT = 10;

    private Paint mPaintRectangle;
    private Paint mPaintText;
    private ArrayList<Result> mResults;

    public ResultView(Context context) {
        super(context);
    }

    public ResultView(Context context, AttributeSet attrs){
        super(context, attrs);
        mPaintRectangle = new Paint();
        mPaintRectangle.setColor(Color.RED);
        mPaintText = new Paint();
    }

    private int mViewOrientation = Configuration.ORIENTATION_PORTRAIT; // Almacena la orientación de la vista original

    @Override
   /*
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mResults == null) return;
        for (Result result : mResults) {
            if (result.classIndex != 4) {
                // Cargar la imagen desde el archivo en assets
                Bitmap bmp = null;
                try {
                    InputStream inputStream = getContext().getAssets().open("cinta.jpg");
                    bmp = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Calcular la escala necesaria para que la imagen ocupe todo el ancho de la caja
                float scaleX = (float) result.rect.width() / (bmp.getWidth());
                float scaleY = (float) result.rect.height() / (bmp.getHeight());

                // Calcular la escala más pequeña para mantener la proporción original
                float scale = Math.min(scaleX, scaleY);

                // Redimensionar la imagen en proporción
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);

                // Rotar la imagen redimensionada según el ángulo de rotación del resultado
                matrix.postRotate(90f, bmp.getWidth() / 2f, bmp.getHeight() / 2f);

                Bitmap scaledBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

                // Crear objeto BitmapShader con el objeto Bitmap redimensionado
                BitmapShader shader = new BitmapShader(scaledBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

                // Establecer el objeto BitmapShader como shader del Paint
                mPaintRectangle.setShader(shader);

                canvas.drawRect(result.rect, mPaintRectangle);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mResults == null) return;
        for (Result result : mResults) {
            if (result.classIndex != 4) {
                // Cargar la imagen desde el archivo en assets
                Bitmap bmp = null;
                try {
                    InputStream inputStream = getContext().getAssets().open("cinta.jpg");
                    bmp = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Calcular la escala necesaria para que la imagen ocupe todo el ancho de la caja
                float scaleX = (float) result.rect.width() / (bmp.getWidth());
                float scaleY = (float) result.rect.height() / (bmp.getHeight());

                // Calcular la escala más pequeña para mantener la proporción original
                float scale = Math.min(scaleX, scaleY);

                // Redimensionar la imagen en proporción
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);

                // Aplicar la rotación correspondiente a la matriz de transformación
                if (mViewOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    matrix.postRotate(90f, bmp.getWidth() / 2f, bmp.getHeight() / 2f);
                }

                Bitmap scaledBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

                // Crear objeto BitmapShader con el objeto Bitmap redimensionado
                BitmapShader shader = new BitmapShader(scaledBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

                // Establecer el objeto BitmapShader como shader del Paint
                mPaintRectangle.setShader(shader);

                canvas.drawRect(result.rect, mPaintRectangle);
            }
        }
    }
    */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mResults == null) return;
        for (Result result : mResults) {
            if (result.classIndex != 4) {
                // Cargar la imagen desde el archivo en assets
                Bitmap bmp = null;
                // Definir la escala a utilizar
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 0; // Escala de 1/3 (30%)
                try {
                    InputStream inputStream = getContext().getAssets().open("cinta.jpg");
                    bmp = BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Calcular la escala necesaria para que la imagen ocupe todo el ancho de la caja
                float scaleX = (float) result.rect.width() / (bmp.getWidth());
                float scaleY = (float) result.rect.height() / (bmp.getHeight());

                // Verificar si la orientación de la imagen original y la orientación del rectángulo son diferentes
                boolean rotate = result.orientation == 90 || result.orientation == 270;
                if (rotate) {
                    // Rotar la imagen original
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(result.orientation, bmp.getWidth() / 2f, bmp.getHeight() / 2f);
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), rotateMatrix, true);
                }

                // Calcular la escala más pequeña para mantener la proporción original
                float scale = Math.min(scaleX, scaleY);

                // Redimensionar la imagen en proporción
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);

                if (rotate) {
                    // Si la imagen original ha sido rotada, rotar también la imagen redimensionada
                    matrix.postRotate(result.orientation, bmp.getWidth() / 2f, bmp.getHeight() / 2f);
                }

                Bitmap scaledBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

                // Crear objeto BitmapShader con el objeto Bitmap redimensionado
                BitmapShader shader = new BitmapShader(scaledBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

                // Establecer el objeto BitmapShader como shader del Paint
                mPaintRectangle.setShader(shader);

                canvas.drawRect(result.rect, mPaintRectangle);
            }
        }
    }




    public void setResults(ArrayList<Result> results) {
        mResults = results;
    }

    public Bitmap getResultBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }



    // Método para cargar una imagen desde la carpeta assets
    private Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(inputStream);
    }
}