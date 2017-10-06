package com.beanlife;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beanlife.store.StoreVO;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
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


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private ImageView storeImgView;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_page, container, false);
        googleApiClient = null;
        mapview = (MapView) view.findViewById(mapView);
        mapview.onCreate(savedInstanceState);
        mapview.onResume();

        MapsInitializer.initialize(getActivity());

        googleMap = mapview.getMap();
//        initPoints();

        SupportMapFragment myLocFragment =
                (SupportMapFragment)getFragmentManager().findFragmentById(mapView);
//        myLocatFragment.getMapAsync(this);
        setUpMap();
        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askPermissions();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //updateLastLocationInfo(location);
            lastLocation = location;
            LatLng myLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.animateCamera(cameraUpdate);
            //googleMap.moveCamera(cameraUpdate);
        }
    };

    private void initPoints() {
        GoogleApiClient.ConnectionCallbacks connectionCallbacks =
                new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED){
                            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                            LocationRequest locationRequest = LocationRequest.create()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(1000)
                                    .setSmallestDisplacement(1);
                            LocationServices.FusedLocationApi.requestLocationUpdates(
                                    googleApiClient, locationRequest, locationListener);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                };

        if (googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(getActivity()).
                    addApi(LocationServices.API)
                    .addConnectionCallbacks(connectionCallbacks)
                    .build();
        }
        googleApiClient.connect();

        if(lastLocation != null){
            myLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        } else {
            myLocation = new LatLng(24.9676446,121.1910268);
        }

        googleMap.setMyLocationEnabled(true);

    }

    private void setUpMap(){
        initPoints();

        if(ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//        googleMap.animateCamera(cameraUpdate);
        //移動到點，沒有動畫
        googleMap.moveCamera(cameraUpdate);

        storeList = new ArrayList<StoreVO>();
        storeList = getAllStore();

        googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(addMakersToMap(storeList)));
    }

//    private class MyMarkerListener implements GoogleMap.OnMarkerClickListener,
//            GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {
//        @Override
//        public void onInfoWindowClick(Marker marker) {
//        }
//
//        @Override
//        public boolean onMarkerClick(Marker marker) {
//            return false;
//        }
//
//        @Override
//        public void onMarkerDragStart(Marker marker) {
//
//        }
//
//        @Override
//        public void onMarkerDrag(Marker marker) {
//
//        }
//
//        @Override
//        public void onMarkerDragEnd(Marker marker) {
//
//        }
//    }

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

    private Map<Marker, StoreVO> addMakersToMap(List<StoreVO> storeList) {
        Map<Marker, StoreVO> makerMap = new HashMap<Marker, StoreVO>();

        for (StoreVO storeVO : storeList){
            LatLng storeLL = new LatLng(Double.parseDouble(storeVO.getStore_add_lat()),
                    Double.parseDouble(storeVO.getStore_add_lon()));
            marker_myLocation = googleMap.addMarker(new MarkerOptions()
                    .position(storeLL)
                    .title(storeVO.getStore_name())
                    .snippet(storeVO.getStore_add())
                    //.imgno(storeVO.setStore_no())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_marker)));
            makerMap.put(marker_myLocation, storeVO);
        }
        return makerMap;
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
        Map<Marker, StoreVO> makerMap;

        private MyInfoWindowAdapter(Map<Marker, StoreVO> makerMap) {
            infoWindow = View.inflate(getActivity(), R.layout.map_bubble, null);
            this.makerMap = makerMap;
        }

        @Override
        public View getInfoWindow(Marker marker) {

            storeImgView = (ImageView) infoWindow.findViewById(R.id.map_bubble_store_img_view);

            try {
                Bitmap bm = new GetImageByPkTask(Common.STORE_URL, "store_no",
                        makerMap.get(marker).getStore_no(), 200, null).execute().get();
                storeImgView.setImageBitmap(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    private static final int REQ_PERMISSIONS = 0;

    // New Permission see Appendix A
    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(),
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
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {

                    }
                }
                break;
        }
    }



}