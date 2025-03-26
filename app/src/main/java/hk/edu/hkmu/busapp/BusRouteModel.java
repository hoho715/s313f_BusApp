package hk.edu.hkmu.busapp;

import com.google.gson.annotations.SerializedName;

public class BusRouteModel {
    @SerializedName("route")
    private String id;

    @SerializedName("dest_en")
    private String destination;

    @SerializedName("orig_en")
    private String origin;

    // Getters
    public String getRoute() { return id; }
    public String getDestination() { return destination; }
    public String getOrigin() { return origin; }
}

