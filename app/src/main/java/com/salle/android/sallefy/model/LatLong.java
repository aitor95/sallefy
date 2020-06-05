package com.salle.android.sallefy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LatLong implements Serializable {
    @SerializedName("latitude")
    double latitude;
    @SerializedName("longitude")
    double longitude;

    public LatLong(double latitude, double longitude){

        this.latitude = latitude;//41.556038;
        this.longitude = longitude;//2.506701;

    }

    public LatLong() {
        this.latitude = 41.556038;
        this.longitude = 2.506701;
    }
}
