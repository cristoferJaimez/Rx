package com.dev.rx.obser;

public interface CacheObserver {
    void onCachedDataReceived(String actualMes);
    void onCachedDataUpdated();
}

