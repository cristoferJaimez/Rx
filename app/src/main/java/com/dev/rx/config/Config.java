package com.dev.rx.config;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.dev.rx.R;
import com.dev.rx.login.Login;
import com.dev.rx.pytorch.ObjectDetectionActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Config extends AppCompatActivity {

    private ImageButton btnBack, btnLogOut;
    private Switch switch1, switch2, switch3;
    private TextView textUser, textAddress, textNamePharma, textFtp;

    private ImageView info, info2, info3;
    private GoogleMap mMap;
    private MapView mapView;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //obtenr datos para llenar campos
        int fkPharma = prefs.getInt("fkPharma", 0); //
        String user = prefs.getString("name_pharma",  "sin datos...");
        String address = prefs.getString("dbAddress",  "sin datos...");
        String type = prefs.getString("dbType",  "sin datos...");
        String ftp = prefs.getString("dbFtp",  "sin datos...");
        String lat = prefs.getString("lat",  " 4.5709");
        String lng = prefs.getString("lng",  " -74.2973");


        // Obtener referencia al MapView del XML
        mapView = findViewById(R.id.mapView);

        // Importante: Llamar al método onCreate() del MapView
        mapView.onCreate(savedInstanceState);

        // Configurar el callback para el mapa cuando esté listo
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                LatLng location = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng)); // San Francisco
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16)); // Zoom in on location
                mMap.addMarker(new MarkerOptions().position(location).title(user)); // Agregar marcador con título
            }
        });


        //hacer consulta de datos




        btnBack = findViewById(R.id.btn_back_camera);
        info = findViewById(R.id.info);
        info2 = findViewById(R.id.info2);
        info3 = findViewById(R.id.info3);
        btnLogOut = findViewById(R.id.btnLogOut);
        textUser = findViewById(R.id.textFarmaciaNombre);


        textUser.setText(user);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Config.this);
                builder.setTitle("Cerrar sesión");
                builder.setMessage("¿Estás seguro de que quieres cerrar sesión?");
                builder.setIcon(R.drawable.alert_octagon_svgrepo_com); // Aquí agregas la imagen deseada
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(Config.this, Login.class);
                        startActivity(intent);
                        finish();

                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();

            }
        });




        // Obtener una instancia de SharedPreferences

        // Obtener una instancia de los Switches en el layout de la interfaz de usuario
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);

        // Obtener los estados guardados de los Switches en SharedPreferences
        boolean switch1State = prefs.getBoolean("switch1_state", false);
        boolean switch2State = prefs.getBoolean("switch2_state", false);
        boolean switch3State = prefs.getBoolean("switch3_state", false);

        // Establecer los estados guardados de los Switches
        switch1.setChecked(switch1State);
        switch2.setChecked(switch2State);
        switch3.setChecked(switch3State);




        // Escuchar los cambios de estado en los Switches
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Guardar el nuevo estado en SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("switch1_state", isChecked);
                editor.apply();
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Guardar el nuevo estado en SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("switch2_state", isChecked);
                editor.apply();
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Guardar el nuevo estado en SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("switch3_state", isChecked);
                editor.apply();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });

        // infos btn
        info.setOnClickListener(v -> {
            // Manejar el evento de clic aquí
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Esta opcion te permite activar o desactivar la vista previa cuando relizas la captura a una Rx.")
                    .setTitle("Info")
                    .setCancelable(false)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Acción a realizar cuando se hace clic en el botón Cancelar
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        });
        info2.setOnClickListener(v -> {
            // Manejar el evento de clic aquí
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Esta opción te permite activar o desactivar la carga automática al servicio FTP.")
                    .setTitle("Info")
                    .setCancelable(false)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Acción a realizar cuando se hace clic en el botón Cancelar
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        });
        info3.setOnClickListener(v -> {
            // Manejar el evento de clic aquí
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Esta opción te permite activar o desactivar la encriptada de la Rx, para no ser visualizadas en la galería del dispositivo.")
                    .setTitle("Info")
                    .setCancelable(false)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Acción a realizar cuando se hace clic en el botón Cancelar
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        });



    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}
