package hnweb.com.findchurches.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.interfaces.OnBackPressedListener;
import hnweb.com.findchurches.model.EventModel;
import hnweb.com.findchurches.utility.AlertUtility;
import hnweb.com.findchurches.utility.AppUtils;
import hnweb.com.findchurches.utility.LoadingDialog;

/**
 * Created by PC-21 on 10-May-18.
 */

public class EventDetailsActivity extends AppCompatActivity {
    String eventId;
    LoadingDialog loadingDialog;
    ImageView iv_church;
    TextView tv_eventname,tv_eventdesc,tv_eventdate,tv_churchname;
    String str_eventName, str_eventDesc,str_eventDate,str_Image,str_churchName;
    protected OnBackPressedListener onBackPressedListener;
    ProgressBar  progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_eventdetails);

        iv_church=(ImageView) findViewById(R.id.iv_church);
        tv_eventname=(TextView) findViewById(R.id.tv_eventname);
        tv_eventdesc=(TextView) findViewById(R.id.tv_eventdesc);
        tv_eventdate=(TextView) findViewById(R.id.tv_eventdate);
        tv_churchname=(TextView) findViewById(R.id.tv_churchname);

        progress = (ProgressBar) findViewById(R.id.progress);

        loadingDialog = new LoadingDialog(this);

        if (getIntent().hasExtra("eventId")) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                eventId = bundle.getString("eventId");

            } else {
                Log.e("null", "null");
            }
        }
        getEventDetails();
    }

    private void getEventDetails() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_EVENTDETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final JSONObject j;
                        System.out.println("res_register" + response);
                        try {
                            j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            final EventModel eventModel = new EventModel();
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                try {
                                                    JSONObject jsonObjectpostion = j.getJSONObject("details");
                                                    str_churchName=jsonObjectpostion.getString("cname");
                                                    str_eventName=jsonObjectpostion.getString("event_name");
                                                    str_eventDesc=jsonObjectpostion.getString("event_desc");
                                                    str_eventDate=jsonObjectpostion.getString("event_dt");
                                                    str_Image=jsonObjectpostion.getString("event_img");

                                                    tv_churchname.setText(str_churchName);
                                                    tv_eventname.setText(str_eventName);
                                                    tv_eventdesc.setText(str_eventDesc);
                                                    tv_eventdate.setText(str_eventDate);


                                                    try {
                                                        Glide.with(EventDetailsActivity.this)
                                                                .load(str_Image)
                                                                .centerCrop()
                                                                .crossFade()
                                                                .listener(new RequestListener<String, GlideDrawable>() {
                                                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                                        progress.setVisibility(View.GONE);
                                                                        return false;
                                                                    }

                                                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                                        progress.setVisibility(View.GONE);
                                                                        return false;
                                                                    }
                                                                })
                                                                .into(iv_church);
                                                    } catch (Exception e) {
                                                        Log.e("Exception", e.getMessage());
                                                    }

                                                } catch (JSONException e) {
                                                    System.out.println("jsonexeption" + e.toString());
                                                }
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
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
                        String reason = AppUtils.getVolleyError(EventDetailsActivity.this, error);
                        AlertUtility.showAlert(EventDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.EVENT_ID, eventId);
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
        RequestQueue requestQueue = Volley.newRequestQueue(EventDetailsActivity.this);
        requestQueue.add(stringRequest);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(EventDetailsActivity.this, ChurchDetailActivity.class);
        Bundle bundleObject = new Bundle();
        bundleObject.putString("eventBack", "back");
        intent.putExtras(bundleObject);
        startActivity(intent);
    }
}
