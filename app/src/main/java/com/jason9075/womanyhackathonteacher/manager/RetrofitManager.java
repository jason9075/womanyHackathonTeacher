package com.jason9075.womanyhackathonteacher.manager;

import android.location.Location;


import com.jason9075.womanyhackathonteacher.model.GoogleMapLocationResult;
import com.jason9075.womanyhackathonteacher.retrofit.LocationAddressService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jason9075 on 2016/12/3.
 */

public enum RetrofitManager {
    INSTANCE;

    public static final String GOOGLE_API_URL = "http://maps.googleapis.com/";

    private Retrofit retrofit;
    private LocationAddressService addressService;
    private String lastAddress = "無地址";

    RetrofitManager() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(GOOGLE_API_URL)
                .build();

        addressService = retrofit.create(LocationAddressService.class);
    }

    public Observable<GoogleMapLocationResult> requestAddress(final Location location, final boolean isSave) {
        if (location == null)
            return Observable.just(new GoogleMapLocationResult());
        return addressService.requestAddress(location.getLatitude() + "," + location.getLongitude(), "zh-TW")
                .map(new Func1<GoogleMapLocationResult, GoogleMapLocationResult>() {
                    @Override
                    public GoogleMapLocationResult call(GoogleMapLocationResult googleMapLocationResult) {
                        if (!isSave)
                            return googleMapLocationResult;
                        for (GoogleMapLocationResult.LocationModel locationModel : googleMapLocationResult.getResults()) {
                            if (locationModel.getFormattedAddress().contains("市") ||
                                    locationModel.getFormattedAddress().contains("縣"))
                                lastAddress = locationModel.getFormattedAddress();
                            return googleMapLocationResult;
                        }
                        lastAddress = "經緯度無法解析的地址";
                        return googleMapLocationResult;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public String getLastAddress() {
        return lastAddress;
    }
}
