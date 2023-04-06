package com.dev.rx.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.rx.R;
import com.dev.rx.R.*;
import com.dev.rx.db.MongoConnect;
import com.dev.rx.login.Login;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private  EditText editTextTextCountry, editTextTextCity, editTextTextCode ,editTextTextLat, editTextTextLng;
    private ImageView imageViewStreetView, imageViewMap;
    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);

        btnBack = findViewById(id.btnBack);
        btnNext = findViewById(id.btnNext);

        editTextTextCountry = findViewById(id.editTextTextCountry);
        editTextTextCity = findViewById(id.editTextTextCity);
        editTextTextCode = findViewById(id.editTextTextCode);
        editTextTextLat = findViewById(id.editTextTextLat);
        editTextTextLng = findViewById(id.editTextTextLng);
        imageViewMap = findViewById(R.id.imagePhotoPre);
        imageViewStreetView = findViewById(R.id.imageLocality);
        getLocationInfo();


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Geo.this, Login.class);
                startActivity(intent);
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Geo.this, DataPharma.class);
                startActivity(intent);

                String country = editTextTextCountry.getText().toString();
                String city = editTextTextCity.getText().toString();
                String code = editTextTextCode.getText().toString();
                String lat = editTextTextLat.getText().toString();
                String lng = editTextTextLng.getText().toString();
                Drawable imageStreet = imageViewStreetView.getDrawable();
                Drawable  imageMap = imageViewMap.getDrawable();

                new MongoConnect();

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
                    "&zoom=16&size=1280x720&markers=color:red%7C" + latitude + "," + longitude + "&key=" + apiKey;

            Glide.with(this).load(urlMap).into(imageViewMap);

            // cargar imagen de la calle en la ubicación actual
            String urlStreetView = "https://maps.googleapis.com/maps/api/streetview?size=1280x720&location=" + latitude + "," + longitude + "&key=" + apiKey;

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



