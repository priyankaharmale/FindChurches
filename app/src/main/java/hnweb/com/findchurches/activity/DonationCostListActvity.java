package hnweb.com.findchurches.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.adaptor.CustomGridAdaptor;
import hnweb.com.findchurches.utility.LoadingDialog;
import hnweb.com.findchurches.utility.SharedPreference;

public class DonationCostListActvity extends AppCompatActivity {
    GridView simpleGrid;
    String logos[] = {"$10", "$25", "$50", "$100", "$250", "$500", "$1000", "Other", "Clear"};
    Button btn_10doller, btn_25doller, btn_50doller, btn_100doller, btn_cost, btn_250doller, btn_500doller, btn_1000doller, btn_other, btn_clear;

    String isClick_10doller = "1", isClick_25doller = "1", isClick_50doller = "1",
            isClick_100doller = "1", isClick_250doller = "1", isClick_500doller = "1", isClick_1000doller = "1";
    Button btn_payment;
    int count = 0, total = 0;
    CheckBox check_youthtrip, check_cyber, check_other;
    ArrayList<String> strings;
    String str_check = "";
    int countSave = 0;
    ArrayList<String> stringsSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        final SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
        total = settings.getInt("count", 0);

        simpleGrid = (GridView) findViewById(R.id.simpleGridView); // init GridView
        btn_10doller = (Button) findViewById(R.id.btn_10doller);
        btn_25doller = (Button) findViewById(R.id.btn_25doller);
        btn_50doller = (Button) findViewById(R.id.btn_50doller);
        btn_100doller = (Button) findViewById(R.id.btn_100doller);
        btn_250doller = (Button) findViewById(R.id.btn_250doller);
        btn_500doller = (Button) findViewById(R.id.btn_500doller);
        btn_1000doller = (Button) findViewById(R.id.btn_1000doller);
        btn_other = (Button) findViewById(R.id.btn_other);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_payment = (Button) findViewById(R.id.btn_payment);
        btn_cost = (Button) findViewById(R.id.btn_cost);
        strings = new ArrayList<>();
        check_youthtrip = (CheckBox) findViewById(R.id.check_youthtrip);
        check_cyber = (CheckBox) findViewById(R.id.check_cyber);
        check_other = (CheckBox) findViewById(R.id.check_other);
        btn_cost.setVisibility(View.GONE);

