package com.jason9075.womanyhackathonteacher.retrofit;


import com.jason9075.womanyhackathonteacher.model.GoogleMapLocationResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jason9075 on 2016/12/3.
 */

public interface LocationAddressService {

    @GET("/maps/api/geocode/json")
    Observable<GoogleMapLocationResult> requestAddress(@Query("latlng") String latlng, @Query("language") String language);
}
