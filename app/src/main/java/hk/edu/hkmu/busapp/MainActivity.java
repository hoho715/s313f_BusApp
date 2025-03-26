package hk.edu.hkmu.busapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText searchBus;
    private ListView busList;
    private ArrayList<String> busData; // 用於存儲所有巴士資料
    private ArrayList<String> filteredBusData; // 用於存儲過濾後的巴士資料
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        /*
        searchBus = findViewById(R.id.search_bus);
        busList = findViewById(R.id.bus_list);

        // 初始化巴士資料
        busData = new ArrayList<>();
        filteredBusData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredBusData);
        busList.setAdapter(adapter);

        // 開始獲取巴士資料
        new FetchBusDataTask().execute("https://data.etabus.gov.hk/v1/transport/kmb/route/");

        // 監聽搜尋欄的變化
        searchBus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterBusData(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });*/
    }

    private void filterBusData(String query) {
        filteredBusData.clear();
        if (query.isEmpty()) {
            filteredBusData.addAll(busData);
        } else {
            for (String busInfo : busData) {
                // 檢查 busInfo 是否包含用戶輸入的字串
                if (busInfo.toLowerCase().startsWith(query.toLowerCase())) {
                    filteredBusData.add(busInfo);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class FetchBusDataTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            ArrayList<String> result = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // 處理 JSON 數據
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray routes = jsonResponse.getJSONArray("data");

                    for (int i = 0; i < routes.length(); i++) {
                        JSONObject route = routes.getJSONObject(i);
                        String busInfo = route.getString("route") + " 巴士  " + route.getString("orig_tc") + " 至 " + route.getString("dest_tc");
                        result.add(busInfo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> busInfo) {
            // 更新所有巴士資料
            busData.clear();
            busData.addAll(busInfo);
            filteredBusData.addAll(busData); // 初始化過濾數據
            adapter.notifyDataSetChanged();
        }
    }
}