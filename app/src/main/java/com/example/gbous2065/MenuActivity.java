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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.gbous2065.Models.CustomCallback;
import com.example.gbous2065.Models.SubUnsubCombine;
import com.example.gbous2065.Utils.NetworkDownload;
import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView logoImage;
    SharedPreferences sharedPref;
    String savedLogin, savedPass;

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

        sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        savedLogin = sharedPref.getString("login", "");
        savedPass = sharedPref.getString("pass", "");

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

        if(!savedLogin.equals("") && !savedPass.equals("")){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_logout);

            //navigationView.setCheckedItem(R.id.nav_account);
            NetworkDownload.getDataAndGo(this, getSupportFragmentManager(), navigationView, "cache", new CustomCallback() {
                @Override
                public void onSuccess(SubUnsubCombine value) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new UserAccountFragment(value.getSubscribedDocs(), value.getUnsubscribedDocs())).commit();
                    toolbar.setTitle("Документы");
                }

                @Override
                public void onFailure() {

                }
            });
        }
        else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_login);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_news);
            toolbar.setTitle("Новости");
        }


    }

//    public void setVisibilityOfMenuItem(Integer itemId, Boolean visible){
//        Menu menu = navigationView.getMenu();
//        for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
//            MenuItem menuItem= menu.getItem(menuItemIndex);
//            if(menuItem.hasSubMenu()) {
//                Menu nestedMenu = menuItem.getSubMenu();
//                for(int i=0; i<nestedMenu.size(); i++) {
//                    MenuItem nestedMenuItem = nestedMenu.getItem(i);
//                    if (nestedMenuItem.getItemId() == itemId) {
//                        nestedMenuItem.setVisible(visible);
//                    }
//                }
//            }
//        }
//        navigationView.getMenu().findItem(itemId).setVisible(visible);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_employee:
                NetworkDownload.getDataAndGo(this, getSupportFragmentManager(), navigationView, "cache", new CustomCallback() {
                    @Override
                    public void onSuccess(SubUnsubCombine value) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new UserAccountFragment(value.getSubscribedDocs(), value.getUnsubscribedDocs())).commit();
                        String fullName = value.getFullName();
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.menu_account);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
                break;
            case R.id.nav_exit:
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("login", "");
                editor.putString("pass", "");
                editor.apply();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment(navigationView)).commit();
                toolbar.setTitle("Вход");
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.menu_login);
                item.setChecked(true);
                break;
            case R.id.nav_login:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment(navigationView)).commit();
                toolbar.setTitle("Вход");
                break;
            case R.id.nav_news:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewsFragment()).commit();
                toolbar.setTitle("Новости");
//                navigationView.getMenu().clear();
//                navigationView.inflateMenu(R.menu.menu_logout);
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
                        }
                        else{
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            saveState(false);
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