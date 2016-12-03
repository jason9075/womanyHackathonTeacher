package com.jason9075.womanyhackathonteacher.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason9075 on 2016/12/3.
 */

public class GoogleMapLocationResult {

    private List<LocationModel> results;

    public GoogleMapLocationResult() {
        this.results = new ArrayList<>();
    }

    public List<LocationModel> getResults() {
        return results;
    }

    public class LocationModel{

        @SerializedName("formatted_address")
        private String formattedAddress;
        @SerializedName("place_id")
        private String placeId;

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public String getPlaceId() {
            return placeId;
        }
    }
}

