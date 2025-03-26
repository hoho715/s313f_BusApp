package hk.edu.hkmu.busapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BusApiService {
    @GET("/v1/transport/kmb/route/")
    Call<BusRouteListModel> getUser(); // Returns User object
}

