package com.dev.rx.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Mysql {


    public void send(Context context, String country, String city, String code, String lat, String lng, String imageMap, String imageStreer, String typePharma, String classPharma, String namePharma, String ftp){

        RequestQueue queue = Volley.newRequestQueue(context);
        Handler handler = new Handler(Looper.getMainLooper());
        String url = "http://18.223.43.180/services/save_pharma.php";

        Log.d("IMAGESMAP", String.valueOf(imageMap.isEmpty()));
        Log.d("IMAGES", imageStreer);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "responde !" + response, Toast.LENGTH_SHORT).show();
                                Log.e("ResMysql", ""+response);
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
                                Toast.makeText(context, "error! al enviar información. "  +error, Toast.LENGTH_SHORT).show();
                                Log.e("ErrMysql", ""+error);
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

}
