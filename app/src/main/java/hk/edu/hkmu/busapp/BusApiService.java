package hk.edu.hkmu.busapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BusApiService {
    @GET("/v1/transport/kmb/route/")
    Call<BusRouteListModel> getRoutes(); // Returns User object

    @GET("/v1/transport/kmb/route-stop/{route}/{bound}/{type}")
    Call<RouteStopListModel> getRouteStop(@Path("route") String route,@Path("bound") String bound,@Path("type") String type);

    @GET("/v1/transport/kmb/stop/{stopId}")
    Call<TempNameListContainerModel> getStopName(@Path("stopId") String stopId);

    @GET("/v1/transport/kmb/route-eta/{route}/{type}")
    Call<EtaListModel> getRouteEta(@Path("route") String route,@Path("type") String type);

    @GET("/v1/transport/kmb/route/{route}/{bound}/{type}")
    Call<DestNameContainerModel> getDestName(@Path("route") String route,@Path("bound") String bound,@Path("type") String type);

    @GET("/v1/transport/kmb/eta/{stopId}/{route}/{type}")
    Call<EtaListModel> getRouteStopEta(@Path("stopId") String stopId, @Path("route") String bound, @Path("type") String type);
}

