package com.dev.rx.estadistica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dev.rx.R;
import com.dev.rx.db.Mysql;
import com.dev.rx.gallery.Gallery;
import com.dev.rx.obser.CacheManager;
import com.dev.rx.obser.CacheObserver;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Estadistica extends AppCompatActivity  implements CacheObserver {


    private TextView textDia, textSemana, textMes, textBalance;
    private ImageButton btn_back_gallery, btnReload;
    private CacheManager cacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistica);


        textDia = findViewById(R.id.textDia);
        textSemana = findViewById(R.id.textSemana);
        textMes = findViewById(R.id.textMes);
        btn_back_gallery = findViewById(R.id.btn_esta_gallery);

        textBalance = findViewById(R.id.textBalance);

        cacheManager = CacheManager.getInstance(this);
        cacheManager.registerObserver(this);
        cacheManager.notifyObserverWithCachedData(this);



        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        // Recuperar los valores de SharedPreferences
        int sumaDia = prefs.getInt("sumaDia", 0);
        int sumaSemana = prefs.getInt("sumaSemana", 0);
        int sumaMes = prefs.getInt("sumaMes", 0);
        String actualMes = prefs.getString("mes_actual", "S/N");
        String actualSemana = prefs.getString("semana_actual", "S/N");
        String actualdia = prefs.getString("dia_actual", "S/N");
        String dom_sab = prefs.getString("registro_semana", "S/N");

        textDia.setText(" Rx del Día " + actualdia + ":\n" + sumaDia + "");
        textSemana.setText(" Rx de la semana #" + actualSemana + ":\n" + sumaSemana + "");
        textMes.setText(" Rx del Mes " + actualMes + ":\n" + sumaMes + "");
        textBalance.setText("Balance Semana #" + actualSemana);

        // Crea una instancia de CacheManager
        cacheManager = CacheManager.getInstance(this);

        btn_back_gallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(Estadistica.this, Gallery.class);
                startActivity(intent);
            }
        });



        BarChart barChart = findViewById(R.id.barChart);

// Configurar datos de ejemplo (reemplazar con tus datos reales)
        ArrayList<BarEntry> entries = new ArrayList<>();
        // Verificar si la cadena no está vacía
        String jsonRegSemana = dom_sab.replace("'", "\"");
        // Convertir la cadena a un objeto JSON
        int domingo,lunes,martes,miercoles,jueves,viernes,sabado;

        try {
            JSONObject jsonObject = new JSONObject(jsonRegSemana);
            domingo = jsonObject.getInt("Sunday");
            lunes = jsonObject.getInt("Monday");
            martes = jsonObject.getInt("Tuesday");
            miercoles = jsonObject.getInt("Wednesday");
            jueves = jsonObject.getInt("Thursday");
            viernes = jsonObject.getInt("Friday");
            sabado = jsonObject.getInt("Saturday");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        entries.add(new BarEntry(0f, domingo)); // Cantidad de fórmulas para el lunes
        entries.add(new BarEntry(1f, lunes)); // Cantidad de fórmulas para el martes
        entries.add(new BarEntry(2f, martes));
        entries.add(new BarEntry(3f, miercoles));
        entries.add(new BarEntry(4f, jueves));
        entries.add(new BarEntry(5f, viernes));
        entries.add(new BarEntry(6f, sabado));
        // Repite para los otros días de la semana...

        BarDataSet dataSet = new BarDataSet(entries, "Cantidad de fórmulas");
        BarData barData = new BarData(dataSet);
        dataSet.setDrawValues(true);
        // Configurar propiedades del gráfico
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);

        // Configurar etiquetas del eje X (días de la semana)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDiasSemana())); // Función para obtener los días de la semana
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);

        barChart.invalidate(); // Actualizar el gráfico

    }

    private List<String> getDiasSemana() {
        List<String> diasSemana = new ArrayList<>();

        // Establecer el locale a español
        Locale locale = new Locale("es", "ES");
        DateFormatSymbols symbols = new DateFormatSymbols(locale);

        // Obtener los nombres de los días de la semana en español
        String[] nombresDias = symbols.getWeekdays();

        // Agregar los nombres de los días de la semana a la lista
        // Nota: El primer elemento (nombresDias[0]) es una cadena vacía, por lo que se omite
        for (int i = 1; i < nombresDias.length; i++) {
            diasSemana.add(nombresDias[i]);
        }

        return diasSemana;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        int fkPharma = prefs.getInt("fkPharma", 0); //
        new Mysql().obtenerEstadisticaFarmacia(Estadistica.this, fkPharma);
        // Aquí puedes agregar cualquier lógica que deseas ejecutar cuando la actividad se reanuda

        // Por ejemplo, puedes actualizar los datos desde la caché
        cacheManager.notifyObserverWithCachedData(this);

    }

    @Override
    public void onCachedDataReceived(String actualMes) {

    }

    @Override
    public void onCachedDataUpdated() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistra esta actividad como observador al destruir la actividad
        cacheManager.unregisterObserver(this);
    }
}