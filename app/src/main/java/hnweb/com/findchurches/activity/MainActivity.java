package hnweb.com.findchurches.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.adaptor.ChurchListAdaptor;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.fragment.AllFavoriteListFragment;
import hnweb.com.findchurches.fragment.ChartiesFragment;
import hnweb.com.findchurches.fragment.LocationFragment;
import hnweb.com.findchurches.fragment.MeFragment;
import hnweb.com.findchurches.fragment.WokShipFragment;
import hnweb.com.findchurches.model.ChurchModelClass;
import hnweb.com.findchurches.utility.AlertUtility;
import hnweb.com.findchurches.utility.AppUtils;
import hnweb.com.findchurches.utility.LoadingDialog;
import hnweb.com.findchurches.utility.SharedPreference;

/**
 */

public class MainActivity extends AppCompatActivity {
    ImageView iv_logout;
    Toolbar toolbar;
    ImageView iv_search, iv_favorite, iv_location;
    RelativeLayout rl_headr, rl_search;
    EditText et_search;
    RecyclerView recycler_view;
    LoadingDialog loadingDialog;
    ArrayList<ChurchModelClass> churchModelClasses = new ArrayList<ChurchModelClass>();
    ChurchListAdaptor adapter;
    String user_Id;
    String isClickfav = "1", isClickLocation = "1";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_logout = (ImageView) toolbar.findViewById(R.id.iv_logout);
        rl_headr = (RelativeLayout) toolbar.findViewById(R.id.rl_headr);
        rl_search = (RelativeLayout) toolbar.findViewById(R.id.rl_search);
        iv_favorite = (ImageView) toolbar.findViewById(R.id.iv_favorite);
        iv_location = (ImageView) toolbar.findViewById(R.id.iv_location);
        et_search = (EditText) toolbar.findViewById(R.id.et_search);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);

        SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);

        user_Id = settings.getString("user_id", null);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        recycler_view.setLayoutManager(mLayoutManager);
        loadingDialog = new LoadingDialog(MainActivity.this);

        setupNavigationView();


        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.logout(MainActivity.this);
            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_headr.setVisibility(View.GONE);
                rl_search.setVisibility(View.VISIBLE);

                isClickfav = "1";
                iv_favorite.setImageResource(R.drawable.header_heart_gray);

            }
        });
        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClickLocation.equals("1")) {
                    pushFragment(new LocationFragment());
                    isClickLocation = "0";
                    iv_location.setImageResource(R.drawable.header_nearme_blue1);
                    iv_favorite.setImageResource(R.drawable.header_heart_gray);
                    isClickfav = "1";
                    }
            }
        });
        iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClickfav.equals("1")) {
                    pushFragment(new AllFavoriteListFragment());
                    isClickfav = "0";
                    iv_favorite.setImageResource(R.drawable.header_heart_blue);
                    iv_location.setImageResource(R.drawable.header_nearme_gray);
                    isClickLocation = "1";

                } /*else {
                    isClickfav = "1";
                    iv_favorite.setImageResource(R.drawable.header_heart_gray);
                    pushFragment(new WokShipFragment());


                }*/
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(et_search.getText().toString());
                recycler_view.setVisibility(View.VISIBLE);


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        adapter = new ChurchListAdaptor(churchModelClasses, MainActivity.this);
        getList();

    }

    private void setupNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        if (bottomNavigationView != null) {

            // Select first menu item by default and show Fragment accordingly.
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(2));

            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    /**
     * Perform action when any item is selected.
     *
     * @param item Item that is selected.
     */
    protected void selectFragment(MenuItem item) {

        isClickLocation = "1";
        isClickfav = "1";
        iv_location.setImageResource(R.drawable.header_nearme_gray);
        iv_favorite.setImageResource(R.drawable.header_heart_gray);

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.action_account:
                isClickfav = "1";
                iv_favorite.setImageResource(R.drawable.header_heart_gray);
                // Action to perform when Account Menu item is selected.
                pushFragment(new WokShipFragment());
                break;
            case R.id.action_home:
                isClickfav = "1";
                iv_favorite.setImageResource(R.drawable.header_heart_gray);
                // Action to perform when Home Menu item is selected.
                pushFragment(new MeFragment());
                break;
            case R.id.action_bag:
                isClickfav = "1";
                iv_favorite.setImageResource(R.drawable.header_heart_gray);
                // Action to perform when Bag Menu item is selected.
                pushFragment(new ChartiesFragment());
                break;

        }
    }

    /**
     * Method to push any fragment into given id.
     *
     * @param fragment An instance of Fragment to show into the given id.
     */
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.commit();
            }
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
                                        recycler_view.setAdapter(adapter);

                                    }
                                    System.out.println("jsonobk" + jsonArrayRow);
                                    System.out.println("churchModelClasses size." + churchModelClasses.size());

                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                        String reason = AppUtils.getVolleyError(MainActivity.this, error);
                        AlertUtility.showAlert(MainActivity.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<ChurchModelClass> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ChurchModelClass s : churchModelClasses) {
            //if the existing elements contains the search input
            if (s.getCname().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        recycler_view.setVisibility(View.GONE);
    }
}
