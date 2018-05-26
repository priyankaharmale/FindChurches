package hnweb.com.findchurches.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import hnweb.com.findchurches.adaptor.FavoriteListAdaptor;
import hnweb.com.findchurches.adaptor.HistoryListAdaptor;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.model.ChurchModelClass;
import hnweb.com.findchurches.model.PaymentModelClass;
import hnweb.com.findchurches.utility.AlertUtility;
import hnweb.com.findchurches.utility.AppUtils;
import hnweb.com.findchurches.utility.LoadingDialog;

public class HistoryActivity extends AppCompatActivity {

    ImageView iv_logout;
    Toolbar toolbar;
    ImageView iv_search;
    RelativeLayout rl_headr, rl_search;
    EditText et_search;
    RecyclerView recycler_view;
    LoadingDialog loadingDialog;
    ChurchListAdaptor adapter;
    String user_Id;
    ArrayList<ChurchModelClass> paymentModelClasses = new ArrayList<>();
    TextView tv_nodata;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        user_Id = settings.getString("user_id", null);
        loadingDialog = new LoadingDialog(HistoryActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_logout = (ImageView) toolbar.findViewById(R.id.iv_logout);
        rl_headr = (RelativeLayout) toolbar.findViewById(R.id.rl_headr);
        rl_search = (RelativeLayout) toolbar.findViewById(R.id.rl_search);
        et_search = (EditText) toolbar.findViewById(R.id.et_search);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        tv_nodata = (TextView) findViewById(R.id.tv_nodata);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        recycler_view.setLayoutManager(mLayoutManager);
        getHistoryList();
    }

    private void getHistoryList() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_PAYMENT_HOSTORY,
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
                                        churchModelClass.setId(jsonObjectpostion.getString("id"));
                                        churchModelClass.setReg_id(jsonObjectpostion.getString("reg_id"));
                                        churchModelClass.setDonation_amount(jsonObjectpostion.getString("donation_amount"));
                                        churchModelClass.setStripeToken(jsonObjectpostion.getString("stripeToken"));
                                        churchModelClass.setStripChargeID(jsonObjectpostion.getString("stripChargeID"));
                                        churchModelClass.setStripTractionID(jsonObjectpostion.getString("stripTractionID"));
                                        churchModelClass.setStatus(jsonObjectpostion.getString("status"));
                                        churchModelClass.setCreated_on(jsonObjectpostion.getString("created_on"));
                                        churchModelClass.setCname(jsonObjectpostion.getString("cname"));
                                        churchModelClass.setCimg(jsonObjectpostion.getString("cimg"));
                                        churchModelClass.setAddress(jsonObjectpostion.getString("address"));
                                        churchModelClass.setCountry(jsonObjectpostion.getString("country"));
                                        churchModelClass.setState(jsonObjectpostion.getString("state"));
                                        churchModelClass.setCity(jsonObjectpostion.getString("city"));
                                        churchModelClass.setZipcode(jsonObjectpostion.getString("zipcode"));

                                        paymentModelClasses.add(churchModelClass);
                                    }

                                    System.out.println("churchModelClasses size." + paymentModelClasses.size());

                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                loadingDialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            if (paymentModelClasses.size() == 0) {
                                recycler_view.setVisibility(View.GONE);
                                tv_nodata.setVisibility(View.VISIBLE);
                            } else {
                                recycler_view.setVisibility(View.VISIBLE);
                                tv_nodata.setVisibility(View.GONE);
                                HistoryListAdaptor adapter = new HistoryListAdaptor(paymentModelClasses, HistoryActivity.this);
                                recycler_view.setAdapter(adapter);
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
                        String reason = AppUtils.getVolleyError(HistoryActivity.this, error);
                        AlertUtility.showAlert(HistoryActivity.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(HistoryActivity.this);
        requestQueue.add(stringRequest);

    }

}
