package hk.edu.hkmu.busapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteActivity extends AppCompatActivity {
    private BusApiService apiService;

    //Set List
    private ArrayList<TempFavListItem> favListItemList = new ArrayList<>();;
    FavouriteActivity.FavListAdapter adapter;

    //Favourite system
    FavouriteSystem favouriteSystem;

    Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        //Favourite system
        favouriteSystem = new FavouriteSystem(this);

        //SetList
        ListView favListView=findViewById(R.id.fav_list);

        //Api call
        apiService = BusApiClient.getApiService();

        for(int i = 0;i<favouriteSystem.getFavouriteRoute().size();i++){
            favListItemList.add(new TempFavListItem(favouriteSystem.getFavouriteRoute().get(i)));
        }

        adapter=new FavouriteActivity.FavListAdapter(this,
                R.layout.fav_list_item,
                favListItemList);
        favListView.setAdapter(adapter);


        BottomNavigationView bottomNavigationView = findViewById(R.id.favBottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.fav);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home){
                startActivity(new Intent(this, HomeActivity.class));
            }
            if(item.getItemId() == R.id.search){
                startActivity(new Intent(this, SearchActivity.class));
            }
            if(item.getItemId() == R.id.setting){
                startActivity(new Intent(this, SettingActivity.class));
            }
            return true;
        });

        fetchDestNames(favouriteSystem.getFavouriteRoute().size()-1);
        //fetchStopNames(favouriteSystem.getFavouriteRoute().size()-1);
    }

    private class FavListAdapter extends ArrayAdapter<TempFavListItem> {
        private Context context;
        List<TempFavListItem> adapList;

        public FavListAdapter(Context context, int textViewResourceId, ArrayList<TempFavListItem> favItemArrayList) {
            super(context, textViewResourceId, favItemArrayList);
            adapList = favItemArrayList;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.fav_list_item, null);
            }

            TempFavListItem o =  adapList.get(position);
            if (o != null) {
                TextView tv1 = (TextView) v.findViewById(R.id.fav_item_route);
                TextView tv2 = (TextView)v.findViewById(R.id.fav_item_destiny);
                TextView tv3 = (TextView)v.findViewById(R.id.fav_item_min);
                TextView tv4 = (TextView)v.findViewById(R.id.fav_item_stop);
                TextView tv5 = (TextView)v.findViewById(R.id.fav_item_min_text);
                Button btn = (Button) v.findViewById(R.id.fav_btn_delete);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favouriteSystem.deleteFavouriteRoute(o.getFavouriteSystemItem());
                        favListItemList.remove(position);
                        updateList(favListItemList);
                        notifyDataSetChanged();
                    }
                });

                v.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent  (context,SearchedBusListActivity.class);
                        intent.putExtra("route", o.getFavouriteSystemItem().getRoute());
                        intent.putExtra("bound", o.getFavouriteSystemItem().getRoute());
                        intent.putExtra("type", o.getFavouriteSystemItem().getRoute());
                        startActivity(intent);
                    }
                });

                tv1.setText(o.getFavouriteSystemItem().getRoute());

                if(o.getDestNameModel().getDestTc() !=null){
                    tv2.setText("往"+o.getDestNameModel().getDestTc());
                }else {
                    tv2.setText("");
                }

                if(o.getEtaModel() !=null && o.getEtaModel().getEta() != null){
                    tv3.setText(o.getEtaModel().getEta().toString());
                }else {
                    tv3.setText("NA");
                }

                if(o.getStopNameModel().getNameTc()!=null){
                    tv4.setText(o.getStopNameModel().getNameTc());
                }else {
                    tv4.setText("");
                }

                tv5.setText("分鐘");
            }
            return v;
        }

        public void updateList(List<TempFavListItem> list){
            adapList = list;
            notifyDataSetChanged();
        }

    }

    private void fetchDestNames(int counter) {
        Log.e("LOG","fetchDestNames() "+counter);
        if (!(counter >= 0)) {
            fetchStopNames(favouriteSystem.getFavouriteRoute().size()-1);
            adapter.notifyDataSetChanged();
            return;
        }

        String currentRoute = favListItemList.get(counter).getFavouriteSystemItem().getRoute();
        String currentBound = favListItemList.get(counter).getFavouriteSystemItem().getBound();
        String textBound;
        if (currentBound.equals("I")){
            textBound = "inbound";
        }else{
            textBound = "outbound";
        }
        String currentType = favListItemList.get(counter).getFavouriteSystemItem().getType();
        apiService.getDestName(currentRoute,textBound,currentType).enqueue(new Callback<DestNameContainerModel>() {
            @Override
            public void onResponse(Call<DestNameContainerModel> call, Response<DestNameContainerModel> response) {
                if (response.isSuccessful()) {
                    DestNameContainerModel destNameContainerModel = response.body();
                    String s1 = destNameContainerModel.getDestNameModel().getDestTc();
                    String s2 = destNameContainerModel.getDestNameModel().getDestEn();
                    String s3 = destNameContainerModel.getDestNameModel().getDestSc();
                    //DestNameContainerModel.DestNameModel o = new DestNameContainerModel.DestNameModel();
                    //o.setContent(s1,s2,s3);
                    favListItemList.get(counter).getDestNameModel().setContent(s1,s2,s3);
                    adapter.updateList(favListItemList);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Error: " + response.code());
                }
                // Process next stop
                fetchDestNames(counter-1);
            }

            @Override
            public void onFailure(Call<DestNameContainerModel> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
                fetchDestNames(counter-1);
            }
        });
    }

    private void fetchStopNames(int counter) {
       Log.e("LOG","fetchStopNames() "+counter);
        if (!(counter >= 0)) {
            for(int i=0;i<favListItemList.size();i++){
                String currSttopId = favListItemList.get(i).getFavouriteSystemItem().getStopId();
                String currRoute = favListItemList.get(i).getFavouriteSystemItem().getRoute();
                String currType = favListItemList.get(i).getFavouriteSystemItem().getType();

                timer= new Timer();
                timer.schedule(new FavouriteActivity.etaTask(currSttopId,currRoute,currType), 0, 10000);
            }

            return;
        }

        Log.e("LOG", "counter: " +counter );
        String currentStopId = favListItemList.get(counter).getFavouriteSystemItem().getStopId();
        apiService.getStopName(currentStopId).enqueue(new Callback<TempNameListContainerModel>() {
            @Override
            public void onResponse(Call<TempNameListContainerModel> call, Response<TempNameListContainerModel> response) {
                if (response.isSuccessful()) {
                    TempNameListContainerModel tempNameListContainerModel = response.body();
                    favListItemList.get(counter).setStopNameModel(tempNameListContainerModel.getTempNameListModel());
                    adapter.updateList(favListItemList);
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
        private String stopId;
        private String route;
        private String type;

        public etaTask(String stopId, String route, String type) {
            super();
            this.stopId = stopId;
            this.route = route;
            this.type = type;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    apiService.getRouteStopEta(stopId,route,type).enqueue(new Callback<EtaListModel>() {
                        @Override
                        public void onResponse(Call<EtaListModel> call, Response<EtaListModel> response) {
                            if (response.isSuccessful()) {
                                EtaListModel etaListModel = response.body();
                                List<EtaListModel.EtaModel> list = etaListModel.getDatalist();

                                for(int i=0;i<list.size();i++){

                                    EtaListModel.EtaModel etaModel = list.get(i);

                                    String currTimeStamp = etaModel.getEtaTimeStamp();
                                    String currId = etaModel.getStopId();
                                    String currRoute = etaModel.getRoute();
                                    String currBound = etaModel.getBound();
                                    String currType = etaModel.getType();
                                    int currSeq = etaModel.getSeq();

                                    for(int j=0;j<favListItemList.size();j++){
                                        TempFavListItem tempFavListItem = favListItemList.get(j);
                                        FavouriteSystem.FavouriteSystemItem favouriteSystemItem = tempFavListItem.getFavouriteSystemItem();

                                        boolean isSame =
                                        /*favouriteSystemItem.getStopId().equals(currId) &&*/
                                                 favouriteSystemItem.getRoute().equals(currRoute)
                                                && favouriteSystemItem.getBound().equals(currBound)
                                                && favouriteSystemItem.getType().equals(currType)
                                                && favouriteSystemItem.getSeq() ==currSeq
                                                && etaModel.getEtaSeq() == 1;

                                        Log.e("API", favouriteSystemItem.getStopId()+" == "+currId );
                                        Log.e("API", favouriteSystemItem.getRoute()+" == "+currRoute );
                                        Log.e("API", favouriteSystemItem.getBound()+" == "+currBound );
                                        Log.e("API", favouriteSystemItem.getType()+" == "+currType );
                                        Log.e("API", favouriteSystemItem.getSeq()+" == "+currSeq );
                                        Log.e("API", String.valueOf(isSame));
                                        if(isSame){
                                            EtaListModel.EtaModel data = new EtaListModel.EtaModel();
                                            data.setEtaTimeStamp(currTimeStamp);
                                            data.setStopId(currId);
                                            data.setRoute(currRoute);
                                            data.setBound(currBound);
                                            data.setType(currType);
                                            favListItemList.get(j).setEtaModel(data);

                                            adapter.updateList(favListItemList);
                                        }
                                    }
                                }

                                Log.e("API", route);
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

    class TempFavListItem{
        private TempNameListContainerModel.TempNameListModel stopNameModel;
        private FavouriteSystem.FavouriteSystemItem favouriteSystemItem;
        private DestNameContainerModel.DestNameModel destNameModel;
        private EtaListModel.EtaModel etaModel;

        public TempFavListItem(FavouriteSystem.FavouriteSystemItem favouriteSystemItem) {
            this.favouriteSystemItem = favouriteSystemItem;
            this.stopNameModel = new TempNameListContainerModel.TempNameListModel();
            this.destNameModel = new DestNameContainerModel.DestNameModel();
            this.etaModel = new EtaListModel.EtaModel();
        }

        public TempNameListContainerModel.TempNameListModel getStopNameModel() {
            return stopNameModel;
        }

        public FavouriteSystem.FavouriteSystemItem getFavouriteSystemItem() {
            return favouriteSystemItem;
        }

        public DestNameContainerModel.DestNameModel getDestNameModel() {
            return destNameModel;
        }

        public void setStopNameModel(TempNameListContainerModel.TempNameListModel stoptNameModel) {
            this.stopNameModel = stoptNameModel;
        }

        public void setDestNameModel(DestNameContainerModel.DestNameModel destNameModel) {
            this.destNameModel = destNameModel;
        }

        public EtaListModel.EtaModel getEtaModel() {
            return etaModel;
        }

        public void setEtaModel(EtaListModel.EtaModel etaModel) {
            this.etaModel = etaModel;
        }
    }
}
