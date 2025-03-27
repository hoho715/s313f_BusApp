package hk.edu.hkmu.busapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchedBusListActivity extends AppCompatActivity {
    private BusApiService apiService;
    private RouteStopListModel routeStopList;
    private TempNameListContainerModel tempNameListContainerModel;
    private List<RouteStopListModel.StopData> stopDataList;
    private ArrayList<String> stopNameListData = new ArrayList();

    ListView lv;
    ArrayAdapter<String> arr;
    Call<String> call2;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_route);

        Bundle extras = getIntent().getExtras();
        String route = extras.getString("route");
        String type = extras.getString("type");
        String bound = extras.getString("bound");
        //Api called
        apiService = BusApiClient.getApiService();

        String textBound;
        if (bound == "0"){
            textBound = "inbound";
        }else{
            textBound = "outbound";
        }
        Log.e("API", route+" "+textBound+" "+type);
        Call<RouteStopListModel> call = apiService.getRouteStop(route,textBound,type);

        //SetList
        lv = findViewById(R.id.lvRoute);
        arr =new  ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                stopNameListData);
        lv.setAdapter(arr);

        call.enqueue(new Callback<RouteStopListModel>(){
            @Override
            public void onResponse(Call<RouteStopListModel> call, Response<RouteStopListModel> response) {
                if (response.isSuccessful()) {
                    routeStopList = response.body();
                    stopDataList = routeStopList.getRouteStopList();
                    fetchStopNames(stopDataList.size()-1);
                } else {
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RouteStopListModel> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });

        //timer = new Timer();
        //timer.schedule(new RemindTask(), 0, 10000); // delay in seconds
    }

    private class RemindTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    // call your method here
                    Toast.makeText(SearchedBusListActivity.this, "C'Mom no hands!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchStopNames(int counter) {
        if (!(counter >= 0)) {
            Log.e("API", counter +"Stopping!");
            // All names fetched, update UI
            arr.notifyDataSetChanged();
            return;
        }

        String currentStopId = stopDataList.get(counter).getStopId();
        Log.e("API", "Getting id " + currentStopId);
        apiService.getStopName(currentStopId).enqueue(new Callback<TempNameListContainerModel>() {
            @Override
            public void onResponse(Call<TempNameListContainerModel> call, Response<TempNameListContainerModel> response) {
                if (response.isSuccessful()) {
                    tempNameListContainerModel = response.body();
                    stopDataList.get(counter).setNameTc(tempNameListContainerModel.getTempNameListModel().getNameTc());
                    stopDataList.get(counter).setNameEn(tempNameListContainerModel.getTempNameListModel().getNameEn());
                    stopDataList.get(counter).setNameSc(tempNameListContainerModel.getTempNameListModel().getNameSc());
                    stopNameListData.add(tempNameListContainerModel.getTempNameListModel().getNameTc());
                } else {
                    stopNameListData.add("Unknown Stop");
                    Log.e("API", "Error: " + response.code());
                }
                // Process next stop
                fetchStopNames(counter-1);
            }

            @Override
            public void onFailure(Call<TempNameListContainerModel> call, Throwable t) {
                stopNameListData.add("Error loading stop");
                Log.e("API", "Failed: " + t.getMessage());
                // Process next stop even if this one failed
                fetchStopNames(counter-1);
            }
        });
    }
}

