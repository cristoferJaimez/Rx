package com.dev.rx.login;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dev.rx.R;
import com.dev.rx.db.Mysql;
import com.dev.rx.permissions.Permissions;
import com.dev.rx.pytorch.ObjectDetectionActivity;
import com.dev.rx.register.Geo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private Button btnLogin, btnRegister;
    private  EditText  usernameEditText, passwordEditText;

    private ImageButton biometricLoginButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //solicitar permisos
        new Permissions().access(this);

        usernameEditText = findViewById(R.id.editTextUserName);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        biometricLoginButton = findViewById(R.id.biometric_login);
        String user = null;
        XmlResourceParser parser = getResources().getXml(R.xml.config);

        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("user")) {
                    user = parser.nextText();
                    break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Recuperar estado de inicio de sesión
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        boolean estaConectado = prefs.getBoolean("estaConectado", false);

        if (estaConectado == false) {
            // Realizar la autenticación con el token guardado
            biometricLoginButton.setVisibility(View.INVISIBLE);
        } else{
            Intent intent = new Intent(Login.this,ObjectDetectionActivity.class);
            startActivity(intent);

            usernameEditText.setText(user);
            //si no esta registrado ocultar configuracion de huella
            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Log.e("MY_APP_TAG", "No biometric features available on this device.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    // Prompts the user to create credentials that your app accepts.
                    final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                    startActivityForResult(enrollIntent, REQUEST_CODE);
                    break;
            }

            executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(Login.this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {

                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(),
                                    "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);

                    Toast.makeText(getApplicationContext(),
                            "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, ObjectDetectionActivity.class);
                    startActivity(intent);
                    //finish();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for my app")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Use account password")
                    .build();

            biometricLoginButton.setOnClickListener(view -> {
                biometricPrompt.authenticate(promptInfo);
            });
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();


                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(Login.this, "Ingrese su nombre de usuario", Toast.LENGTH_SHORT).show();
                    usernameEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Login.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
                    passwordEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Login.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(passwordEditText, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }else{



                      new Mysql().users(Login.this, username,password);

                    //Toast.makeText(getApplicationContext(),
                    //        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(Login.this, ObjectDetectionActivity.class);
                    //startActivity(intent);
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, Geo.class);
                startActivity(intent);
            }
        });
    }


}