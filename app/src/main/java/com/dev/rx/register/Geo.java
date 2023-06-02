package com.dev.rx.register;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.dev.rx.R;
import com.dev.rx.R.id;
import com.dev.rx.db.Mysql;
import com.dev.rx.login.Login;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Geo extends AppCompatActivity {

    private Button btnBack, btnNext;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private  EditText editTextTextCountry, editTextTextCity, editTextTextCode ,editTextTextLat, editTextTextLng;
    private ImageView imageViewStreetView, imageViewMap;
    private String dbUrlMap, dbUrlStreer;

    private ImageButton   btnReload;
    private AutoCompleteTextView autoCompleteTextView, autoCompleteCadena, autoCompleteFarmacia;

    @SuppressLint({"StringFormatInvalid", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);

        btnBack = findViewById(id.btnBack);
        btnNext = findViewById(id.btnNext);
        btnReload = findViewById(id.btnReload);
        editTextTextCountry = findViewById(id.editTextTextCountry);
        editTextTextCity = findViewById(id.editTextTextCity);
        editTextTextCode = findViewById(id.editTextTextCode);
        editTextTextLat = findViewById(id.editTextTextLat);
        editTextTextLng = findViewById(id.editTextTextLng);
        imageViewMap = findViewById(R.id.imagePhotoPre);
        imageViewStreetView = findViewById(R.id.imageLocality);


        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteCadena = findViewById(id.autoCompleteCadena);
        autoCompleteFarmacia = findViewById(id.autoCompleteFarmacia);

        SharedPreferences prefs = this.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);



        //llenar selects
        Mysql mysql = new Mysql();
        mysql.selectOne(this, new Mysql.VolleyCallback() {
            @Override
            public void onSuccess(List<String> result) {

                    // Obtener una referencia al AutoCompleteTextView del diseño
                    AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

                    // Configure un ArrayAdapter con sus datos
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Geo.this, android.R.layout.simple_dropdown_item_1line, result);

                    // Establecer el adaptador en el AutoCompleteTextView
                    autoCompleteTextView.setAdapter(adapter);

                    // Configure un listener para manejar la selección del usuario
                    autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Obtener el valor seleccionado y hacer algo con él
                            String selectedItem = (String) parent.getItemAtPosition(position);
                            Toast.makeText(Geo.this, "Seleccionó: " + selectedItem, Toast.LENGTH_SHORT).show();
                            AutoCompleteTextView siguienteAutoCompleteTextView = findViewById(R.id.autoCompleteCadena);
                            if(selectedItem.equals("INDEPENDIENTE")) {
                                // Deshabilitar el siguiente AutoCompleteTextView
                                siguienteAutoCompleteTextView.setEnabled(false);
                                siguienteAutoCompleteTextView.setText("S/N");
                            }else if(selectedItem.equals("COOPIDROGAS INDEPENDIENTE")){
                                // Deshabilitar el siguiente AutoCompleteTextView
                                siguienteAutoCompleteTextView.setEnabled(false);
                                siguienteAutoCompleteTextView.setText("S/N");
                            }else if(selectedItem.equals("Cadena")){
                                siguienteAutoCompleteTextView.setEnabled(true);
                                siguienteAutoCompleteTextView.setText("");
                            }
                        }
                    });

                    // Agregue un TextChangedListener para realizar la búsqueda mientras el usuario escribe
                    autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // Filtro de búsqueda
                            adapter.getFilter().filter(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                }
        });

        mysql.selectTwo(this, new Mysql.VolleyCallback() {
            @Override
            public void onSuccess(List<String> result) {


                    // Obtener una referencia al AutoCompleteTextView del diseño
                    AutoCompleteTextView autoCompleteTextView = findViewById(id.autoCompleteCadena);

                    // Configure un ArrayAdapter con sus datos
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Geo.this, android.R.layout.simple_dropdown_item_1line, result);

                    // Establecer el adaptador en el AutoCompleteTextView
                    autoCompleteTextView.setAdapter(adapter);

                    // Configure un listener para manejar la selección del usuario
                    autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Obtener el valor seleccionado y hacer algo con él
                            String selectedItem = (String) parent.getItemAtPosition(position);
                            Toast.makeText(Geo.this, "Seleccionó: " + selectedItem, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Agregue un TextChangedListener para realizar la búsqueda mientras el usuario escribe
                    autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // Filtro de búsqueda
                            adapter.getFilter().filter(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                }

        });

        mysql.selectTree(this, new Mysql.VolleyCallback() {
            @Override
            public void onSuccess(List<String> result) {
                // Obtener una referencia al AutoCompleteTextView del diseño
                AutoCompleteTextView autoCompleteTextView = findViewById(id.autoCompleteFarmacia);

                // Configure un ArrayAdapter con sus datos
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Geo.this, android.R.layout.simple_dropdown_item_1line, result);

                // Establecer el adaptador en el AutoCompleteTextView
                autoCompleteTextView.setAdapter(adapter);

                // Configure un listener para manejar la selección del usuario
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Obtener el valor seleccionado y hacer algo con él
                        String selectedItem = (String) parent.getItemAtPosition(position);
                        Toast.makeText(Geo.this, "Seleccionó: " + selectedItem, Toast.LENGTH_SHORT).show();
                    }
                });

                // Agregue un TextChangedListener para realizar la búsqueda mientras el usuario escribe
                autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Filtro de búsqueda
                        adapter.getFilter().filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        });



        getLocationInfo();


        //recargar geo localizacion :
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationInfo();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Geo.this, Login.class);
                startActivity(intent);
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtener valores de los campos
                String country = editTextTextCountry.getText().toString();
                String city = editTextTextCity.getText().toString();
                String addressLine = editTextTextCode.getText().toString();
                String lat = editTextTextLat.getText().toString();
                String lng = editTextTextLng.getText().toString();
                String typePharma = autoCompleteTextView.getText().toString();
                String classPharma = autoCompleteCadena.getText().toString();
                String namePharma = autoCompleteFarmacia.getText().toString();
                String ftp = "Rx/"+country+"/"+city+"/"+typePharma+"/"+classPharma+"/"+namePharma+"/";

                //Verificar si los campos autocompletados no están vacíos
                if(typePharma.isEmpty() || classPharma.isEmpty() || namePharma.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Por favor, completa los campos vacíos", Toast.LENGTH_SHORT).show();
                } else {
                    //Crear diálogo de confirmación
                    AlertDialog.Builder builder = new AlertDialog.Builder(Geo.this);
                    builder.setTitle("Confirmar datos");
                    builder.setMessage("¿Estás seguro de que deseas enviar estos datos?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Enviar datos a la base de datos MySQL
                            Mysql mysql = new Mysql();
                            mysql.send(Geo.this,""+country,
                                    ""+city, ""+addressLine,
                                    ""+ lat, ""+lng,
                                    ""+dbUrlMap,
                                    ""+dbUrlStreer,
                                    ""+typePharma,
                                    ""+ classPharma,
                                    ""+namePharma,
                                    ""+ftp );
                            dialog.dismiss();


                            //control
                            String idRepresentante = prefs.getString("idRepresentante", "");
                            mysql.enviarControl(Geo.this, namePharma, idRepresentante);

                        }



                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });



        // Crea una instancia del FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Solicita los permisos necesarios para acceder a la ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }

        //tomar foto para mostrar la vista en un imagenview y mantenerla para ser guardada cuando finalize el registo
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;
                                // Llamar a la función para obtener la información de ubicación
                                getLocationInfo();
                            }
                        }
                    });
        }

    }



    private void getLocationInfo() {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            String apiKey = "AIzaSyBb3IgM-eU8HwwkzPNpIcpA1BWAdWtdaoI";
            // cargar imagen del mapa con marcador en la ubicación actual
            String urlMap = "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude +
                    "&zoom=16&size=640x480&markers=color:red%7C" + latitude + "," + longitude + "&key=" + apiKey;

            Glide.with(this).load(urlMap).into(imageViewMap);
            dbUrlMap = urlMap;

            // cargar imagen de la calle en la ubicación actual
            String urlStreetView = "https://maps.googleapis.com/maps/api/streetview?size=640x480&location=" + latitude + "," + longitude + "&key=" + apiKey;
            dbUrlStreer = urlStreetView;
            Glide.with(this).load(urlStreetView).into(imageViewStreetView);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10); // Obtener hasta 10 direcciones

                Address bestAddress = null;
                float bestAccuracy = Float.MAX_VALUE;

                for (Address address : addresses) {
                    float[] results = new float[1];
                    Location.distanceBetween(latitude, longitude, address.getLatitude(), address.getLongitude(), results);

                    if (results[0] <= mLastLocation.getAccuracy() * 2 && results[0] < bestAccuracy) { // Solo considerar direcciones dentro de 100 metros y más precisas que las anteriores
                        bestAccuracy = results[0];
                        bestAddress = address;
                    }
                }

                if (bestAddress != null) {
                    String country = bestAddress.getCountryName();
                    String city = bestAddress.getLocality();
                    String addressLine = bestAddress.getAddressLine(0);

                    editTextTextCountry.setText(country);
                    editTextTextCity.setText(city);
                    editTextTextCode.setText(addressLine);
                    editTextTextLat.setText("" + latitude);
                    editTextTextLng.setText("" + longitude);




                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





}



