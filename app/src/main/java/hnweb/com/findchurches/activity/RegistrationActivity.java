package hnweb.com.findchurches.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.utility.AlertUtility;
import hnweb.com.findchurches.utility.AppUtils;
import hnweb.com.findchurches.utility.LoadingDialog;
import hnweb.com.findchurches.utility.Utils;
import hnweb.com.findchurches.utility.Validations;


/**
 */

public class RegistrationActivity extends AppCompatActivity {

    Button btn_registernow, btn_login;
    EditText et_name, et_email, et_phone, et_createpwd, et_reenpwd;
    TextInputLayout input_name, input_email, input_phone, input_createpwd, input_reenpwd;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loadingDialog = new LoadingDialog(RegistrationActivity.this);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_registernow = (Button) findViewById(R.id.btn_registernow);

        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_createpwd = (EditText) findViewById(R.id.et_createpwd);
        et_reenpwd = (EditText) findViewById(R.id.et_reenpwd);

        input_name = (TextInputLayout) findViewById(R.id.input_name);
        input_email = (TextInputLayout) findViewById(R.id.input_email);
        input_phone = (TextInputLayout) findViewById(R.id.input_phone);
        input_createpwd = (TextInputLayout) findViewById(R.id.input_createpwd);
        input_reenpwd = (TextInputLayout) findViewById(R.id.input_reenpwd);


        btn_registernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidation()) {

                    if (Utils.isNetworkAvailable(RegistrationActivity.this)) {


                        String password = et_createpwd.getText().toString();
                        String email = et_email.getText().toString();
                        String phoneNo = et_phone.getText().toString();
                        String name = et_name.getText().toString();

                        if (!et_createpwd.getText().toString().equals(et_reenpwd.getText().toString())) {
                            Toast.makeText(RegistrationActivity.this, "Password Not matching ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        register(name, phoneNo, email, password);
                    } else {
                       Utils.myToast1(RegistrationActivity.this);
                    }
                }
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_getuniversicity_name = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i_getuniversicity_name);

            }
        });
    }


    private void register(final String name, final String phoneNo, final String email, final String password) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_REGISTER,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        System.out.println("res_register" + response);

                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent i_getuniversicity_name = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                startActivity(i_getuniversicity_name);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
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
                        String reason = AppUtils.getVolleyError(RegistrationActivity.this, error);
                        AlertUtility.showAlert(RegistrationActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.KEY_NAME, name);
                    params.put(AppConstant.KEY_EMAIL, email);
                    params.put(AppConstant.KEY_PASSWORD, password);
                    params.put(AppConstant.KEY_PHONE, phoneNo);

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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private boolean checkValidation() {

        boolean ret = true;

        if (!Validations.hasText_input_layout(et_name, "Please Enter Name", input_name))
            ret = false;

        if (!Validations.hasText_input_layout(et_phone, "Please Enter Phone no", input_phone))
            ret = false;

        if (!Validations.hasText_input_layout(et_createpwd, "Please Enter Password", input_createpwd))
            ret = false;

        if (!Validations.hasText_input_layout(et_email, "Please Enter Email ID ", input_email))
            ret = false;
        if (!Validations.hasText_input_layout(et_reenpwd, "Please Enter RE-Enter Password", input_reenpwd))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_createpwd, "Password atleast 7 characters", input_createpwd))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_reenpwd, "Password atleast 7 characters", input_reenpwd))
            ret = false;
        if (!Validations.isEmailAddress_input_layout(et_email, true, "Please Enter Valid Email ID", input_email))
            ret = false;


        return ret;
    }
}
