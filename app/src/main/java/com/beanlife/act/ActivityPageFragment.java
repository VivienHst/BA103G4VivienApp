package com.beanlife.act;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.beanlife.cart.CartFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vivienhuang on 2017/9/5.
 * 活動內頁
 */

public class ActivityPageFragment extends Fragment {
    private View view;
    private TextView actNameTv, actFollowNumTv, memAcTv, actOpDateTv, actMemCountTv, actAddTv, actContTv;
    private ImageView activityIv, actFollowIconIv;
    private ActVO actVO;
    private GoogleMap googleMap;
    private LatLng actLocation;
    private MapView actMv;
    private CommonTask retrieveFoCount, retrieveIsFollow, retrieveAddFollow, retrieveDeleteFollow;
    private String mem_ac;
    private boolean isFollowed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        actVO = (ActVO) getArguments().getSerializable("actVO");

        view = inflater.inflate(R.layout.activity_page_fragment, container, false);
        findView();

        setUpMap(savedInstanceState);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView() {

        activityIv = (ImageView) view.findViewById(R.id.actPageIv);
        String action = "act_no";
        new GetImageByPkTask(Common.ACT_URL, action, actVO.getAct_no(), 500, activityIv).execute();

        actNameTv = (TextView) view.findViewById(R.id.actName);
        actFollowNumTv = (TextView) view.findViewById(R.id.actFollowNum);
        memAcTv = (TextView) view.findViewById(R.id.memAc);
        actOpDateTv = (TextView) view.findViewById(R.id.actOpDate);
        actMemCountTv = (TextView) view.findViewById(R.id.actMemCount);
        actAddTv = (TextView) view.findViewById(R.id.actAdd);
        actContTv = (TextView) view.findViewById(R.id.actCont);
        actFollowIconIv = (ImageView) view.findViewById(R.id.actFollowIcon);

        actNameTv.setText(actVO.getAct_name());

        actFollowNumTv.setText(getFoCount(actVO.getAct_no()));
        memAcTv.setText(actVO.getMem_ac());
        actOpDateTv.setText(actVO.getAct_op_date());
        actMemCountTv.setText(actVO.getMem_count().toString());
        actAddTv.setText(actVO.getAct_add());
        actContTv.setText(actVO.getAct_cont());
        actFollowIconIv.setImageResource(R.drawable.like_no);
        onClickFollow();



    }
    private void onClickFollow(){
        if (isLogIn()) {
            actFollowIconIv.setClickable(true);

            if (isFollowed(actVO.getAct_no())) {
                actFollowIconIv.setImageResource(R.drawable.like_yes);
                actFollowIconIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actFollowIconIv.setImageResource(R.drawable.like_no);
                        retrieveDeleteFollow = (CommonTask) new CommonTask().execute(Common.ACT_URL,
                                "deleteFoAct", "mem_ac", mem_ac, "act_no", actVO.getAct_no());
                        actFollowNumTv.setText(getFoCount(actVO.getAct_no()));
                        onClickFollow();
                    }
                });

            } else {
                actFollowIconIv.setImageResource(R.drawable.like_no);
                actFollowIconIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actFollowIconIv.setImageResource(R.drawable.like_yes);
                        retrieveAddFollow = (CommonTask) new CommonTask().execute(Common.ACT_URL,
                                "addFoAct", "mem_ac", mem_ac, "act_no", actVO.getAct_no());
                        actFollowNumTv.setText(getFoCount(actVO.getAct_no()));
                        onClickFollow();
                    }
                });
            }
        }
    }

    private void setUpMap(Bundle savedInstanceState) {
        actMv = (MapView) view.findViewById(R.id.act_mapView);
        actMv.onCreate(savedInstanceState);
        actMv.onResume();
        MapsInitializer.initialize(getActivity());
        googleMap = actMv.getMap();
        actLocation = new LatLng(Double.parseDouble(actVO.getAct_add_lat()), Double.parseDouble(actVO.getAct_add_lon()));

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(actLocation).zoom(16).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.addMarker(new MarkerOptions().position(actLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_marker)));
        googleMap.moveCamera(cameraUpdate);
    }

    private String getFoCount(String act_no) {
        String count = "";
        retrieveFoCount = (CommonTask) new CommonTask().execute(Common.ACT_URL, "getFoCount", "act_no", act_no);

        try {
            count = retrieveFoCount.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<String>() {}.getType();
        return gson.fromJson(count, listType);
    }

    private boolean isFollowed(String act_no) {
        isFollowed = false;
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        mem_ac = loginState.getString("userAc", "noLogIn");

        retrieveIsFollow = (CommonTask) new CommonTask().execute(Common.ACT_URL, "isFollowed",
                "mem_ac", mem_ac, "act_no", act_no);
        String isFollowedString = "";
        try {
            isFollowedString = retrieveIsFollow.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (!isFollowedString.equals("null")) {
            isFollowed = true;
        }

        return isFollowed;
    }

    private boolean isLogIn() {
        boolean isLogIn;
        SharedPreferences loginState = getActivity().getSharedPreferences(Common.LOGIN_STATE, MODE_PRIVATE);
        mem_ac = loginState.getString("userAc", "noLogIn");
        if (!mem_ac.equals("noLogIn")) {
            isLogIn = true;
        } else {
            isLogIn = false;
        }
        return isLogIn;
    }

}

