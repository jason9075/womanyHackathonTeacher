package com.jason9075.womanyhackathonteacher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jason9075.womanyhackathonteacher.componment.AppComponent;
import com.jason9075.womanyhackathonteacher.componment.DaggerAppComponent;
import com.jason9075.womanyhackathonteacher.manager.SharedPreferencesManager;
import com.jason9075.womanyhackathonteacher.model.AlertManagerModule;
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
    private ActivityLifecycleCallbacks callbacks;
    private Activity currentOnTopActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
        getComponents().inject(this);

        /* 用於監控所有Activity的生命週期 */
        callbacks = getActivityLifecycleCallbacks();
        registerActivityLifecycleCallbacks(callbacks);

    }


    @Override
    public void onTerminate() {
        unregisterActivityLifecycleCallbacks(callbacks);
        super.onTerminate();
    }

    private void initializeInjector() {
        appComponent = DaggerAppComponent.builder()
                .myLocationManagerModule(new MyLocationManagerModule(this))
                .sharedPrefModule(new SharedPrefModule(this))
                .alertManagerModule(new AlertManagerModule(this))
                .build();
    }

    public static AppComponent getComponents() {
        return appComponent;
    }

    @Nullable
    public Context getCurrentActivity() {
        return currentOnTopActivity;
    }

    /* 用於監控所有Activity的生命週期 */
    @NonNull
    private ActivityLifecycleCallbacks getActivityLifecycleCallbacks() {
        return new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentOnTopActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity.equals(currentOnTopActivity))
                    currentOnTopActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity.equals(currentOnTopActivity))
                    currentOnTopActivity = null;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
    }


}
