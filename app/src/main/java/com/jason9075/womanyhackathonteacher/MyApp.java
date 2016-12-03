package com.jason9075.womanyhackathonteacher;

import android.app.Application;

import com.jason9075.womanyhackathonteacher.componment.AppComponent;
import com.jason9075.womanyhackathonteacher.componment.DaggerAppComponent;
import com.jason9075.womanyhackathonteacher.manager.SharedPreferencesManager;
import com.jason9075.womanyhackathonteacher.module.MyLocationManagerModule;
import com.jason9075.womanyhackathonteacher.module.SharedPrefModule;

import javax.inject.Inject;

/**
 * Created by jason9075 on 2016/12/3.
 */

public class MyApp extends Application {

    @Inject
    SharedPreferencesManager pref;

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
        getComponents().inject(this);

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void initializeInjector() {
        appComponent = DaggerAppComponent.builder()
                .myLocationManagerModule(new MyLocationManagerModule(this))
                .sharedPrefModule(new SharedPrefModule(this))
                .build();
    }

    public static AppComponent getComponents() {
        return appComponent;
    }


}
