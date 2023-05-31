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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mysql {


    //estadistica de farmacia
    public void obtenerEstadisticaFarmacia(Context context, int idFarmacia) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://18.219.242.84/services/estadistica_farmacia.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Manejar la respuesta JSON
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int sumaDia = jsonObject.getInt("suma_dia");
                            int sumaSemana = jsonObject.getInt("suma_semana");
                            int sumaMes = jsonObject.getInt("suma_mes");
                            String actualMes = jsonObject.getString("mes_actual");
                            String actualSemana = jsonObject.getString("semana_actual");
                            String actualDia = jsonObject.getString("dia_actual");
                            String regSemana = jsonObject.getString("registros_semana");

                            // Guardar los resultados en SharedPreferences
                            SharedPreferences prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("sumaDia", sumaDia);
                            editor.putInt("sumaSemana", sumaSemana);
                            editor.putInt("sumaMes", sumaMes);
                            editor.putString("mes_actual", actualMes);
                            editor.putString("semana_actual", actualSemana);
                            editor.putString("dia_actual",actualDia);
                            editor.putString("registro_semana", regSemana);
                           editor.apply();



                            // Mostrar los datos obtenidos
                            //Toast.makeText(context, "Suma de la semana: " + sumaSemana, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, "Suma del mes: " + sumaMes, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, "mes: " + actualMes, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, "semana: " + regSemana, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error al procesar la respuesta JSON" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Unknown error";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                errorMessage = new String(error.networkResponse.data, "UTF-8");
                                System.err.println(errorMessage);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("EstadisticaErr", errorMessage);
                        Toast.makeText(context, "Error al enviar la solicitud HTTP", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros de la solicitud POST
                Map<String, String> params = new HashMap<>();
                params.put("id_farmacia", String.valueOf(idFarmacia));
                return params;
            }
        };

        queue.add(stringRequest);
    }


    // guardar numero de rx en base de datos por fecha y farmacia
    public void enviarContador(Context context, int idFarmacia, int cantidad) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://18.219.242.84/services/contador.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Manejar la respuesta JSON
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String fecha = jsonObject.getString("fecha");
                            int cantidadActualizada = jsonObject.getInt("cantidad");
                            int cantidadRegistrosMes = jsonObject.getInt("cantidad_registros_mes");

                            // Mostrar los datos obtenidos
                            Toast.makeText(context, "Fecha: " + fecha, Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "Cantidad actualizada: " + cantidadActualizada, Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "Cantidad de registros en el mes: " + cantidadRegistrosMes, Toast.LENGTH_SHORT).show();
                            SharedPreferences prefs = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();

                            editor.putString("numRx", String.valueOf(cantidadRegistrosMes));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error al procesar la respuesta JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Unknown error";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                errorMessage = new String(error.networkResponse.data, "UTF-8");
                                System.err.println(errorMessage);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("ContadorErr", errorMessage);
                        Toast.makeText(context, "Error al enviar la solicitud HTTP", Toast.LENGTH_SHORT).show();
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros de la solicitud POST
                Map<String, String> params = new HashMap<>();
                params.put("fk_farma", String.valueOf(idFarmacia));
                params.put("cantidad", String.valueOf(cantidad));
                return params;
            }
        };

        queue.add(stringRequest);
    }


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
                                    //System.out.println(" ---> aqui ----Z>"  + jsonObject.getString("id"));
                                    String ftp = jsonObject.getString("ftp");
                                    String pharma = jsonObject.getString("name_pharma");
                                    String latitud = jsonObject.getString("lat");
                                    String longitud = jsonObject.getString("lng");
                                    int idpharma = jsonObject.getInt("id");
                                    SharedPreferences prefs = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();

                                    editor.putString("ftp", ftp);
                                    editor.putString("name_pharma", pharma);
                                    editor.putInt("fkPharma", idpharma);
                                    editor.putString("lat", latitud);
                                    editor.putString("lng", longitud);
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


