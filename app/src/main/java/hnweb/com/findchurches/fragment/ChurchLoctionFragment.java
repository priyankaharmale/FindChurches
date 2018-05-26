package hnweb.com.findchurches.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.activity.MainActivity;
import hnweb.com.findchurches.model.ChurchModelClass;
import hnweb.com.findchurches.utility.LoadingDialog;
import hnweb.com.findchurches.utility.LocationSet;
import hnweb.com.findchurches.utility.MyLocationListener;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import hnweb.com.findchurches.contants.AppConstant;

import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by PC-21 on 09-May-18.
 */

public class ChurchLoctionFragment extends Fragment implements LocationListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    LocationSet locationSet;
    MapView mMapView;
    public static final int MIN_TIME_BW_UPDATES = 10; // 10 meters;
    public static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000 * 60 * 1; // 1 minute;
    private static final String TAG = "MapsActivity";
    Boolean flag = false;
    List<Marker> markers;
    android.location.Location location;
    boolean isNetworkEnabled = false;
    boolean isGPSEnabled = false;
    Address returnedAddress;
    Double latitude;
    Double longitude;
    String str_lat, str_long, str_churchName;
    SharedPreferences sharedPreferences;
    StringBuilder strReturnedAddress;
    String category, city;
    ProgressDialog progress;
    ChurchModelClass churchModelClass;
    MyLocationListener myLocationListener;
    private GoogleMap googleMap;
    double CameraLat, CameraLong;
    LoadingDialog loadingDialog;
    ArrayList<ChurchModelClass> churchModelClasses = new ArrayList<ChurchModelClass>();
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    final LatLng[] latLng = {null};
    List<Address> addresses = null;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_churchmap, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        myLocationListener = new MyLocationListener(getActivity());

        SharedPreferences settings = getActivity().getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);

        str_lat = settings.getString(AppConstant.LATITUDE, null);
        str_long = settings.getString(AppConstant.LONGITUDE, null);
        str_churchName = settings.getString(AppConstant.KEY_NAME, null);

        Gson gson = new Gson();
        String json = settings.getString("MyObject", "");
        churchModelClass = gson.fromJson(json, ChurchModelClass.class);


        locationSet = new LocationSet();
        if (locationSet.checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION, getActivity(), getActivity())) {
            fetchLocationData();
        } else {
            locationSet.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getActivity(), getActivity());
        }

        MapsInitializer.initialize(getActivity());
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();
        } else {
            SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
            // googleMap = fm.getMap();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }


            try {

                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled


                } else {
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {

                            }
                        }
                    }
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {

                                }
                            }
                        }
                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rootView;


    }


    private void fetchLocationData() {
        if (myLocationListener.canGetLocation()) {
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();
        } else {
            myLocationListener.showSettingsAlert();
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fetchLocationData();

                } else {

                    Toast.makeText(getActivity(), "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onLocationChanged(android.location.Location location) {

        LatLng latLng = null;
        googleMap.clear();
        if (googleMap != null) {
            latLng = new LatLng(Double.valueOf(churchModelClass.getLatitude()), Double.valueOf(churchModelClass.getLongitude()));
            MarkerOptions markerOptions = new MarkerOptions();
            googleMap.addMarker(markerOptions.title(churchModelClass.getCname()).snippet(churchModelClass.getAddress())
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );
        }

        Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.valueOf(str_lat), Double.valueOf(str_long), 1);
            if (addresses != null) {
                returnedAddress = addresses.get(0);
                strReturnedAddress = new StringBuilder("Address:\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                // Toast.makeText(getApplicationContext(),"" +strReturnedAddress.toString(),Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "No Address returned!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        // Toast.makeText(MapsActivity.this, "  " + latitude + "  " + longitude, Toast.LENGTH_SHORT).show();

    }


    //*************************************** Marker Click Event Handling *********************************************************
    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();
        if (flag == false) {
            if (marker.getTitle().equals(str_churchName)) {
                // Toast.makeText(getActivity(), "Current Location", Toast.LENGTH_SHORT).show();
            } else {

            }
        } else {
            Log.i("Marker", "Hidden");

        }
        getActivity().findViewById(R.id.ic_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d" +
                                "&saddr=" + location.getLatitude() + " " + location.getLongitude() + "&daddr=" + marker.getPosition().latitude + " " + marker.getPosition().longitude + "&hl=zh&t=m&dirflg=d"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);


            }
        });
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap1) {
        googleMap = googleMap1;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
