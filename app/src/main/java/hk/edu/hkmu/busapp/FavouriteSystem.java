package hk.edu.hkmu.busapp;



import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavouriteSystem {
    SharedPreferences sharedPref;
    Gson gson;
    Context context;

    public static class FavouriteSystemItem{
        private String route;
        private String type;
        private String bound;
        private String stopId;
        private int seq;

        public FavouriteSystemItem(String route, String type, String bound, String stopId,int seq) {
            this.route = route;
            this.type = type;
            this.bound = bound;
            this.stopId = stopId;
            this.seq = seq;
        }

        public String getRoute() {
            return route;
        }

        public String getType() {
            return type;
        }

        public String getBound() {
            return bound;
        }

        public String getStopId() {
            return stopId;
        }

        public int getSeq() {
            return seq;
        }
    }

    public FavouriteSystem(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("BUS_FAVORITES_PREFS", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void setFavouriteRoute(FavouriteSystemItem favouriteSystemItem){
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        String json = gson.toJson(favouriteSystemItem);

        Map<String, ?> allFavourite = sharedPref.getAll();

        String checkey = favouriteSystemItem.getStopId()+"|"+favouriteSystemItem.getRoute()
                +"|"+favouriteSystemItem.getBound()+"|"+favouriteSystemItem.getType();
        for (Map.Entry<String, ?> entry : allFavourite.entrySet()) {
            if(entry.getKey().toString().equals(checkey)){
                Toast.makeText(context, context.getString(R.string.add_fav), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        prefsEditor.putString(checkey, json);
        Log.e("Fav", "Add cuccesfully");
        Toast.makeText(context, context.getString(R.string.already_fav), Toast.LENGTH_SHORT).show();
        prefsEditor.commit();
    }

    public ArrayList<FavouriteSystemItem> getFavouriteRoute(){
        Map<String, ?> allFavourite = sharedPref.getAll();
        ArrayList<FavouriteSystemItem> list = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allFavourite.entrySet()) {
            FavouriteSystemItem item = gson.fromJson(entry.getValue().toString(),FavouriteSystemItem.class);
            list.add(item);
        }
        return list;
    }

    public void deleteFavouriteRoute(FavouriteSystemItem favouriteSystemItem){
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        String key = favouriteSystemItem.getStopId()+"|"+favouriteSystemItem.getRoute()
                +"|"+favouriteSystemItem.getBound()+"|"+favouriteSystemItem.getType();
        prefsEditor.remove(key);
        Toast.makeText(context, "移除收藏", Toast.LENGTH_SHORT).show();
        prefsEditor.commit();
    }

    public void clearSystem(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

}
