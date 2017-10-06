package com.beanlife;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beanlife.act.ActVO;
import com.beanlife.act.Act_pairVO;
import com.beanlife.act.ActivityFragment;
import com.beanlife.act.ActivityPageWithTab;
import com.beanlife.act.MemberHostActivityContFragment;
import com.beanlife.act.MemberHostActivityScanFragment;
import com.beanlife.cart.CartFragment;
import com.beanlife.mem.MemberCenterFragment;
import com.beanlife.ord.OrderFragment;
import com.beanlife.prod.ProductTotalFragment;
import com.beanlife.search.SearchFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissions();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //顯示掃描QRCode結果
        if(requestCode == IntentIntegrator.REQUEST_CODE){
            IntentResult intentResult  = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            TextView hostScanTv = (TextView) findViewById(R.id.host_scan_result_tv);
            ImageView hostScanIv =  (ImageView) findViewById(R.id.host_scan_result_iv);
            Act_pairVO act_pairVO = new Act_pairVO();
            String getScanResult = intentResult.getContents();
            SharedPreferences loginState = getSharedPreferences(Common.SCAN_STATE, MODE_PRIVATE);
            String act_no= loginState.getString("act_no", "no act_no");

            //QRCode為Beanlife專用
            if(getScanResult.startsWith("Beanlife")){
                String[] resultString = getScanResult.split(",");
                String actVOString = "";
                if(resultString[1].equals(act_no)){
                    //查詢會員
                    CommonTask retrieveActVO = (CommonTask) new CommonTask().execute(Common.ACT_URL, "updateActPair", "act_no" ,
                            resultString[1], "mem_ac", resultString[2]);
                    try {
                        actVOString = retrieveActVO.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<Act_pairVO>(){}.getType();
                    act_pairVO = gson.fromJson(actVOString, listType);

                    hostScanTv.setText(act_pairVO.getMem_ac() + " " + act_pairVO.getChk_state());
                    new GetImageByPkTask(Common.MEM_URL, "mem_ac", resultString[2], 300, hostScanIv).execute();

                    Log.d("Scan" , intentResult.getContents());
                } else {
                    hostScanTv.setText("錯誤的活動配對");
                }
            } else {
                hostScanTv.setText("錯誤的QRCode");
            }
        }


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
                        switchFragment(new ProductTotalFragment());
                        setTitle(R.string.text_Market);
                        break;

                    case R.id.item_Activity:
                        switchFragment(new ActivityFragment());
                        setTitle(R.string.text_Activity);
                        break;

                    case R.id.item_About:
                        switchFragment(new ProductTotalFragment());
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
                        switchFragment(new ProductTotalFragment());
                        setTitle(R.string.text_Market);
                        break;
                }
                return true;
            }
        });
    }

    private void initBody(){
        switchFragment(new ProductTotalFragment());
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
        super.onDestroy();
        logOut();
    }

    private static final int REQ_PERMISSIONS = 0;

    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    REQ_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSIONS:
                String text = "";
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        text += permissions[i] + "\n";
                    }
                }
                if (!text.isEmpty()) {
                    text += getString(R.string.text_NotGranted);
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
