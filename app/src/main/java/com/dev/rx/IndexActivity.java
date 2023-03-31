package com.dev.rx;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // Obtiene la referencia al ImageView del logo
        ImageView logoImageView = findViewById(R.id.logoImageView);

// Obtiene las dimensiones de la pantalla
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

// Crea una instancia de ValueAnimator y establece los valores iniciales y finales de la propiedad
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

// Establece la duración de la animación
        animator.setDuration(5000);

// Crea una instancia de OvershootInterpolator para generar una función de rebote
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator();

// Establece la acción que se ejecutará en cada fotograma de la animación
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // Obtiene el valor actual de la propiedad de la animación
                float animatedValue = (float) valueAnimator.getAnimatedValue();

                // Calcula la nueva posición del logo en función del valor de la animación
                float x = screenWidth * 0.5f;
                float y = screenHeight * 0.5f;

                // Utiliza una función de rebote para calcular la posición del logo
                float progress = overshootInterpolator.getInterpolation(animatedValue);
                float translationY = (float) (100 * Math.sin(progress * Math.PI * 2));

                // Establece la posición del logo en función de la animación
                logoImageView.setX(x - logoImageView.getWidth() * 0.5f);
                logoImageView.setY(y - logoImageView.getHeight() * 0.5f + translationY);
            }
        });

// Repite la animación para hacer que el logo rebote varias veces
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

// Inicia la animación
        animator.start();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IndexActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000); // espera 5 segundos (5000 milisegundos) antes de iniciar la SegundaActivity

    }
}