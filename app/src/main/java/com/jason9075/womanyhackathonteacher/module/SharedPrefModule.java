package com.jason9075.womanyhackathonteacher.module;

import android.content.Context;

import com.jason9075.womanyhackathonteacher.manager.SharedPreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jason9075 on 2016/12/3.
 */
@Module
public class SharedPrefModule {

    private final Context context;

    public SharedPrefModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public SharedPreferencesManager provideSharedPreferencesManager() {
        return new SharedPreferencesManager(context);
    }
}
