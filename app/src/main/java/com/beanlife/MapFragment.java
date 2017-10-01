package com.beanlife;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanlife.store.StoreVO;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.beanlife.R.id.mapView;
/**
 * Created by Java on 2017/8/27.
 */

public class MapFragment extends Fragment {
    MapView mapview;
    private GoogleMap googleMap;
    private LatLng myLocation;
    private Marker marker_myLocation;
    private List<StoreVO> storeList;
    ImageView storeImgView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_page, container, false);
        mapview = (MapView) view.findViewById(mapView);
        mapview.onCreate(savedInstanceState);
        mapview.onResume();

        MapsInitializer.initialize(getActivity());

        googleMap = mapview.getMap();
//        initPoints();
        SupportMapFragment myLocatFragment =
                (SupportMapFragment)getFragmentManager().findFragmentById(mapView);


//        myLocatFragment.getMapAsync(this);
        setUpMap();
        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initPoints() {
        myLocation = new LatLng(24.9676446,121.1910268);
    }

    private void setUpMap(){
        initPoints();

        if(ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(18).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//        googleMap.animateCamera(cameraUpdate);
        //移動到點，沒有動畫
        googleMap.moveCamera(cameraUpdate);

        storeList = new ArrayList<StoreVO>();
        storeList = getAllStore();
        for (StoreVO storeVO : storeList){
            addMakersToMap(storeVO);
            googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(storeVO));

        }
        //addUserToMap();
        //googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

        MyMarkerListener myMakerListener = new MyMarkerListener();

    }

    private class MyMarkerListener implements GoogleMap.OnMarkerClickListener,
            GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {
        @Override
        public void onInfoWindowClick(Marker marker) {

        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }

        @Override
        public void onMarkerDragStart(Marker marker) {

        }

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {

        }
    }

    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setUpMap();
    }

//    private void addUserToMap() {
//        marker_myLocation = googleMap.addMarker(new MarkerOptions()
//                .position(myLocation )
//                .title("阿兜仔咖啡店")
//                .snippet("地址： 高雄市鳥松區本館路72巷11-1號")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_marker)));
//    }

    private void addMakersToMap(StoreVO storeVO) {
        LatLng storeLL = new LatLng(Double.parseDouble(storeVO.getStore_add_lat()),
                Double.parseDouble(storeVO.getStore_add_lon()));
        marker_myLocation = googleMap.addMarker(new MarkerOptions()
                .position(storeLL)
                .title(storeVO.getStore_name())
                .snippet(storeVO.getStore_add())
                //.imgno(storeVO.setStore_no())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_marker)));
    }

    private List<StoreVO> getAllStore(){
        String storeVOString = "";
        CommonTask retrieveStore = (CommonTask) new CommonTask().execute(Common.STORE_URL,"getAll");
        try {
            storeVOString = retrieveStore.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<StoreVO>>(){}.getType();
        return gson.fromJson(storeVOString, listType);
    }

    //設定bubble的內容
    private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View infoWindow;
        StoreVO storeVO;

        private MyInfoWindowAdapter(StoreVO storeVO) {
            infoWindow = View.inflate(getActivity(), R.layout.map_bubble, null);
            this.storeVO = storeVO;
        }

        @Override
        public View getInfoWindow(Marker marker) {

//            int logoId = R.drawable.store1;
//            if (marker.equals(marker_myLocation)) {
//                logoId = R.drawable.store1;}

            storeImgView = (ImageView) infoWindow
                    .findViewById(R.id.map_bubble_store_img_view);
            //storeImgView.setImageResource(logoId);
            new GetImageByPkTask(Common.STORE_URL, "store_no", storeVO.getStore_no(), 200, storeImgView).execute();

            String title = marker.getTitle();
            TextView storeTitle = ((TextView) infoWindow
                    .findViewById(R.id.store_name));
            storeTitle.setText(title);

            String snippet = marker.getSnippet();
            TextView tvSnippet = ((TextView) infoWindow
                    .findViewById(R.id.store_add));
            tvSnippet.setText(snippet);
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}