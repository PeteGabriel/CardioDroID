package com.dev.cardioid.ps.cardiodroid.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * TODo
 */

public final class CircularArea {

    private LatLng location;
    private double radius;
    private String id;


    public CircularArea(String id, LatLng location, double radius){
        this.id = id;
        this.location = location;
        this.radius = radius;
    }

    public String getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public LatLng getLocation() {
        return location;
    }


}
