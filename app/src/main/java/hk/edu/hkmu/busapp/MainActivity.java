package hk.edu.hkmu.busapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity  {
    //searchBus = findViewById(R.id.search_bus);

    private BusApiService apiService;

    private ListView busList;
    private ArrayList<String> BusListData = new ArrayList<>();
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        busList = findViewById(R.id.bus_list);


        //Api call
        apiService = BusApiClient.getApiService();
        Call<BusRouteListModel> call = apiService.getUser();

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                BusListData);
        busList.setAdapter(adapter);

        call.enqueue(new Callback<BusRouteListModel>(){
            @Override
            public void onResponse(Call<BusRouteListModel> call, Response<BusRouteListModel> response) {
                Log.e("API", "Called: " + response.code());
                if (response.isSuccessful()) {
                    BusRouteListModel busRouteData = response.body();
                    for(BusRouteModel r:busRouteData.getRouteList()){
                        BusListData.add(r.getRoute());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BusRouteListModel> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });




    }
}

