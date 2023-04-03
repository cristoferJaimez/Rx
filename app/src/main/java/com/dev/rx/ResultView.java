package com.dev.rx;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mResults == null) return;
        for (Result result : mResults) {
            System.out.println(result.classIndex);
            if ( result.classIndex != 4 ) {

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
                Bitmap scaledBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

                // Crear objeto BitmapShader con el objeto Bitmap redimensionado
                BitmapShader shader = new BitmapShader(scaledBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

                // Establecer el objeto BitmapShader como shader del Paint
                mPaintRectangle.setShader(shader);
                // Reducir el tamaño de la caja dibujada en un porcentaje
                //float reductionPercentage = 0.99f; // 20% de reducción
                //int reducedWidth = (int) (result.rect.width() * (1 - reductionPercentage));
                //int reducedHeight = (int) (result.rect.height() * (1 - reductionPercentage));
                //int widthDifference = result.rect.width() - reducedWidth;
                //int heightDifference = result.rect.height() - reducedHeight;
                //RectF reducedRect = new RectF(result.rect.left + (widthDifference / 2), result.rect.top + (heightDifference / 2), result.rect.right - (widthDifference / 2), result.rect.bottom - (heightDifference / 2));

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