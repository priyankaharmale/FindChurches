package hnweb.com.findchurches.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import hnweb.com.findchurches.R;
import hnweb.com.findchurches.activity.ChurchDetailActivity;
import hnweb.com.findchurches.activity.EventDetailsActivity;
import hnweb.com.findchurches.activity.MainActivity;
import hnweb.com.findchurches.calender.CalendarListener;
import hnweb.com.findchurches.calender.CustomCalendarView;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.interfaces.BaseBackPressedListener;
import hnweb.com.findchurches.model.EventModel;
import hnweb.com.findchurches.utility.AlertUtility;
import hnweb.com.findchurches.utility.AppUtils;
import hnweb.com.findchurches.utility.Utils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventCalenderFragment extends Fragment {

    String TAG = EventCalenderFragment.class.getSimpleName();
    CustomCalendarView customCalendarView;
    AlertDialog b;
    String todayDate;
    ArrayList<EventModel> eventModels;
    ArrayList<String> arraylistDate;
    ProgressDialog myDialog;
    String church_id;
    ArrayList<EventModel> selected_Date_events;

    public EventCalenderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_calender, container, false);


        customCalendarView = (CustomCalendarView) view.findViewById(R.id.calendar_view);

        getsaveData();
        initView();
        return view;
    }

    private void initView() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");

        todayDate = mdformat.format(calendar.getTime());
        Log.d("strDate", "" + todayDate);

        fetchEvents(church_id, todayDate, todayDate);


        customCalendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {

                SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                String changed_date = mdformat.format(date.getTime());
                Log.d("changed date ", "" + changed_date);
                // Toast.makeText(getActivity(), "" + changed_date, Toast.LENGTH_SHORT).show();
                getEventsDetails(eventModels, changed_date);

             //   customCalendarView.setBackgroundColorOfRedOrGreenNew(arraylistDate, todayDate, "UnAvailable");
            }

            @Override
            public void onMonthChanged(Date time) {
                String month = String.valueOf(time.getMonth());

                SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                String changed_date = mdformat.format(time.getTime());
                Log.d("changed_date", "" + changed_date);
                arraylistDate.clear();
                fetchEvents(church_id, changed_date, changed_date);
                //    customCalendarView.setBackgroundColorOfRedOrGreenNew(arraylistDate, todayDate, "UnAvailable");

                //Toast.makeText(getActivity(), "" + changed_date, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void fetchEvents(final String user_id, final String strDate, final String date) {


        myDialog = Utils.DialogsUtils.showProgressDialog(getActivity(), getString(R.string.processing));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_EVENT,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        if (myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        Log.d("res_events", response);

                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            eventModels = new ArrayList<>();
                            arraylistDate = new ArrayList<>();

                            if (message_code == 1) {
                                JSONArray jsonArray = j.getJSONArray("event_dates");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    EventModel model = new EventModel();
                                    model.setEid(jsonObject.getString("eid"));
                                    model.setEvent_dt(jsonObject.getString("event_dt"));

                                    eventModels.add(model);

                                    arraylistDate.add(jsonObject.getString("event_dt"));
                                    System.out.println("Datebdfdsf" + jsonObject.getString("event_dt"));

                                }

                                System.out.println("ModelSize" + eventModels.size());
                                System.out.println("ArraySize" + arraylistDate.size());
                                customCalendarView.setBackgroundColorOfRedOrGreenNew(arraylistDate, date, "UnAvailable");


                                //  getSelectedDateEvent(date);

                                // Toast.makeText(getActivity(), "" + eventModels.size(), Toast.LENGTH_SHORT).show();
                            } else {
                                message = j.getString("message");

                                Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (myDialog.isShowing()) {
                            myDialog.dismiss();
                        }

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


    private void getsaveData() {
        SharedPreferences settings = getActivity().getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);

        church_id = settings.getString("church_id", null);
    }


    public void getEventsDetails(ArrayList<EventModel> arraylistDate, String changed_date) {

        String eventId = null;
        for (EventModel eventModel : arraylistDate) {
            if (eventModel.getEvent_dt().equals(changed_date)) {
                eventId = eventModel.getEid();
                Intent intent2 = new Intent(getContext(), EventDetailsActivity.class);
                Bundle bundleObject = new Bundle();
                bundleObject.putString("eventId", eventId);
                intent2.putExtras(bundleObject);
                startActivity(intent2);
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0) { // Activity.RESULT_OK
            Toast.makeText(getActivity(), "Mail Send", Toast.LENGTH_LONG).show();
            b.dismiss();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }


}
