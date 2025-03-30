package hk.edu.hkmu.busapp;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.time.ZoneId;
import java.util.List;

import java.time.OffsetDateTime;
import java.time.Duration;
import java.time.ZoneOffset;

public class EtaListModel {
    @SerializedName("data")
    private List<EtaModel> datalist;

    public static class EtaModel{
        @SerializedName("seq")
        private int seq;

        @SerializedName("eta_seq")
        private int etaSeq;

        @SerializedName("route")
        private String route;

        @SerializedName("service_type")
        private String type;

        @SerializedName("eta")
        private String etaTimeStamp;

        @SerializedName("stop")
        private String stopId;

        @SerializedName("dir")
        private String bound;

        @SerializedName("dest_tc")
        private String destTc;

        @SerializedName("dest_en")
        private String destEn;

        @SerializedName("dest_sc")
        private String destSc;

        private String tempName;

        public String getTempName() {
            return tempName;
        }

        public void setTempName(String tempName) {
            this.tempName = tempName;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public int getEtaSeq() {
            return etaSeq;
        }

        public void setEtaSeq(int etaSeq) {
            this.etaSeq = etaSeq;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getEtaTimeStamp() {
            return etaTimeStamp;
        }

        public void setEtaTimeStamp(String etaTimeStamp) {
            this.etaTimeStamp = etaTimeStamp;
        }

        public String getStopId() {
            return stopId;
        }

        public void setStopId(String stopId) {
            this.stopId = stopId;
        }

        public String getBound() {
            return bound;
        }

        public void setBound(String bound) {
            this.bound = bound;
        }

        public String getDestTc() {
            return destTc;
        }

        public String getDestEn() {
            return destEn;
        }

        public String getDestSc() {
            return destSc;
        }

        public Integer getEta() {
            if(etaTimeStamp  == null){
                return null;
            }

            OffsetDateTime givenTime = OffsetDateTime.parse(etaTimeStamp);
            OffsetDateTime currentTime = OffsetDateTime.now(ZoneId.of("Hongkong"));
            Duration duration = Duration.between(givenTime, currentTime);

            return (int) Math.abs(duration.toMinutes());
        }
    }

    public List<EtaModel> getDatalist() {
        return datalist;
    }
}
