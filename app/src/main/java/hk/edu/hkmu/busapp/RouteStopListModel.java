package hk.edu.hkmu.busapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteStopListModel {
    @SerializedName("data")
    private List<StopData> routeStopList;

    public static class StopData {
        @SerializedName("stop")
        private String stopId;

        @SerializedName("lat")
        private String latitude;

        @SerializedName("long")
        private String longitude;

        @SerializedName("seq")
        private String seq;

        private String nameEn;
        private String nameTc;
        private String nameSc;

        // Getters
        public String getStopId() {
            return stopId;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getSeq() {
            return seq;
        }

        public String getNameEn() {
            return nameEn;
        }

        public String getNameTc() {
            return nameTc;
        }

        public String getNameSc() {
            return nameSc;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public void setNameTc(String nameTc) {
            this.nameTc = nameTc;
        }

        public void setNameSc(String nameSc) {
            this.nameSc = nameSc;
        }

    }

    public List<StopData> getRouteStopList() { return routeStopList; }
}
