package com.jason9075.womanyhackathonteacher.componment;

import com.jason9075.womanyhackathonteacher.MainActivity;
import com.jason9075.womanyhackathonteacher.MyApp;
import com.jason9075.womanyhackathonteacher.model.AlertManagerModule;
import com.jason9075.womanyhackathonteacher.module.MyLocationManagerModule;
import com.jason9075.womanyhackathonteacher.module.SharedPrefModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jason9075 on 2016/12/3.
 */

@Singleton
@Component(modules = {MyLocationManagerModule.class, SharedPrefModule.class, AlertManagerModule.class})
public interface AppComponent {

    /* Application */

    void inject(MyApp myApp);


    /* Activity */

    void inject(MainActivity mainActivity);

}
