package hnweb.com.findchurches.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.adaptor.MonthAdaptor;
import hnweb.com.findchurches.adaptor.YearAdaptor;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.interfaces.OnCallBack;
import hnweb.com.findchurches.utility.LoadingDialog;
import hnweb.com.findchurches.utility.SharedPreference;


public class PaymentTotalActivity extends AppCompatActivity implements OnCallBack {
    int count;
    //   ArrayList<String> strings;
    TextView tv_paymnettoatl;
    LinearLayout ll_addDonation;
    Button btn_payment;
    ImageView iv_cancle;
    String str_checksdonatioon, user_Id, church_id;
    String str_check="";
    LoadingDialog loadingDialog;
    Dialog dialog;
    EditText et_year, et_month;
    OnCallBack onCallBack;
    String item;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);
        tv_paymnettoatl = (TextView) findViewById(R.id.tv_paymnettoatl);
        ll_addDonation = (LinearLayout) findViewById(R.id.ll_addDonation);
        btn_payment = (Button) findViewById(R.id.btn_payment);
        iv_cancle = (ImageView) findViewById(R.id.iv_cancle);
        onCallBack = PaymentTotalActivity.this;

        final SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
        count = settings.getInt("count", 0);
        str_checksdonatioon = settings.getString("Key", null);
        church_id = settings.getString("church_id", null);
        loadingDialog = new LoadingDialog(PaymentTotalActivity.this);

        System.out.println("count" + count);


        user_Id = settings.getString("user_id", null);
        tv_paymnettoatl.setText("$" + String.valueOf(count));
        ll_addDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentTotalActivity.this, DonationCostListActvity.class);
                startActivity(intent);
                SharedPreferences settings;
                SharedPreferences.Editor editor;
                settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
                editor = settings.edit();
                editor.putInt("count", count);
               /* Gson gson = new Gson();
                String json = gson.toJson(strings);*/
                //    editor.putString("stringsSave", json);
                editor.commit();
                finish();

            }
        });
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0) {
                    Toast.makeText(PaymentTotalActivity.this, "Please Select the amount", Toast.LENGTH_SHORT).show();

                } else if (str_check.equals("") || str_check.equals("null") || str_check==null ) {
                    Toast.makeText(PaymentTotalActivity.this, "Please Select the Set Recurring Option", Toast.LENGTH_SHORT).show();

                } else {
                    dialog();
                }

            }
        });
        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_paymnettoatl.setText("$0.00");
                SharedPreferences.Editor editor;
                SharedPreferences settings1 = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
                editor = settings1.edit();
                editor.putInt("count", 0);
                count = 0;
                editor.commit();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PaymentTotalActivity.this, DonationCostListActvity.class);
        startActivity(intent);

        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
        editor = settings.edit();
        editor.putInt("count", 0);
        editor.commit();

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.check_never:
                if (checked) {
                    str_check = "Never";

                }
                // Put some meat on the sandwich

                // Remove the meat
                break;
            case R.id.check_daily:

                if (checked) {
                    str_check = "Daily";
                }
                // Cheese me

                // I'm lactose intolerant
                break;
            case R.id.check_weekly:
                if (checked) {
                    str_check = "Weekly";

                }
                // Cheese me

                // I'm lactose intolerant
                break;
            case R.id.check_biweekly:
                if (checked) {
                    str_check = "Bi-Weekly";

                }
                // Cheese me

                // I'm lactose intolerant
                break;
            case R.id.check_1st:
                if (checked) {
                    str_check = "1st and 16th";

                }
                // Cheese me
                // I'm lactose intolerant
                break;
            case R.id.check_monthly:
                if (checked) {
                    str_check = "Monthly";

                }
                // Cheese me
                // I'm lactose intolerant
                break;
            case R.id.check_other:
                if (checked) {
                    str_check = "Other";

                }
                // Cheese me

                // I'm lactose intolerant
                break;
            // TODO: Veggie sandwich
        }
    }

    public void dialog() {
        dialog = new Dialog(PaymentTotalActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_payment);
        //   final EditText et_exprydate = (EditText) dialog.findViewById(R.id.et_exprydate);
        final EditText et_cvvname = (EditText) dialog.findViewById(R.id.et_cvvName);
        final EditText et_cardNo = (EditText) dialog.findViewById(R.id.et_cardNo1);
        et_year = (EditText) dialog.findViewById(R.id.et_cardNo2);
        et_month = (EditText) dialog.findViewById(R.id.et_cardNo3);
        final EditText et_cardName = (EditText) dialog.findViewById(R.id.et_cardName);
        Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        TextView tv_amount = (TextView) dialog.findViewById(R.id.tv_amount);

        tv_amount.setText("$" + String.valueOf(count));

        dialog.show();
        dialog.setCancelable(true);


        et_cardNo.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            boolean isDelete = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0)
                    isDelete = false;
                else
                    isDelete = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String source = editable.toString();
                int length = source.length();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(source);

                if (length > 0 && length % 5 == 0) {
                    if (isDelete)
                        stringBuilder.deleteCharAt(length - 1);
                    else
                        stringBuilder.insert(length - 1, " ");

                    et_cardNo.setText(stringBuilder);
                    et_cardNo.setSelection(et_cardNo.getText().length());

                }
            }
        });


        et_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogYear();
            }
        });
        et_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMonth();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start progress dialog
                /*-------------------------------------------------strip-------------------------------------*/

                if (et_cardNo.getText().toString().equals("")) {
                    et_cardNo.setError("Please Enter the Card Number");
                } else if (et_month.getText().toString().equals("")) {
                    et_month.setError("Please Select the Month");
                } else if (et_year.getText().toString().equals("")) {
                    et_year.setError("Please Select the Year");
                } else if (et_cvvname.getText().toString().equals("")) {
                    et_cvvname.setError("Please Enter CVV Number");
                } else if (et_cardName.getText().toString().equals("")) {
                    et_cardName.setError("Please Enter Holder Name");
                } else if (!et_cardNo.getText().toString().equals("4242 4242 4242 4242")) {
                    et_cardNo.setError("Please Enter Valid Card Number ");

                } else {
                    String month = et_month.getText().toString();
                    String year = et_year.getText().toString();
                    String cardnumber = et_cardNo.getText().toString();
                    String cvv = et_cvvname.getText().toString();
                    Log.d("strip", "monthField:\t" + month + "\tyearField:" + year + "\tcardnumber:\t" + cardnumber + "\tcvv:\t" + cvv);

                    Card card = new Card(
                            cardnumber,
                            Integer.valueOf(month),
                            Integer.valueOf(year),
                            cvv
                    );
                    if (card == null) {
                        Toast.makeText(PaymentTotalActivity.this, "Please Enter Card Information First..", Toast.LENGTH_LONG).show();

                        Log.d("card null exception", "card null exception");
                    } else {
                        loadingDialog.show();

                        //Stripe stripe = new Stripe(ActivityGEtMoreCreditsStripe.this, "pk_live_zm8S5ULvHPyj4tX5YYIMK1JQ");
                        Stripe stripe = new Stripe(PaymentTotalActivity.this, "pk_test_AkMHGCGjHuulfclLc3El7ky2");

                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        loadingDialog.show();
                                        System.out.println("afadsfasdf Success" + token.getId());
                                        Log.d("TokenStripe", "is:\t" + token.getId());
                                        SharedPreferences settings;
                                        SharedPreferences.Editor editor;
                                        settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
                                        editor = settings.edit();
                                        editor.putInt("count", count);


                                        saveCard(token.getId());

                                    }

                                    public void onError(Exception error) {
                                        System.out.println("afadsfasdf Failre");

                                    }
                                }
                        );
                    }


                }

            }
        });






        /*------------------------------------expiry date validation----------------------------*//*
        et_exprydate.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
// TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
// TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String working = s.toString();
                boolean isValid = true;
                if (working.length() == 2 && before == 0) {
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 12) {
                        isValid = false;
                    } else {
                        working += "/";
                        et_exprydate.setText(working);
                        et_exprydate.setSelection(working.length());
                    }
                } else if (working.length() != 5) {
                    isValid = false;
                }

                if (!isValid) {
                    et_exprydate.setError("Enter a valid date: MM/YY");
                } else {
                    et_exprydate.setError(null);
                }
            }
        });
*/

    }

    private void saveCard(final String id) {

        dialog.dismiss();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_STRIPE_PAYMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int message_code;
                        String message;
                        loadingDialog.dismiss();
                        System.out.println("resLogin" + response);

                        try {
                            final JSONObject jsonObj = new JSONObject(response);
                            message_code = jsonObj.getInt("message_code");

                            message = jsonObj.getString("message");
                            if (message_code == 1) {


                                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentTotalActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int id) {
                                                try {
                                                    Intent intent = new Intent(PaymentTotalActivity.this, RecipetDetailActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                    SharedPreferences settings;
                                                    SharedPreferences.Editor editor;
                                                    settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
                                                    editor = settings.edit();
                                                    editor.putString("envelop_no", jsonObj.getString("envelop_no"));
                                                    editor.putString("Receipt_id", jsonObj.getString("Receipt_id"));
                                                    editor.putString("cid", jsonObj.getString("cid"));
                                                    editor.putString("donation_amount", String.valueOf(count));
                                                    editor.putString("Approval_code", jsonObj.getString("Approval_code"));
                                                    editor.putInt("count", 0);

                    /*Gson gson = new Gson();
                    String json = gson.toJson(strings);*/
                                                    editor.putString("Key", str_check);
                                                    editor.commit();

                                                } catch (JSONException e) {
                                                    System.out.println("jsonexeption" + e.toString());
                                                }
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                                // setData(credits);


                            } else {


                                message = jsonObj.getString("message");

                                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentTotalActivity.this);
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

                        Toast.makeText(PaymentTotalActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //user_id
                    params.put("stripeToken", id);
                    params.put("donation_amount", String.valueOf(count));
                    params.put("reg_id", user_Id);
                    params.put("cid", church_id);
                    params.put("set_recurring", str_check);
                    params.put("donation_for", str_checksdonatioon);
                    //params.put(AppConstant.KEY_AMOUNT_PAID, transactionId);


                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                    Log.e("Exception", e.getMessage());
                }


                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentTotalActivity.this);
        requestQueue.add(stringRequest);
        stringRequest.setShouldCache(false);
    }


    public void dialogYear() {
        Dialog dialog = new Dialog(PaymentTotalActivity.this);
        dialog.setContentView(R.layout.dialog_month);

        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Year");

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i <= 2060; i++) {
            years.add(Integer.toString(i));
        }
        YearAdaptor adapter = new YearAdaptor(PaymentTotalActivity.this, years, onCallBack, dialog);
        lv.setAdapter(adapter);

        dialog.show();
    }

    public void dialogMonth() {
        Dialog dialog = new Dialog(PaymentTotalActivity.this);
        dialog.setContentView(R.layout.dialog_month);

        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Month");

        ArrayList<String> years = new ArrayList<String>();
        years.add("01");
        years.add("02");
        years.add("03");
        years.add("04");
        years.add("05");
        years.add("06");
        years.add("07");
        years.add("08");
        years.add("09");
        years.add("10");
        years.add("11");
        years.add("12");

        MonthAdaptor adapter = new MonthAdaptor(PaymentTotalActivity.this, years, onCallBack, dialog);
        lv.setAdapter(adapter);

        dialog.show();
    }

    @Override
    public void callback(String count) {
        et_month.setText(count);
    }

    @Override
    public void callbackYear(String count) {
        et_year.setText(count);

    }

    public void dialog1() {
        final Dialog dialog = new Dialog(PaymentTotalActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_other);

        final EditText et_amount = (EditText) dialog.findViewById(R.id.et_amount);
        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.btn_submit);
        ImageView iv_cancle = (ImageView) dialog.findViewById(R.id.iv_cancle);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                if (et_amount.getText().toString().equals("")) {
                    Toast.makeText(PaymentTotalActivity.this, "Please Enter Other Set Recurring", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    count = Integer.valueOf(et_amount.getText().toString());
                  //  btn_cost.setText("$" + String.valueOf(count));
                   // btn_cost.setVisibility(View.VISIBLE);
                }
            }
        });
        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
