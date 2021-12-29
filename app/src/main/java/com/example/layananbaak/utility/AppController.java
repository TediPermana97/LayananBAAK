package com.example.layananbaak.utility;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;

public class AppController extends MultiDexApplication {
    private static AppController sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        sInstance = this;
    }

    public static synchronized AppController getInstance() {
        return sInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}