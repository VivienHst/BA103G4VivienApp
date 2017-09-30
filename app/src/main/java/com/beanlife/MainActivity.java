package com.beanlife;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.beanlife.act.ActivityFragment;
import com.beanlife.act.ActivityPageWithTab;
import com.beanlife.cart.CartFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpActionBar();
        initDrawer();
        initBody();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.container);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
        } else {
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
        }
        actionBarDrawerToggle.syncState(); //預設動態選單>>漢堡

        NavigationView view_start = (NavigationView) findViewById(R.id.navigation_start);

        view_start.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.item_Market:
                        switchFragment(new ProductTotalFragmentTest());
                        setTitle(R.string.text_Market);
                        break;

                    case R.id.item_Activity:
                        switchFragment(new ActivityFragment());
                        setTitle(R.string.text_Activity);
                        break;

                    case R.id.item_About:
                        switchFragment(new ProductTotalFragmentTest());
                        setTitle(R.string.text_ＡboutUs);
                        break;

                    case R.id.nvMemData:
                        switchFragment(new MemberCenterFragment());
                        setTitle(R.string.text_Member);
                        break;

                    case R.id.item_Map:
                        switchFragment(new MapFragment());
                        setTitle(R.string.text_Map);
                        break;

                    case R.id.nvMemOrd:
                        switchFragment(new OrderFragment());
                        setTitle("訂單查詢");
                        break;

                    case R.id.nvAct:
                        switchFragment(new ActivityPageWithTab());
                        setTitle(R.string.text_MyActivity);
                        break;

                    case R.id.nvMemLogin:
                        switchFragment(new LogInFragment());
                        setTitle("BeanLife");
                        break;

                    case R.id.nvMemLogout:
                        logOut();
                        switchFragment(new ProductTotalFragmentTest());
                        setTitle(R.string.text_Market);
                        break;
                }
                return true;
            }
        });
    }

    private void initBody(){
        switchFragment(new ProductTotalFragmentTest());
        setTitle(R.string.text_Market);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.action_search:
                switchFragment(new SearchFragment());
                return true;

            case R.id.action_shopping_car:
                switchFragment(new CartFragment());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void loginOnClick(View view) {
        setContentView(R.layout.activity_main);
        setUpActionBar();
        initDrawer();
        initBody();
    }

    private void logOut(){
        //設定登入畫面不可見
        NavigationView view_start = (NavigationView) findViewById(R.id.navigation_start);
        view_start.getMenu().findItem(R.id.memCenter).setVisible(false);
        View headerNv = findViewById(R.id.mem_nvHeader);
        view_start.getMenu().findItem(R.id.nvMemLogin).setVisible(true);
        headerNv.setVisibility(View.GONE);
        //刪除偏好設定中登入資訊
        SharedPreferences loginState = getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        loginState.edit().putBoolean("login", false).putString("userAc", null)
                .putString("userPsw", null).apply();
    }

    @Override
    protected void onDestroy() {
        logOut();
        super.onDestroy();
    }
}
