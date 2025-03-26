package hk.edu.hkmu.busapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusRouteListModel {
    @SerializedName("data")
    private List<BusRouteModel> routeList;

    // Getters
    public List<BusRouteModel> getRouteList() { return routeList; }
}
