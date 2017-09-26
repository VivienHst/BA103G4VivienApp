package com.beanlife;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by Java on 2017/9/10.
 */

public class StorePageFragment extends Fragment{
    private static final String TAG = "Store fragment";
    private View view;
    private StoreVO storeVO;
    private ImageView storeImg;
    private TextView storeNameTv, storePhoneTv, storeAddTv, storeDescConTv;
    MapView storeMv;
    private GoogleMap googleMap;
    private LatLng myLocation;
    private Marker marker_myLocation;
//    StoreVO storeVO;

    public void getStoreVO(StoreVO storeVO){
        this.storeVO = storeVO;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.store_fragment, container, false);
        Log.d(TAG, "storeVO.getStore_no() : "+ storeVO.getStore_no());
        findView(storeVO);
        storeMv = (MapView) view.findViewById(R.id.store_mapView);
        storeMv.onCreate(savedInstanceState);
        storeMv.onResume();
        MapsInitializer.initialize(getActivity());
        googleMap = storeMv.getMap();
        setUpMap();
        //android:id="@+id/store_mapView"
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(StoreVO storeVO){

        storeNameTv = (TextView) view.findViewById(R.id.store_name);
        storePhoneTv = (TextView) view.findViewById(R.id.store_phone);
        storeAddTv = (TextView) view.findViewById(R.id.store_add);
        storeDescConTv = (TextView) view.findViewById(R.id.store_dsc_con);

        storeImg = (ImageView) view.findViewById(R.id.store_img);
        new GetImageByPkTask(Common.STORE_URL, "store_no", storeVO.getStore_no(), 500, storeImg).execute();

        storeNameTv.setText(storeVO.getStore_name());
        storePhoneTv.setText("電話：" + storeVO.getStore_phone());
        storeAddTv.setText("地址：" + storeVO.getStore_add());
        storeDescConTv.setText(storeVO.getStore_cont());

    }

    private void setUpMap(){
        myLocation = new LatLng(Double.parseDouble(storeVO.getStore_add_lat()),Double.parseDouble(storeVO.getStore_add_lon()));


        if(ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//        googleMap.animateCamera(cameraUpdate);

        googleMap.addMarker(new MarkerOptions()
                        .position(myLocation )
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_marker)));
        googleMap.moveCamera(cameraUpdate);
    }

}
