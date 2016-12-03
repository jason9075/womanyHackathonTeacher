package com.jason9075.womanyhackathonteacher.module;

import android.content.Context;


import com.jason9075.womanyhackathonteacher.manager.MyLocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jason9075 on 2016/12/3.
 */

@Module
public class MyLocationManagerModule {

    private final Context applicationContext;

    public MyLocationManagerModule(Context context){
        this.applicationContext = context;
    }

    @Provides
    @Singleton
    public MyLocationManager provideMyLocationManager(){
        return new MyLocationManager(applicationContext);
    }
}
