package hk.edu.hkmu.busapp;

import com.google.gson.annotations.SerializedName;

public class TempNameListContainerModel {
    @SerializedName("data")
    private TempNameListModel tempNameListModel;

    public TempNameListModel getTempNameListModel() {
        return tempNameListModel;
    }

    public static class TempNameListModel {
        @SerializedName("name_en")
        private String nameEn;

        @SerializedName("name_tc")
        private String nameTc;

        @SerializedName("name_sc")
        private String nameSc;

        public String getNameEn() {
            return nameEn;
        }

        public String getNameTc() {
            return nameTc;
        }

        public String getNameSc() {
            return nameSc;
        }
    }
}
