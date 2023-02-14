package com.unitecker.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.applovin.sdk.AppLovinSdk;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.orhanobut.hawk.BuildConfig;
import com.orhanobut.hawk.Hawk;

import timber.log.Timber;


public class Application extends MultiDexApplication {

    private static Application instance;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Hawk.init(this).build();

        instance = this;
        if (BuildConfig.DEBUG)
        {
            Timber.plant(new Timber.DebugTree());
        }
        AudienceNetworkAds.initialize(instance);
        AppLovinSdk.initializeSdk(instance);
        MobileAds.initialize(this, initializationStatus -> {

        });


    }

    public static Application getInstance ()
    {
        return instance;
    }

    public static boolean hasNetwork ()
    {
        return instance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
