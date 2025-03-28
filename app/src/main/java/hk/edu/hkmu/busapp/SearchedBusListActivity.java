package hk.edu.hkmu.busapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchedBusListActivity extends AppCompatActivity {
    private BusApiService apiService;
    private RouteStopListModel routeStopList;
    private TempNameListContainerModel tempNameListContainerModel;

    RouteStopAdapter  adapter;
    private List<RouteStopModel> etaMap = new ArrayList<>();

    ExpandableListView lv;

    //Extra
    private String route;
    private String type;
    private String bound;
    Timer timer;

    FavouriteSystem favouriteSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_route);

        Bundle extras = getIntent().getExtras();
        route = extras.getString("route");
        type = extras.getString("type");
        bound = extras.getString("bound");
        //Api called
        apiService = BusApiClient.getApiService();
        //Favourite System
        favouriteSystem = new FavouriteSystem(this);

        String textBound;
        if (bound.equals("I")){
            textBound = "inbound";
        }else{
            textBound = "outbound";
        }
        Log.e("API", route+" "+textBound+" "+type);
        Call<RouteStopListModel> call = apiService.getRouteStop(route,textBound,type);

        //SetList
        lv = findViewById(R.id.lvRoute);
        adapter =new RouteStopAdapter(this, etaMap);
        lv.setAdapter(adapter);


        call.enqueue(new Callback<RouteStopListModel>(){
            @Override
            public void onResponse(Call<RouteStopListModel> call, Response<RouteStopListModel> response) {
                if (response.isSuccessful()) {
                    routeStopList = response.body();
                    Log.e("API", "Api1");
                    for(RouteStopListModel.StopData data: routeStopList.getRouteStopList()){
                        etaMap.add(new RouteStopModel(data.getStopId()));
                    }
                    fetchStopNames(routeStopList.getRouteStopList().size()-1);
                } else {
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RouteStopListModel> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }
    private void fetchStopNames(int counter) {
        if (!(counter >= 0)) {
            // All names fetched, update UI
            adapter.updateList(etaMap);
            timer = new Timer();
            timer.schedule(new etaTask(), 0, 10000);
            return;
        }

        String currentStopId = routeStopList.getRouteStopList().get(counter).getStopId();
        apiService.getStopName(currentStopId).enqueue(new Callback<TempNameListContainerModel>() {
            @Override
            public void onResponse(Call<TempNameListContainerModel> call, Response<TempNameListContainerModel> response) {
                if (response.isSuccessful()) {
                    tempNameListContainerModel = response.body();
                    String NameTc = tempNameListContainerModel.getTempNameListModel().getNameTc();
                    String CurrId = routeStopList.getRouteStopList().get(counter).getStopId();
                    etaMap.get(counter).setStopName(NameTc);
                } else {
                    //stopNameListData.add("Unknown Stop");
                    Log.e("API", "Error: " + response.code());
                }
                // Process next stop
                fetchStopNames(counter-1);
            }

            @Override
            public void onFailure(Call<TempNameListContainerModel> call, Throwable t) {
                //stopNameListData.add("Error loading stop");
                Log.e("API", "Failed: " + t.getMessage());
                // Process next stop even if this one failed
                fetchStopNames(counter-1);
            }
        });
    }

    private class etaTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    apiService.getRouteEta(route,type).enqueue(new Callback<EtaListModel>() {
                        @Override
                        public void onResponse(Call<EtaListModel> call, Response<EtaListModel> response) {
                            if (response.isSuccessful()) {
                                EtaListModel model = response.body();

                                List<EtaListModel.EtaModel> list = model.getDatalist();
                                for (int i = 0; i < list.size(); i++) {
                                    EtaListModel.EtaModel currItem = list.get(i);
                                    int seq = currItem.getSeq();
                                    int etaSeq = currItem.getEtaSeq();
                                    if (etaSeq > 3){
                                        continue;
                                    }
                                    Integer currEta = null;
                                    if(currItem.getEtaTimeStamp() != null){
                                        currEta = currItem.getEta();
                                    }
                                    String currBound = currItem.getBound();
                                    if(currBound.equals(bound)){
                                        Log.e("API", "seq-1: " +(seq-1));
                                        Log.e("API", "etaSeq-1: " +(etaSeq-1));
                                        etaMap.get(seq-1).setBound(currBound);
                                        etaMap.get(seq-1).getStopEta()[etaSeq-1] = currEta;
                                    }
                                }
                                adapter.updateList(etaMap);
                            } else {
                                Log.e("API", "Error: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<EtaListModel> call, Throwable t) {
                            Log.e("API", "Failed: " + t.getMessage());
                        }
                    });
                }
            });
        }
    }

    private class RouteStopAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<RouteStopModel> adapEtaMap;

        public RouteStopAdapter(Context context, List<RouteStopModel> etaMap) {
            this.adapEtaMap = etaMap;
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return adapEtaMap.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return 1;
        }

        @Override
        public  RouteStopModel getGroup(int groupPosition) {
            return adapEtaMap.get(groupPosition);
        }

        @Override
        public Integer[] getChild(int groupPosition, int childPosition) {
            return adapEtaMap.get(groupPosition).getStopEta();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            //RouteNameModel routeNameModel = getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.route_stop_group, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.text1);

            RouteStopModel currGroup = getGroup(groupPosition);
            textView.setText(currGroup.getStopName());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.route_stop_child, null);
            }
            TextView etaView1 = (TextView) convertView.findViewById(R.id.Eta1);
            TextView etaView2 = (TextView) convertView.findViewById(R.id.Eta2);
            TextView etaView3 = (TextView) convertView.findViewById(R.id.Eta3);
            Integer[] currGroup = getChild(groupPosition,childPosition);

            Log.e("API", "Test: " + currGroup[0]);
            Log.e("API", "Test: " + currGroup[1]);
            Log.e("API", "Test: " + currGroup[2]);

            if (currGroup != null && getGroup(groupPosition).getBound().equals(bound)) {
                etaView1.setText(currGroup[0] + " 分鐘");
            } else if (currGroup == null) {
                etaView1.setText("暫時未有班次");
            }

            if (currGroup != null && getGroup(groupPosition).getBound().equals(bound)) {
                etaView2.setText(currGroup[1] + " 分鐘");
            } else if (currGroup == null) {
                etaView2.setText("");
            }

            if (currGroup != null && getGroup(groupPosition).getBound().equals(bound)) {
                etaView3.setText(currGroup[2] + " 分鐘");
            } else if (currGroup == null) {
                etaView3.setText("");
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public void updateList(List<RouteStopModel> list){
            this.adapEtaMap = list;
            notifyDataSetChanged();
        }
    }

    private class RouteStopModel{
        private String stopId;
        private String stopName;
        private Integer[] stopEta;
        private String bound;

        public RouteStopModel(String stopId) {
            this.stopId = stopId;
            this.stopEta = new Integer[3];
        }

        public String getStopId() {
            return stopId;
        }

        public String getStopName() {
            return stopName;
        }

        public Integer[] getStopEta() {
            return stopEta;
        }

        public String getBound() {
            return bound;
        }

        public void setStopId(String stopId) {
            this.stopId = stopId;
        }

        public void setStopName(String stopName) {
            this.stopName = stopName;
        }

        public void setStopEta(Integer[] stopEta) {
            this.stopEta = stopEta;
        }

        public void setBound(String bound) {
            this.bound = bound;
        }
    }
}

