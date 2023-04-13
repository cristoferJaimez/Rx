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
import com.dev.rx.pytorch.ObjectDetectionActivity;

public class Config extends AppCompatActivity {

    private ImageButton btnBack;
    private Switch switch1, switch2, switch3;
    private TextView textUser, textAddress, textNamePharma, textFtp;

    private ImageView info, info2, info3;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        //obtenr datos para llenar campos

        int fkPharma = prefs.getInt("fkPharma", 0); //
        String user = prefs.getString("dbUSer",  "sin datos...");
        String address = prefs.getString("dbAddress",  "sin datos...");
        String type = prefs.getString("dbType",  "sin datos...");
        String ftp = prefs.getString("dbFtp",  "sin datos...");


        btnBack = findViewById(R.id.btn_back_camera);
        info = findViewById(R.id.info);
        info2 = findViewById(R.id.info2);
        info3 = findViewById(R.id.info3);


        textUser = findViewById(R.id.textView);
        textAddress = findViewById(R.id.textView2);
        textNamePharma = findViewById(R.id.textView3);
        textFtp = findViewById(R.id.textView5);



        textUser.setText(user);
        textAddress.setText(address);
        textNamePharma.setText(type);
        textFtp.setText(ftp);



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


}
