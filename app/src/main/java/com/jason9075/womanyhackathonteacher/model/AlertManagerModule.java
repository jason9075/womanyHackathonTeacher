package com.jason9075.womanyhackathonteacher.model;

import android.content.Context;

import com.jason9075.womanyhackathonteacher.manager.AlertManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jason9075 on 2016/12/4.
 */

@Module
public class AlertManagerModule {
    private final Context applicationContext;

    public AlertManagerModule(Context context) {
        this.applicationContext = context;
    }

    @Provides
    @Singleton
    public AlertManager provideAlertManager() {
        return new AlertManager(applicationContext);
    }
}
