package com.dev.rx.db;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dev.rx.alerts.SuccessDialog;
import com.dev.rx.login.Login;
import com.dev.rx.pytorch.ObjectDetectionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mysql {
    public void send(Context context,
                     String country,
                     String city,
                     String code,
                     String lat,
                     String lng,
                     String imageMap,
                     String imageStreer,
                     String typePharma,
                     String classPharma,
                     String namePharma,
                     String ftp) {

        RequestQueue queue = Volley.newRequestQueue(context);
        Handler handler = new Handler(Looper.getMainLooper());
        String url = "http://18.219.242.84/services/register_pharma_users.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.equals("true")) {
                                    Toast.makeText(context, "Farmacia registrada!...", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(context, "Ingrese credenciales para iniciar sessión!...", Toast.LENGTH_SHORT).show();
                                    // Continuar con el proceso de autenticación



                                    Intent intent = new Intent(context, Login.class);
                                    context.startActivity(intent);

                                } else if(response.equals("false")) {
                                    Toast.makeText(context, "Fallo el registro!.", Toast.LENGTH_SHORT).show();
                                    // Pedir al usuario que ingrese sus credenciales nuevamente
                                }else{
                                    Toast.makeText(context, "responde !" + response, Toast.LENGTH_SHORT).show();
                                    Log.e("ResMysql", "" + response);
                                }
                                Log.e("ResMysql_", "" + response);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "error! al enviar información. " + error, Toast.LENGTH_SHORT).show();
                                Log.e("ErrMysql", "" + error);
                            }
                        });
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("country", country);
                params.put("city", city);
                params.put("address", code);
                params.put("lat", lat);
                params.put("lng", lng);
                params.put("imageMap", imageMap);
                params.put("imageStreer", imageStreer);
                params.put("typePharma", typePharma);
                params.put("classPharma", classPharma);
                params.put("namePharma", namePharma);
                params.put("ftp", ftp);
                return params;
            }
        };

        queue.add(stringRequest);

    }


    public interface VolleyCallback {
        void onSuccess(List<String> result);
    }

    //llenar campos autocomplete
    public void selectOne(Context context,
                          VolleyCallback callback) {
        String url = "http://18.219.242.84/services/select_type_pharma.php";
        List<String> listaValores = new ArrayList<>();

        // Crear una solicitud HTTP utilizando la biblioteca Volley
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("resultados");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String valor = jsonObject.getString("description");
                                listaValores.add(valor);
                            }

                            // Llamar al callback con la lista de valores
                            callback.onSuccess(listaValores);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error de la solicitud HTTP
                    }
                });

        // Agregar la solicitud HTTP a la cola de solicitudes de Volley
        queue.add(jsonObjectRequest);
    }

    public void selectTwo(Context context,
                          VolleyCallback callback) {
        String url = "http://18.219.242.84/services/select_type_cadena.php";

        // Crear una solicitud HTTP utilizando la biblioteca Volley
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> listaValores = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String valor = jsonObject.getString("name");
                                listaValores.add(valor);
                            }
                            // Llamar al callback con la lista de valores
                            callback.onSuccess(listaValores);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error de la solicitud HTTP
                    }
                });

        // Agregar la solicitud HTTP a la cola de solicitudes de Volley
        queue.add(jsonArrayRequest);
    }

    public void selectTree(Context context,
                          VolleyCallback callback) {
        String url = "http://18.219.242.84/services/select_pharma.php";

        // Crear una solicitud HTTP utilizando la biblioteca Volley
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> listaValores = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String valor = jsonObject.getString("name");
                                listaValores.add(valor);
                            }
                            // Llamar al callback con la lista de valores
                            callback.onSuccess(listaValores);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error de la solicitud HTTP
                    }
                });

        // Agregar la solicitud HTTP a la cola de solicitudes de Volley
        queue.add(jsonArrayRequest);
    }


    // validar usuario
    public void users(Context context,
                     String user,
                     String pw
                    ) {

        RequestQueue queue = Volley.newRequestQueue(context);
        Handler handler = new Handler(Looper.getMainLooper());
        String url = "http://18.219.242.84/services/validate_user.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.equals("true")) {
                                    Toast.makeText(context, "El usuario y la contraseña son válidos" + response, Toast.LENGTH_SHORT).show();
                                    // Continuar con el proceso de autenticación
                                    //crear token
                                    SharedPreferences prefs = context.getSharedPreferences("MisPreferencias", context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    //editor.putInt("id", response); // Guarda el id de usuario
                                    editor.putBoolean("estaConectado", true); //Guarda el estado de inicio de sesión
                                    editor.apply(); //Guarda los cambios en las preferencias compartidas
                                    //guardar en cache id de usuario
                                    //getFkPharma(context, user);
                                    getUsers(context, user);

                                    Intent intent = new Intent(context,ObjectDetectionActivity.class);
                                    context.startActivity(intent);
                                } else {
                                    Toast.makeText(context, "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                                    // Pedir al usuario que ingrese sus credenciales nuevamente
                                }
                                Log.e("ResMysql", "" + response);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "error! al enviar información. " + error, Toast.LENGTH_SHORT).show();
                                Log.e("ErrMysql", "" + error);
                            }
                        });
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", user);
                params.put("password", pw);
                return params;
            }
        };

        queue.add(stringRequest);

    }


    // optenr id de usuario
    public void getUsers(Context context, String user) {
        RequestQueue queue = Volley.newRequestQueue(context);
        Handler handler = new Handler(Looper.getMainLooper());
        String url = "http://18.219.242.84/services/select_data_user.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    //System.out.println(" ---> aqui ----Z>"  + jsonObject.getString("ftp"));
                                    String ftp = jsonObject.getString("ftp");
                                    String pharma = jsonObject.getString("name_pharma");
                                    SharedPreferences prefs = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();

                                    editor.putString("ftp", ftp);
                                    editor.putString("name_pharma", pharma);
                                    editor.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "error! al enviar información. " + error, Toast.LENGTH_SHORT).show();
                                Log.e("ErrMysql", "" + error);
                            }
                        });
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", user);
                return params;
            }
        };

        queue.add(stringRequest);
    }



}


