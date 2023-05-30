package com.dev.rx.obser;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class CacheManager {
    private static CacheManager instance;
    private List<CacheObserver> observers;
    private String cachedData;
    private Context context;

    private CacheManager(Context context) {
        this.context = context.getApplicationContext();
        observers = new ArrayList<>();
        loadDataFromSharedPreferences();
    }

    public static CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context);
        }
        return instance;
    }

    public void setCachedData(String data) {
        cachedData = data;
        saveDataToSharedPreferences();
        notifyObservers();
    }

    private void saveDataToSharedPreferences() {
        SharedPreferences prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("cachedData", cachedData);
        editor.apply();
    }

    private void loadDataFromSharedPreferences() {
        SharedPreferences prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        cachedData = prefs.getString("cachedData", null);
    }

    public void registerObserver(CacheObserver observer) {
        observers.add(observer);
        observer.onCachedDataReceived(cachedData);
        notifyObserverWithCachedData(observer);
    }

    public void unregisterObserver(CacheObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (CacheObserver observer : observers) {
            observer.onCachedDataUpdated();
        }
    }

    public void notifyObserverWithCachedData(CacheObserver observer) {
        observer.onCachedDataReceived(cachedData);
    }
}
