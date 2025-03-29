package hk.edu.hkmu.busapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FavouriteActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

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
    }
}
