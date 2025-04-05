package hk.edu.hkmu.busapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private BusApiService apiService;

    private ListView busList;
    private ArrayList<BusRouteListModel.BusRouteModel> busListData = new ArrayList<>();

    BusRouteAdapter adapter;

    private ArrayList<BusStopModel.BusStopDataModel> stopList = new ArrayList<>();
    private ArrayList<BusStopModel.BusStopDataModel> filteredStopList = new ArrayList<>();

    private  ArrayList<EtaListModel.EtaModel> displayList = new ArrayList<>();

    //location
    private LocationManager lm;

    Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        busList = findViewById(R.id.home_bus_list);

        //Api call
        apiService = BusApiClient.getApiService();
        Call<BusRouteListModel> call = apiService.getRoutes();
        Call<BusStopModel> call2 = apiService.getStops();
        //SetList
        adapter = new BusRouteAdapter(this,
                R.layout.home_list_item,
                displayList);
        busList.setAdapter(adapter);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("loc","onLocationChanged()");
                if (location != null) {
                    List<BusStopModel.BusStopDataModel> idNearBy200List = new ArrayList<>();
                    for (BusStopModel.BusStopDataModel busStopDataModel : stopList) {
                        if (haversine200(location.getLatitude(), location.getLongitude(), busStopDataModel.getLocLat(), busStopDataModel.getLocLong())) {
                            idNearBy200List.add(busStopDataModel);
                            //Log.e("API", busStopDataModel.getStop());
                        }
                    }
                    filteredStopList.clear();
                    filteredStopList.addAll(idNearBy200List);

                    //for loop
                    //displayList.clear();
                    int totalCalls = filteredStopList.size();
                    final int[] completedCalls = {0};
                    ArrayList< EtaListModel.EtaModel> tempList = new ArrayList<>();

                    for (BusStopModel.BusStopDataModel busStopDataModel : filteredStopList) {
                        Call<EtaListModel> call = apiService.getEtaById(busStopDataModel.getStop());
                        call.enqueue(new Callback<EtaListModel>() {
                            @Override
                            public void onResponse(Call<EtaListModel> call, Response<EtaListModel> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<EtaListModel.EtaModel> dataList = response.body().getDatalist();
                                    if (dataList != null) {
                                        for (EtaListModel.EtaModel etaModel : dataList) {
                                            if (etaModel.getEtaSeq() == 1 && etaModel.getEta()!=null) {
                                                etaModel.setStopId(busStopDataModel.getStop());
                                                tempList.add(etaModel);
                                            }
                                        }
                                    }
                                } else {
                                    Log.e("API", "Error: " + response.code());
                                }
                                completedCalls[0]++;
                                // Update the adapter once all calls are completed
                                if (completedCalls[0] == totalCalls) {
                                    adapter.updateList(tempList);
                                }
                            }

                            @Override
                            public void onFailure(Call<EtaListModel> call, Throwable t) {
                                Log.e("API", "Failed: " + t.getMessage());
                                completedCalls[0]++;
                                // Update the adapter even if the request fails
                                if (completedCalls[0] == totalCalls) {
                                    adapter.updateList(displayList);
                                }
                            }
                        });
                    }
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.searchBottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.search){
                startActivity(new Intent(this, SearchActivity.class));
            }
            if(item.getItemId() == R.id.fav){
                startActivity(new Intent(this, FavouriteActivity.class));
            }
            if(item.getItemId() == R.id.setting){
                startActivity(new Intent(this, SettingActivity.class));
            }
            return true;
        });

        call.enqueue(new Callback<BusRouteListModel>(){
            @Override
            public void onResponse(Call<BusRouteListModel> call, Response<BusRouteListModel> response) {
                Log.e("API", "Called: " + response.code());
                if (response.isSuccessful()) {
                    BusRouteListModel busRouteData = response.body();
                    for(BusRouteListModel.BusRouteModel r:busRouteData.getRouteList()){
                        busListData.add(r);
                        //displayList.add(new TempEtaObj(r));
                    }
                    busListData.addAll(busListData);
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

        call2.enqueue(new Callback<BusStopModel>(){
            @Override
            public void onResponse(Call<BusStopModel> call, Response<BusStopModel> response) {
                Log.e("API", "Called: " + response.code());
                if (response.isSuccessful()) {
                    BusStopModel busStopModel = response.body();
                    for(BusStopModel.BusStopDataModel busStopDataModel:busStopModel.getBusStopDataModel()){
                        stopList.add(busStopDataModel);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BusStopModel> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }
    private class BusRouteAdapter extends ArrayAdapter<EtaListModel.EtaModel> {
        private ArrayList<EtaListModel.EtaModel> adapList;
        private Context context;

        public BusRouteAdapter(Context context, int textViewResourceId, ArrayList<EtaListModel.EtaModel> adapList) {
            super(context, textViewResourceId, adapList);
            this.adapList = adapList;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.home_list_item, null);
            }
            EtaListModel.EtaModel tempEtaObj = adapList.get(position);
            if (tempEtaObj != null) {
                TextView tv1 = (TextView) v.findViewById(R.id.home_item_route);
                TextView tv2 = (TextView)v.findViewById(R.id.home_item_destiny);
                TextView tv3 = (TextView)v.findViewById(R.id.home_item_min);
                TextView tv4 = (TextView)v.findViewById(R.id.home_item_stop);
                TextView tv5 = (TextView)v.findViewById(R.id.home_item_min_text);

                tv1.setText(tempEtaObj.getRoute());

                Locale locale = getBaseContext().getResources().getConfiguration().locale;
                String country = locale.getCountry();
                switch (country){
                    case "HK":tv2.setText( getString(R.string.to)+" "+tempEtaObj.getDestTc());break;
                    case "US":tv2.setText( getString(R.string.to)+" "+tempEtaObj.getDestEn());break;
                    case "CN":tv2.setText( getString(R.string.to)+" "+tempEtaObj.getDestSc());break;
                }

                if(tempEtaObj != null && tempEtaObj.getEta()!=null){
                    tv3.setText(tempEtaObj.getEta().toString());
                }else{
                    tv3.setText("NA");
                }

                for(BusStopModel.BusStopDataModel busStopDataModel:stopList){
                    if(busStopDataModel.getStop().equals(tempEtaObj.getStopId())){
                        switch (country){
                            case "HK":tv4.setText(busStopDataModel.getNameTc());break;
                            case "US":tv4.setText(busStopDataModel.getNameEn());break;
                            case "CN":tv4.setText(busStopDataModel.getNameSc());break;
                        }

                    }
                }
                //tv4.setText(tempEtaObj.get);
            }

            v.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //Log.e("OnCLick", o.getRoute());
                    Intent intent = new Intent  (context,SearchedBusListActivity.class);
                    intent.putExtra("route", tempEtaObj.getRoute());
                    intent.putExtra("bound", tempEtaObj.getBound());
                    intent.putExtra("type", tempEtaObj.getType());
                    startActivity(intent);
                }
            });
            return v;
        }

        public void updateList(ArrayList<EtaListModel.EtaModel> list){
            adapList.clear();
            adapList.addAll(list);
            notifyDataSetChanged();
        }
    }

    private void fetchEta(){

    }

    private boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ? true : false;
    }

    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }

    static boolean haversine200(double lat1, double lon1, double lat2, double lon2) {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));

        if((rad * c)*1000 < 300){
            return true;
        }
        return false;
    }

}