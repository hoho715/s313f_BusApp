package hk.edu.hkmu.busapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        BottomNavigationView bottomNavigationView = findViewById(R.id.settingBottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.setting);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home){
                startActivity(new Intent(this, HomeActivity.class));
            }
            if(item.getItemId() == R.id.search){
                startActivity(new Intent(this, SearchActivity.class));
            }
            if(item.getItemId() == R.id.fav){
                startActivity(new Intent(this, FavouriteActivity.class));
            }
            return true;
        });


            Spinner spinner = findViewById(R.id.language_spinner);
            //String[] items = {"Item 1", "Item 2", "Item 3"};
            List<LanguageSpinnerItem> items = new ArrayList<>();

            Locale locale = getBaseContext().getResources().getConfiguration().locale;
            String country = locale.getCountry();
            Log.e("LOG",locale.toLanguageTag());
            switch (country) {
                case "HK":
                    items.add(0,new LanguageSpinnerItem(getString(R.string.lan_hk),"zh-HK"));
                    items.add(1,new LanguageSpinnerItem(getString(R.string.lan_en),"en-US"));
                    items.add(2,new LanguageSpinnerItem(getString(R.string.lan_zh),"zh-CN"));
                    break;
                case "US":
                    items.add(0,new LanguageSpinnerItem(getString(R.string.lan_en),"en-US"));
                    items.add(1,new LanguageSpinnerItem(getString(R.string.lan_hk),"zh-HK"));
                    items.add(2,new LanguageSpinnerItem(getString(R.string.lan_zh),"zh-CN"));
                    break;
                case "CN":
                    items.add(0,new LanguageSpinnerItem(getString(R.string.lan_zh),"zh-CN"));
                    items.add(1,new LanguageSpinnerItem(getString(R.string.lan_en),"en-US"));
                    items.add(2,new LanguageSpinnerItem(getString(R.string.lan_hk),"zh-HK"));
                    break;
            }

            ArrayAdapter<LanguageSpinnerItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerInteractionListener listener = new SpinnerInteractionListener();
            spinner.setOnTouchListener(listener);
            spinner.setOnItemSelectedListener(listener);
            spinner.setAdapter(adapter);
            spinner.setSelection(0,false);

        findViewById(R.id.dark_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Switch to light mode
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Switch to dark mode
                }
            }
        });
    }

    public void setLocale(String lang) {
        Locale locale = Locale.forLanguageTag(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        startActivity(getIntent());
        finish();
    }

    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (userSelect) {
                LanguageSpinnerItem selectedItem = (LanguageSpinnerItem)parent.getItemAtPosition(pos);
                String hiddenValue = selectedItem.getHiddenValue();
                setLocale(hiddenValue);
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    }

    public class LanguageSpinnerItem {
        private String displayValue;
        private String hiddenValue;

        public LanguageSpinnerItem(String displayValue,String hiddenValue) {
            this.displayValue = displayValue;
            this.hiddenValue = hiddenValue;
        }

        public String getDisplayValue() {
            return displayValue;
        }

        public String getHiddenValue() {
            return hiddenValue;
        }

        @Override
        public String toString() {
            return displayValue; // This will be displayed in the Spinner
        }
    }
}
