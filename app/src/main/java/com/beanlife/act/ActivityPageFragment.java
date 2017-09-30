package com.beanlife.act;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.Common;
import com.beanlife.GetImageByPkTask;
import com.beanlife.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Java on 2017/9/5.
 */

public class ActivityPageFragment extends Fragment{
    private View view;
    private TextView actNameTv, actFollowNumTv, memAcTv, actOpDateTv, actMemCountTv, actAddTv, actContTv;
    private ImageView activityIv;
    private ActVO actVO;
    private GoogleMap googleMap;
    private LatLng actLocation;
    private MapView actMv;

//    public void getActVO(ActVO actVO){
//        this.actVO = actVO;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        actVO = (ActVO) getArguments().getSerializable("act");

        view = inflater.inflate(R.layout.activity_page_fragment, container, false);
        findView();

        setUpMap(savedInstanceState);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){

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

        actNameTv.setText(actVO.getAct_name());
        actFollowNumTv.setText("20");
        memAcTv.setText(actVO.getMem_ac());
        actOpDateTv.setText(actVO.getAct_op_date().toString());
        actMemCountTv.setText(actVO.getMem_count().toString());
        actAddTv.setText(actVO.getAct_add());
        actContTv.setText(actVO.getAct_cont());

    }

    private void setUpMap(Bundle savedInstanceState){
        actMv = (MapView) view.findViewById(R.id.act_mapView);
        actMv.onCreate(savedInstanceState);
        actMv.onResume();
        MapsInitializer.initialize(getActivity());
        googleMap = actMv.getMap();
        actLocation = new LatLng(Double.parseDouble(actVO.getAct_add_lat()),Double.parseDouble(actVO.getAct_add_lon()));

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(actLocation).zoom(16).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.addMarker(new MarkerOptions().position(actLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_marker)));
        googleMap.moveCamera(cameraUpdate);
    }
}
