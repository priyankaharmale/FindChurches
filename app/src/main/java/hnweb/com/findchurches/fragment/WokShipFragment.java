package hnweb.com.findchurches.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.activity.ChurchDetailActivity;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.model.ChurchModelClass;
import hnweb.com.findchurches.utility.AlertUtility;
import hnweb.com.findchurches.utility.AppUtils;
import hnweb.com.findchurches.utility.LoadingDialog;
import hnweb.com.findchurches.utility.LocationSet;
import hnweb.com.findchurches.utility.MyLocationListener;

import android.Manifest;
import android.content.pm.PackageManager;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static hnweb.com.findchurches.utility.SharedPreference.PREFS_NAME;


/**
 * Created by PC-21 on 07-May-18.
 */

public class WokShipFragment extends Fragment implements LocationListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    LocationSet locationSet;
    MapView mMapView;
    public static final int MIN_TIME_BW_UPDATES = 10; // 10 meters;
    public static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000 * 60 * 1; // 1 minute;
    private static final String TAG = "MapsActivity";
    Boolean flag = false;
    List<Marker> markers;
    int fromIndex = 0;
    int toIndex = 200;
    android.location.Location location;
    boolean isNetworkEnabled = false;
    boolean isGPSEnabled = false;
    Address returnedAddress;
    Double latitude;
    Double longitude;
    String heading;
    String distance1;
    double distnace;
    SharedPreferences sharedPreferences;
    StringBuilder strReturnedAddress;
    String category, city;
    ProgressDialog progress;
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


        rootView = inflater.inflate(R.layout.fragment_woship, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        myLocationListener = new MyLocationListener(getActivity());
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

            //   SupportMapFragment fm = (SupportMapFragment) ((MainActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);

            // SupportMapFragment mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();


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
                                //googleMap.clear();
                                //onLocationChanged(location);
                                getList();
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
                                    //googleMap.clear();
                                    //onLocationChanged(location);
                                    getList();
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
        // check if myLocationListener enabled
        if (myLocationListener.canGetLocation()) {
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();

            //  Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            myLocationListener.showSettingsAlert();
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();

            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        }

    }

    private void getList() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETLIST_CHURCH_NEARBY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("details");

                                loadingDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("NearBy Churches Loaded on Map..")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                try {
                                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                                         ChurchModelClass churchModelClass = new ChurchModelClass();

                                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                                        churchModelClass.setCid(jsonObjectpostion.getString("cid"));
                                                        churchModelClass.setAddress(jsonObjectpostion.getString("address"));
                                                        churchModelClass.setCname(jsonObjectpostion.getString("cname"));
                                                        churchModelClass.setCountry(jsonObjectpostion.getString("country"));
                                                        churchModelClass.setState(jsonObjectpostion.getString("state"));
                                                        churchModelClass.setCity(jsonObjectpostion.getString("city"));
                                                        churchModelClass.setZipcode(jsonObjectpostion.getString("zipcode"));
                                                        churchModelClass.setCimg(jsonObjectpostion.getString("cimg"));
                                                        churchModelClass.setLat_long(jsonObjectpostion.getString("lat_long"));
                                                        churchModelClass.setLatitude(jsonObjectpostion.getString("latitude"));
                                                        churchModelClass.setLongitude(jsonObjectpostion.getString("longitude"));
                                                        churchModelClass.setDistance(jsonObjectpostion.getString("distance"));
                                                        churchModelClasses.add(churchModelClass);

                                                    }
                                                    System.out.println("jsonobk" + jsonArrayRow);
                                                    System.out.println("churchModelClasses size." + churchModelClasses.size());
                                                    googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                                        @Override
                                                        public void onMapLoaded() {
                                                            markers = new ArrayList<Marker>();
                                                            LatLng latlngDestination = null;
                                                            for (ChurchModelClass contracotrModelClass : churchModelClasses) {
                                                                if (contracotrModelClass.getLatitude() != null || contracotrModelClass.getLongitude() != null) {
                                                                    latlngDestination = new LatLng(Double.valueOf(contracotrModelClass.getLatitude()), Double.valueOf(contracotrModelClass.getLongitude()));
                                                                    Marker marker = googleMap.addMarker(new MarkerOptions().title(contracotrModelClass.getCname())
                                                                            .position(latlngDestination)
                                                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.worship_icon_gray1))
                                                                    );
                                                                    System.out.println("latitude" + contracotrModelClass.getLatitude());
                                                                    System.out.println("longitude " + contracotrModelClass.getLongitude());
                                                                    markers.add(marker);

                                                                }
                                                            }
                                                            try {
                                                                latLng[0] = new LatLng(25.761681, -80.191788);

                                                            } catch (ArrayIndexOutOfBoundsException e) {
                                                                e.printStackTrace();
                                                            }
                                                            try {
                                                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                                        .target(latLng[0])//zoom on current location
                                                                        .zoom(16)
                                                                        .build();
                                                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
                                                                    @Override
                                                                    public void onFinish() {
                                                                        CameraUpdate cu_scroll = CameraUpdateFactory.scrollBy(-300, 70);
                                                                        googleMap.animateCamera(cu_scroll);
                                                                    }

                                                                    @Override
                                                                    public void onCancel() {
                                                                    }
                                                                });
                                                            } catch (ArrayIndexOutOfBoundsException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } catch (JSONException e) {
                                                    System.out.println("jsonexeption" + e.toString());
                                                }
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }


                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.LATITUDE, "25.761681");
                    params.put(AppConstant.LONGITUDE, "-80.191788");
                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

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
            latLng = new LatLng(25.761681, -80.191788);
            MarkerOptions markerOptions = new MarkerOptions();
            googleMap.addMarker(markerOptions.title("Current Location")
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );
        /*   googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(getApplicationContext(),"" +strReturnedAddress.toString(),Toast.LENGTH_SHORT).show();
                    return false;
                }
            });*/


           /* googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(Marker arg0) {
                    if(arg0.getTitle().equals("Current Location")) // if marker source is clicked
                        Toast.makeText(MapsActivity.this,"" +strReturnedAddress.toString(), Toast.LENGTH_SHORT).show();// display toast
                    return false;
                }

            });
*/
        }

        Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(25.761681, -80.191788, 1);
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
        // latLng1 = marker.getPosition();
        //  googleMap.getUiSettings().setMapToolbarEnabled(false);
        //  googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        //    googleMap.getUiSettings().setCompassEnabled(true);
        // Toast.makeText(MapsActivity.this,"Hiii",Toast.LENGTH_SHORT).show();
        System.out.println("markers.size" +markers.size());

        if (flag == false) {


            if (marker.getTitle().equals("Current Location")) {
                Toast.makeText(getActivity(), "Current Location", Toast.LENGTH_SHORT).show();
            } else {

                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    // Use default InfoWindow frame
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    // Defines the contents of the InfoWindow
                    @Override
                    public View getInfoContents(Marker marker) {

                        // Getting view from the layout file info_window_layout
                        View view = getLayoutInflater().inflate(R.layout.church_info, null);
                        final ChurchModelClass infoWindowData = churchModelClasses.get(markers.indexOf(marker));
                        TextView tv_churchname = view.findViewById(R.id.tv_churchname);
                        TextView tv_address = view.findViewById(R.id.tv_address);
                        ImageView iv_church = view.findViewById(R.id.iv_church);
                        Button btn_donateNow = view.findViewById(R.id.btn_donateNow);

                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            public void onInfoWindowClick(Marker marker) {
                                Intent intent = new Intent(getActivity(), ChurchDetailActivity.class);

                                Bundle bundleObject = new Bundle();
                                bundleObject.putString("ChurchImage", infoWindowData.getCimg());
                                intent.putExtras(bundleObject);
                                // intent2.putExtra("EventList", selected_Date_events);
                                startActivity(intent);

                                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("church_id", infoWindowData.getCid());
                                editor.putString(AppConstant.LATITUDE, infoWindowData.getLatitude());
                                editor.putString(AppConstant.LONGITUDE, infoWindowData.getLongitude());
                                editor.putString(AppConstant.KEY_NAME, infoWindowData.getCname());
                                editor.putString("ChurchImage", infoWindowData.getCimg());

                                Gson gson = new Gson();
                                String json = gson.toJson(infoWindowData);
                                editor.putString("MyObject", json);


                                editor.commit();
                            }
                        });

                       /* int imageId = getResources().getIdentifier(infoWindowData.getCimg().toLowerCase(),
                                "drawable", getActivity().getPackageName());
                        iv_church.setImageResource(imageId);*/
                        try {
                            Glide.with(getActivity())
                                    .load(infoWindowData.getCimg())
                                    .centerCrop()
                                    .crossFade()
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            return false;
                                        }
                                    })
                                    .into(iv_church);
                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage());
                        }
                        tv_churchname.setText(infoWindowData.getCname());
                        tv_address.setText(infoWindowData.getAddress() + " " + infoWindowData.getCity() + " " + infoWindowData.getState() + " " + infoWindowData.getCountry() + " " + infoWindowData.getZipcode());


                        return view;
                    }

                });

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

          googleMap.getUiSettings().setMapToolbarEnabled(false);
         googleMap.getUiSettings().setMyLocationButtonEnabled(true);
           googleMap.getUiSettings().setCompassEnabled(true);


    }
}
