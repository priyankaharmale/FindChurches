package hnweb.com.findchurches.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import hnweb.com.findchurches.utility.SharedPreference;
import hnweb.com.findchurches.utility.Utils;
import hnweb.com.findchurches.utility.Validations;


/**
 */

public class LoginActivity extends AppCompatActivity {

    TextView tv_click_here;
    Button btn_login, btn_createacc;
    EditText et_email, et_password;
    TextInputLayout input_email, input_password;
    LoadingDialog loadingDialog;
    String  reg_id ,name, email1, mobno, profile_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadingDialog = new LoadingDialog(LoginActivity.this);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_createacc = (Button) findViewById(R.id.btn_createacc);
        tv_click_here = (TextView) findViewById(R.id.tv_click_here);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        input_email = (TextInputLayout) findViewById(R.id.input_email);
        input_password = (TextInputLayout) findViewById(R.id.input_password);

        btn_createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        tv_click_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {

                    if (Utils.isNetworkAvailable(LoginActivity.this)) {

                        String password = et_password.getText().toString();
                        String email = et_email.getText().toString();
                        login(email, password);
                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }
            }
        });


    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validations.hasText_input_layout(et_email, "Please Enter Email ID ", input_email))
            ret = false;
        if (!Validations.hasText_input_layout(et_password, "Please Enter RE-Enter Password", input_password))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_password, "Password atleast 7 characters", input_password))
            ret = false;
        if (!Validations.isEmailAddress_input_layout(et_email, true, "Please Enter Valid Email ID", input_email))
            ret = false;


        return ret;
    }

    private void login(final String email, final String password) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_LOGIN,
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

                                JSONObject jsonObject = j.getJSONObject("details");
                                reg_id=jsonObject.getString("reg_id");
                                name=jsonObject.getString("name");
                                email1=jsonObject.getString("email");
                                mobno=jsonObject.getString("mobno");
                                profile_photo=jsonObject.getString("profile_photo");

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                               SharedPreference.profileSave(getApplicationContext(), reg_id, name, email, mobno, profile_photo);
                                                SharedPreferences settings;
                                                SharedPreferences.Editor editor;
                                                settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
                                                editor = settings.edit();
                                                editor.commit();
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                        String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                        AlertUtility.showAlert(LoginActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.KEY_EMAIL, email);
                    params.put(AppConstant.KEY_PASSWORD, password);
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


}