        btn_10doller.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (isClick_10doller.equals("1")) {
                    btn_10doller.setBackgroundResource(R.drawable.small_box_blue);
                    btn_25doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_50doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_100doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_250doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_500doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_1000doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_other.setBackgroundResource(R.drawable.small_box_line);
                    btn_cost.setVisibility(View.GONE);


                    count = 10;
                    isClick_10doller = "0";
                } else {
                    btn_10doller.setBackgroundResource(R.drawable.small_box_line);
                    count = 0;

                    isClick_10doller = "1";
                }
            }
        });
        btn_25doller.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (isClick_25doller.equals("1")) {
                    btn_25doller.setBackgroundResource(R.drawable.small_box_blue);
                    btn_10doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_50doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_100doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_250doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_500doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_1000doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_other.setBackgroundResource(R.drawable.small_box_line);
                    btn_cost.setVisibility(View.GONE);

                    isClick_25doller = "0";
                    count = 25;

                } else {
                    btn_25doller.setBackgroundResource(R.drawable.small_box_line);
                    isClick_25doller = "1";
                    count = 0;


                }
            }
        });

        btn_50doller.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (isClick_50doller.equals("1")) {
                    btn_50doller.setBackgroundResource(R.drawable.small_box_blue);
                    isClick_50doller = "0";
                    count = 50;
                    btn_25doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_10doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_100doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_250doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_500doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_1000doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_other.setBackgroundResource(R.drawable.small_box_line);
                    btn_cost.setVisibility(View.GONE);

                } else {
                    btn_50doller.setBackgroundResource(R.drawable.small_box_line);
                    isClick_50doller = "1";
                    count = 0;

                }
            }
        });
        btn_100doller.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (isClick_100doller.equals("1")) {
                    btn_100doller.setBackgroundResource(R.drawable.small_box_blue);
                    isClick_100doller = "0";

                    btn_50doller.setBackgroundResource(R.drawable.small_box_line);

                    btn_25doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_10doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_250doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_500doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_1000doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_other.setBackgroundResource(R.drawable.small_box_line);
                    btn_cost.setVisibility(View.GONE);

                    count = 100;

                } else {
                    btn_100doller.setBackgroundResource(R.drawable.small_box_line);
                    isClick_100doller = "1";
                    count = 0;

                }
            }
        });
        btn_250doller.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (isClick_250doller.equals("1")) {
                    btn_250doller.setBackgroundResource(R.drawable.small_box_blue);
                    btn_50doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_25doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_10doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_100doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_500doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_1000doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_other.setBackgroundResource(R.drawable.small_box_line);
                    btn_cost.setVisibility(View.GONE);

                    isClick_250doller = "0";
                    count = 250;

                } else {
                    btn_250doller.setBackgroundResource(R.drawable.small_box_line);
                    isClick_250doller = "1";
                    count = 0;

                }
            }
        });

        btn_500doller.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (isClick_500doller.equals("1")) {
                    btn_500doller.setBackgroundResource(R.drawable.small_box_blue);
                    isClick_500doller = "0";
                    btn_50doller.setBackgroundResource(R.drawable.small_box_line);

                    btn_25doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_10doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_100doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_250doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_1000doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_other.setBackgroundResource(R.drawable.small_box_line);
                    btn_cost.setVisibility(View.GONE);

                    count = 500;

                } else {
                    btn_500doller.setBackgroundResource(R.drawable.small_box_line);
                    isClick_500doller = "1";
                    count = 0;

                }
            }
        });


        btn_1000doller.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (isClick_1000doller.equals("1")) {
                    btn_1000doller.setBackgroundResource(R.drawable.small_box_blue);
                    btn_50doller.setBackgroundResource(R.drawable.small_box_line);

                    btn_25doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_10doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_100doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_250doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_500doller.setBackgroundResource(R.drawable.small_box_line);
                    btn_other.setBackgroundResource(R.drawable.small_box_line);
                    btn_cost.setVisibility(View.GONE);

                    isClick_1000doller = "0";
                    count = 1000;

                } else {
                    btn_1000doller.setBackgroundResource(R.drawable.small_box_line);
                    isClick_1000doller = "1";
                    count = 0;

                }
            }
        });

        btn_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_other.setBackgroundResource(R.drawable.small_box_line);
                btn_1000doller.setBackgroundResource(R.drawable.small_box_line);
                btn_50doller.setBackgroundResource(R.drawable.small_box_line);
                btn_25doller.setBackgroundResource(R.drawable.small_box_line);
                btn_10doller.setBackgroundResource(R.drawable.small_box_line);
                btn_100doller.setBackgroundResource(R.drawable.small_box_line);
                btn_250doller.setBackgroundResource(R.drawable.small_box_line);
                btn_500doller.setBackgroundResource(R.drawable.small_box_line);
                btn_cost.setVisibility(View.GONE);

                dialog();

            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_other.setBackgroundResource(R.drawable.small_box_line);
                btn_1000doller.setBackgroundResource(R.drawable.small_box_line);
                btn_50doller.setBackgroundResource(R.drawable.small_box_line);
                btn_25doller.setBackgroundResource(R.drawable.small_box_line);
                btn_10doller.setBackgroundResource(R.drawable.small_box_line);
                btn_100doller.setBackgroundResource(R.drawable.small_box_line);
                btn_250doller.setBackgroundResource(R.drawable.small_box_line);
                btn_500doller.setBackgroundResource(R.drawable.small_box_line);
                btn_cost.setVisibility(View.GONE);
                count = 0;

            }
        });
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0) {
                    Toast.makeText(DonationCostListActvity.this, "Please Select the Cost", Toast.LENGTH_SHORT).show();
                } else if (str_check.equals("") || str_check == null) {
                    Toast.makeText(DonationCostListActvity.this, "Please Select Donation For", Toast.LENGTH_SHORT).show();

                } else {
                    btn_cost.setVisibility(View.GONE);
                    btn_other.setBackgroundResource(R.drawable.small_box_line);
                    Intent intent = new Intent(DonationCostListActvity.this, PaymentTotalActivity.class);
                    startActivity(intent);
                    finish();
                    SharedPreferences settings;
                    SharedPreferences.Editor editor;
                    settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
                    editor = settings.edit();
                    total = total + count;
                    editor.putInt("count", total);

                    /*Gson gson = new Gson();
                    String json = gson.toJson(strings);*/
                    editor.putString("Key", str_check);
                    editor.commit();

                }

            }
        });
        CustomGridAdaptor customAdapter = new CustomGridAdaptor(getApplicationContext(), logos);
        simpleGrid.setAdapter(customAdapter);

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.check_cyber:
                if (checked) {
                    str_check = "Cyber Giving Sunday";
                    // strings.add(str_check);
                }
                // Put some meat on the sandwich

                // Remove the meat
                break;
            case R.id.check_youthtrip:

                if (checked) {
                    str_check = "Youth Trip";
                    //  strings.add(str_check);
                }
                // Cheese me

                // I'm lactose intolerant
                break;
            case R.id.check_other:
                if (checked) {
                    str_check = "Other";
                    // strings.add(str_check);
                }
                // Cheese me

                // I'm lactose intolerant
                break;
            // TODO: Veggie sandwich
        }
    }

    public void dialog() {
        final Dialog dialog = new Dialog(DonationCostListActvity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_donation);

        final EditText et_amount = (EditText) dialog.findViewById(R.id.et_amount);
        dialog.show();
        Button declineButton = (Button) dialog.findViewById(R.id.btn_submit);
        ImageView iv_cancle = (ImageView) dialog.findViewById(R.id.iv_cancle);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                if (et_amount.getText().toString().equals("")) {
                    Toast.makeText(DonationCostListActvity.this, "Please Enter the amount", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    count = Integer.valueOf(et_amount.getText().toString());
                    btn_cost.setText("$" + String.valueOf(count));
                    btn_cost.setVisibility(View.VISIBLE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
        editor = settings.edit();
        editor.putInt("count", 0);
        editor.commit();
    }
}
