package com.example.baira;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Инициализация Firebase
        FirebaseApp.initializeApp(this);
        
        try {
            // Включение офлайн-режима Firebase
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Log.d(TAG, "Firebase persistence enabled");
        } catch (Exception e) {
            Log.e(TAG, "Error enabling Firebase persistence: " + e.getMessage());
        }
        
        // Установка русской локали по умолчанию
        setRussianLocale();
        
        // Включение поддержки векторных изображений
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    
    /**
     * Устанавливает русскую локаль для приложения
     */
    private void setRussianLocale() {
        try {
            Locale russianLocale = new Locale("ru", "RU");
            Locale.setDefault(russianLocale);
            
            Configuration config = getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(russianLocale);
                createConfigurationContext(config);
            } else {
                config.locale = russianLocale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }
            
            Log.d(TAG, "Russian locale set successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting Russian locale: " + e.getMessage());
        }
    }
    
    @Override
    protected void attachBaseContext(Context base) {
        // Применяем русскую локаль к базовому контексту
        Locale russianLocale = new Locale("ru", "RU");
        Locale.setDefault(russianLocale);
        
        Configuration config = new Configuration(base.getResources().getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(russianLocale);
        } else {
            config.locale = russianLocale;
        }
        
        super.attachBaseContext(base.createConfigurationContext(config));
    }
} 