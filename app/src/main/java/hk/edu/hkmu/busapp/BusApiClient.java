package hk.edu.hkmu.busapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BusApiClient {
        private static final String BASE_URL = "https://data.etabus.gov.hk/";
    private static BusApiService apiService;

    public static BusApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(BusApiService.class);
        }
        return apiService;
    }
}
