package hk.edu.hkmu.busapp;

import com.google.gson.annotations.SerializedName;

public class DestNameContainerModel {
    @SerializedName("data")
    DestNameModel destNameModel;

    public static class DestNameModel{
        @SerializedName("dest_tc")
        private String dest_tc;

        @SerializedName("dest_sc")
        private String dest_sc;

        @SerializedName("dest_en")
        private String dest_en;

        public String getDestTc() {
            return dest_tc;
        }

        public String getDestSc() {
            return dest_sc;
        }

        public String getDestEn() {
            return dest_en;
        }

        public void setContent(String stringTc,String stringEn,String stringSc){
            dest_tc = stringTc;
            dest_en = stringEn;
            dest_sc = stringSc;
        }
    }

    public DestNameModel getDestNameModel() {
        return destNameModel;
    }
}
