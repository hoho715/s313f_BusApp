package hk.edu.hkmu.busapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    }
}