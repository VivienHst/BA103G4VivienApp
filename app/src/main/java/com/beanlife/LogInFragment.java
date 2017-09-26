package com.beanlife;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by vivienhuang on 2017/9/18.
 */

public class LogInFragment extends Fragment {
    private View view;
    private EditText memAcTv, memPswTv;
    private Button registerBt, logInBt, magicBt;
    MenuItem memCenter;
    private RetrieveMemTask retrieveMemTask;
    private boolean isValid;
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.login_layout, container, false);
        findView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //設定可以控制actionBar標題
        setHasOptionsMenu(true);
    }

    private void findView(){
        memAcTv = (EditText) view.findViewById(R.id.userAc);
        memPswTv = (EditText) view.findViewById(R.id.userPsw);
        registerBt = (Button) view.findViewById(R.id.register);
        logInBt = (Button) view.findViewById(R.id.logIn);
        magicBt = (Button) view.findViewById(R.id.magicButton);
        //
        magicBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memAcTv.setText("mrbrown");
                memPswTv.setText("mr333");
            }
        });

        logInBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userAc = memAcTv.getText().toString().trim();
                String userPsw = memPswTv.getText().toString().trim();

                if(userAc.isEmpty() || userPsw.isEmpty()){
                    Toast.makeText(view.getContext(), "請輸入帳號與密碼", Toast.LENGTH_SHORT).show();

                } else if(isUserValid(userAc, userPsw)){
                    Toast.makeText(view.getContext(), "您好,"+ userAc, Toast.LENGTH_SHORT).show();
                    //設定可以看見會員頁面
                    NavigationView view_start = (NavigationView) getActivity().findViewById(R.id.navigation_start);

                    view_start.getMenu().findItem(R.id.memCenter).setVisible(true);
                    View headerNv = getActivity().findViewById(R.id.mem_nvHeader);
                    view_start.getMenu().findItem(R.id.nvMemLogin).setVisible(false);

                    String action = "mem_ac";
                    ImageView memPicIv = (ImageView) getActivity().findViewById(R.id.memPic_nvHeader);
                    TextView memAcTv = (TextView) getActivity().findViewById(R.id.memAc_nvHeader);
                    headerNv.setVisibility(View.VISIBLE);
                    new GetImageByPkTask(Common.MEM_URL, action, userAc, 120, memPicIv).execute();
                    memAcTv.setText(userAc);



                    //存入偏好設定
                    SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
                    loginState.edit().putBoolean("login", true).putString("userAc", userAc).putString("userPsw", userPsw).apply();

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.body, new ProductTotalFragmentTest());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else{
                    Toast.makeText(view.getContext(), "錯誤的帳號或密碼", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        this.menu = menu;
//
//    }

    private boolean networkConnected(){
        ConnectivityManager conManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //檢查帳號密碼
    private boolean isUserValid(String userAc, String userPsw){

        String action = "logIn";
        if (networkConnected()){
            retrieveMemTask = (RetrieveMemTask) new RetrieveMemTask(action, userAc, userPsw).execute(Common.MEM_URL);

            try {
                isValid = (boolean) retrieveMemTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return isValid;
    }
}
