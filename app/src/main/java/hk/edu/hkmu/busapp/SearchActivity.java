package hk.edu.hkmu.busapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity  {
    private BusApiService apiService;
    private EditText searchBus;

    private ListView busList;
    private ArrayList<BusRouteListModel.BusRouteModel> busListData = new ArrayList<>();
    private ArrayList<BusRouteListModel.BusRouteModel> filteredBusListData = new ArrayList<>();;
    BusRouteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        busList = findViewById(R.id.bus_list);
        searchBus = findViewById(R.id.search_bus);

        //Api call
        apiService = BusApiClient.getApiService();
        Call<BusRouteListModel> call = apiService.getRoutes();

        //SetList
        adapter=new BusRouteAdapter(this,
                android.R.layout.simple_list_item_2,
                filteredBusListData);
        busList.setAdapter(adapter);

        searchBus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("Text", "onTextChanged: "+s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("Text", "afterTextChanged: "+s.toString());
                filterBusData(s.toString());
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.homeBottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.search);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home){
                startActivity(new Intent(this, HomeActivity.class));
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
                    }
                    filteredBusListData.addAll(busListData);
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

    private void filterBusData(String query) {
        filteredBusListData.clear();
        if (query.isEmpty()) {
            filteredBusListData.addAll(busListData);
        } else {
            for (BusRouteListModel.BusRouteModel busInfo : busListData) {
                // 檢查 busInfo 是否包含用戶輸入的字串
                if (busInfo.getRoute().toLowerCase().startsWith(query.toLowerCase())) {
                    filteredBusListData.add(busInfo);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class BusRouteAdapter extends ArrayAdapter<BusRouteListModel.BusRouteModel> {
        private ArrayList<BusRouteListModel.BusRouteModel> busRouteList;
        private Context context;

        public BusRouteAdapter(Context context, int textViewResourceId, ArrayList<BusRouteListModel.BusRouteModel> busRouteList) {
            super(context, textViewResourceId, busRouteList);
            this.busRouteList = busRouteList;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.bus_list, null);
            }
            BusRouteListModel.BusRouteModel o = busRouteList.get(position);
            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.text1);
                TextView bt = (TextView) v.findViewById(R.id.text2);

                Locale locale = getBaseContext().getResources().getConfiguration().locale;
                String country = locale.getCountry();

                if (tt != null) {
                    tt.setText(o.getRoute());
                }

                if(bt != null){
                    switch (country){
                        case "HK":bt.setText( o.getOriginTc() +" "+getString(R.string.to)+" "+o.getDestinationTc());break;
                        case "US":bt.setText( o.getOriginEn() +" "+getString(R.string.to)+" "+o.getDestinationEn());break;
                        case "CN":bt.setText( o.getOriginSc() +" "+getString(R.string.to)+" "+o.getDestinationSc());break;
                    }
                }
            }

            v.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.e("OnCLick", o.getRoute());
                    Intent intent = new Intent  (context,SearchedBusListActivity.class);
                    intent.putExtra("route", o.getRoute());
                    intent.putExtra("bound", o.getBound());
                    intent.putExtra("type", o.getServiceType());
                    startActivity(intent);
                }
            });
            return v;
        }

    }
}


