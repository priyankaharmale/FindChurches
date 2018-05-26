package hnweb.com.findchurches.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.activity.MainActivity;
import hnweb.com.findchurches.adaptor.CharityListAdaptor;
import hnweb.com.findchurches.adaptor.FavoriteListAdaptor;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.model.ChurchModelClass;
import hnweb.com.findchurches.utility.AlertUtility;
import hnweb.com.findchurches.utility.AppUtils;
import hnweb.com.findchurches.utility.LoadingDialog;

/**
 * Created by PC-21 on 07-May-18.
 */

public class ChartiesFragment extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    LoadingDialog loadingDialog;
    String user_Id;
    ArrayList<ChurchModelClass> churchModelClasses = new ArrayList<ChurchModelClass>();
    TextView tv_nodata;
    EditText et_searchs;
    CharityListAdaptor adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_charties, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        user_Id = settings.getString("user_id", null);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        et_searchs = (EditText) rootView.findViewById(R.id.et_searchs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        tv_nodata = (TextView) rootView.findViewById(R.id.tv_nodata);
        loadingDialog = new LoadingDialog(getActivity());

        et_searchs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(et_searchs.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getCharityList();


        return rootView;
    }

    private void getCharityList() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_CHARITY_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_fav" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("details");
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        final ChurchModelClass churchModelClass = new ChurchModelClass();
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
                                        churchModelClasses.add(churchModelClass);
                                    }

                                    System.out.println("churchModelClasses size." + churchModelClasses.size());

                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }
                                /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                loadingDialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();*/
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
                            if (churchModelClasses.size() == 0) {
                                recyclerView.setVisibility(View.GONE);
                                tv_nodata.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                tv_nodata.setVisibility(View.GONE);
                                adapter = new CharityListAdaptor(churchModelClasses, getContext());
                                recyclerView.setAdapter(adapter);
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

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<ChurchModelClass> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ChurchModelClass s : churchModelClasses) {
            //if the existing elements contains the search input
            if (s.getCname().toLowerCase().contains(text.toLowerCase()) || s.getCountry().toLowerCase().contains(text.toLowerCase()) ||
                    s.getCity().toLowerCase().contains(text.toLowerCase()) || s.getState().toLowerCase().contains(text.toLowerCase()) ||
                    s.getZipcode().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }



}

