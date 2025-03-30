package hk.edu.hkmu.busapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusRouteListModel {
    @SerializedName("data")
    private List<BusRouteModel> routeList;

    public static class BusRouteModel {
        @SerializedName("route")
        private String id;

        @SerializedName("bound")
        private String bound;

        @SerializedName("service_type")
        private String serviceType;

        @SerializedName("dest_tc")
        private String destinationTc;

        @SerializedName("dest_en")
        private String destinationEn;

        @SerializedName("dest_sc")
        private String destinationSc;

        @SerializedName("orig_tc")
        private String originTc;

        @SerializedName("orig_en")
        private String originEn;

        @SerializedName("orig_sc")
        private String originSc;


        // Getters
        public String getRoute() { return id; }
        public String getDestination() { return destinationTc; }
        public String getOrigin() { return originTc; }
        public String getServiceType() { return serviceType; }
        public String getBound() { return bound; }
    }
    // Getters
    public List<BusRouteModel> getRouteList() { return routeList; }
}
