package com.example.gbous2065;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(loadState() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkTheme_GBOUS2065);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.Theme_GBOUS2065);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(loadState() == true){
            navigationView.inflateHeaderView(R.layout.header_black);
        }
        else{
            navigationView.inflateHeaderView(R.layout.header);
        }

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewsFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_news);
        toolbar.setTitle("Новости");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_news:
//                startActivity(new Intent(this, MainActivity.class));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewsFragment()).commit();
                toolbar.setTitle("Новости");
                break;
            case R.id.nav_schedule:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScheduleFragment()).commit();
                toolbar.setTitle("Расписание занятий");
                break;
            case R.id.nav_food_menu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
                toolbar.setTitle("Меню столовой");
                break;
            case R.id.nav_contacts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactFragment()).commit();
                toolbar.setTitle("Контакты");
                break;
            case R.id.nav_dark_mode:
                item.setActionView(R.layout.theme_switch);
                Switch themeSwitch = item.getActionView().findViewById(R.id.action_switch);
                if(loadState() == true){
                    themeSwitch.setChecked(true);
                }
                themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            saveState(true);
                            //recreate();
                        }
                        else{
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            saveState(false);
                            //recreate();
                        }
                    }
                });
                return false;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void saveState(Boolean state){
        SharedPreferences sharedPreferences = getSharedPreferences("nightMode", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("nightMode", state);
        editor.apply();
    }

    private  Boolean loadState(){
        SharedPreferences sharedPreferences = getSharedPreferences("nightMode", MODE_PRIVATE);
        Boolean state = sharedPreferences.getBoolean("nightMode", false);
        return state;
    }

}