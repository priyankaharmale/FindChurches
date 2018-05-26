package hnweb.com.findchurches.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.activity.MainActivity;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.model.ChurchModelClass;
import hnweb.com.findchurches.utility.AlertUtility;
import hnweb.com.findchurches.utility.AppUtils;
import hnweb.com.findchurches.utility.LoadingDialog;

/**
 * Created by PC-21 on 07-May-18.
 */

public class ProfileFragment extends Fragment {
    String user_Id, church_id;
    View rootView;
    LoadingDialog loadingDialog;
    ArrayList<ChurchModelClass> churchModelClasses = new ArrayList<ChurchModelClass>();
    ImageView iv_fav;
    TextView tv_churchname, tv_place, tv_address, tv_phone;
    String isClickFav = "1";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);

        user_Id = settings.getString("user_id", null);
        church_id = settings.getString("church_id", null);

        loadingDialog = new LoadingDialog(getActivity());

        tv_churchname = (TextView) rootView.findViewById(R.id.tv_churchname);
        tv_place = (TextView) rootView.findViewById(R.id.tv_place);
        tv_address = (TextView) rootView.findViewById(R.id.tv_address);
        tv_phone = (TextView) rootView.findViewById(R.id.tv_phone);
        iv_fav = (ImageView) rootView.findViewById(R.id.iv_fav);
        getProfileData();

        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickFav.equals("1")) {
                    iv_fav.setImageResource(R.drawable.heart_red);
                    isClickFav = "0";
                    addToFav("Y");
                } else {
                    iv_fav.setImageResource(R.drawable.header_heart_blue);
                    isClickFav = "1";
                    addToFav("N");
                }
            }
        });
        return rootView;

    }

    private void getProfileData() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_CHURCH_DETAILS,
                new Response.Listener<String>() {
            @Override
                    public void onResponse(String response) {
                        final JSONObject j;
                        System.out.println("res_register" + response);
                        try {
                            j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            final ChurchModelClass churchModelClass = new ChurchModelClass();
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                try {
                                    JSONObject jsonObjectpostion = j.getJSONObject("details");
                                    try {
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
                                        churchModelClass.setContact_no(jsonObjectpostion.getString("contact_no"));
                                        churchModelClass.setFav_status(jsonObjectpostion.getString("fav_status"));
                                        String str_favStatus = jsonObjectpostion.getString("fav_status");
                                        if (str_favStatus.equals("Y")) {
                                            iv_fav.setImageResource(R.drawable.heart_red);
                                            isClickFav = "0";
                                            } else {
                                            iv_fav.setImageResource(R.drawable.header_heart_blue);
                                            isClickFav = "1";
                                        }
                                        tv_churchname.setText(churchModelClass.getCname());
                                        tv_address.setText(churchModelClass.getAddress() + " " + churchModelClass.getCity() + " " + churchModelClass.getState() + " " + churchModelClass.getCountry() + " " + churchModelClass.getZipcode());
                                        tv_phone.setText(churchModelClass.getContact_no());
                                    } catch (JSONException e) {
                                        System.out.println("jsonexeption" + e.toString());
                                    }
                                    } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

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
                    params.put(AppConstant.CHURCH_ID, church_id);
                    params.put(AppConstant.USER_ID, user_Id);
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

    private void addToFav(final String str_favstatus) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_ADD_FAV,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        final JSONObject j;
                        System.out.println("res_register" + response);
                        try {
                            j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            final ChurchModelClass churchModelClass = new ChurchModelClass();
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
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
                    params.put(AppConstant.CHURCH_ID, church_id);
                    params.put(AppConstant.USER_ID, user_Id);
                    params.put(AppConstant.FAV_STATUS, str_favstatus);
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
    public void onDetach() {
        super.onDetach();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}

