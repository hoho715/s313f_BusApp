package hk.edu.hkmu.busapp;

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


        public int getSeq() {
            return seq;
        }

        public int getEtaSeq() {
            return etaSeq;
        }

        public String getEtaTimeStamp() {
            return etaTimeStamp;
        }

        public int getEta() {
            OffsetDateTime givenTime = OffsetDateTime.parse(etaTimeStamp);
            OffsetDateTime currentTime = OffsetDateTime.now(ZoneId.of("Hongkong"));
            Duration duration = Duration.between(givenTime, currentTime);
            System.out.println("givenTime: "+givenTime+"currentTime: "+currentTime+"duration: "+duration);
            System.out.println("duration.toMinutes(): "+duration.toMinutes());
            return Math.toIntExact(duration.toMillis()/60000);
        }
    }

    public List<EtaModel> getDatalist() {
        return datalist;
    }
}
