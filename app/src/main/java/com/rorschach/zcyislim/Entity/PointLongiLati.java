package com.rorschach.zcyislim.Entity;

import java.io.Serializable;

public class PointLongiLati implements Serializable {
    private static final long serialVersionUID = 9272L;
    double latitude;
    double longitude;

    public PointLongiLati(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "latitude/longitude(" + +latitude + "," + longitude + ')';
    }

}
