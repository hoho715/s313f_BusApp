package hk.edu.hkmu.busapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusStopModel {
    @SerializedName("data")
    List<BusStopDataModel> busStopDataModel;

    public List<BusStopDataModel> getBusStopDataModel() {
        return busStopDataModel;
    }

    public static class BusStopDataModel{
        @SerializedName("stop")
        private String stop;

        @SerializedName("name_tc")
        private String nameTc;

        @SerializedName("name_en")
        private String nameEn;

        @SerializedName("name_sc")
        private String nameSc;

        @SerializedName("lat")
        private float locLat;

        @SerializedName("long")
        private float locLong;

        public String getStop() {
            return stop;
        }

        public String getNameTc() {
            return nameTc;
        }

        public String getNameEn() {
            return nameEn;
        }

        public String getNameSc() {
            return nameSc;
        }

        public float getLocLat() {
            return locLat;
        }

        public float     getLocLong() {
            return locLong;
        }
    }
}
