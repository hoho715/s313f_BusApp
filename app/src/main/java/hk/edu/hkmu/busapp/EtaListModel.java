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

        @SerializedName("eta")
        private String etaTimeStamp;

        @SerializedName("dir")
        private String bound;


        public int getSeq() {
            return seq;
        }

        public int getEtaSeq() {
            return etaSeq;
        }

        public String getEtaTimeStamp() {
            return etaTimeStamp;
        }

        public String getBound() {
            return bound;
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
