package com.udacity.footballscores;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

public class FootballApp extends Application {
    private static FootballApp mInstance;
    private static Context mContext;
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mInstance = this;

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());


    }
    public static synchronized FootballApp getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mContext;
    }


}
