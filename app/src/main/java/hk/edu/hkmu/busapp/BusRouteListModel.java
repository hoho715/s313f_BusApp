package hk.edu.hkmu.busapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusRouteListModel {
    @SerializedName("data")
    private List<BusRouteModel> routeList;

    public static class BusRouteModel {
        @SerializedName("route")
        private String id;

        @SerializedName("dest_tc")
        private String destination;

        @SerializedName("orig_tc")
        private String origin;

        @SerializedName("service_type")
        private String serviceType;

        @SerializedName("bound")
        private String bound;

        // Getters
        public String getRoute() { return id; }
        public String getDestination() { return destination; }
        public String getOrigin() { return origin; }
        public String getServiceType() { return serviceType; }
        public String getBound() { return bound; }
    }
    // Getters
    public List<BusRouteModel> getRouteList() { return routeList; }
}
