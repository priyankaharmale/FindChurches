package hnweb.com.findchurches.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Calendar;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.model.ChurchModelClass;

public class HistoryDetailActivity extends AppCompatActivity {
    ChurchModelClass churchModelClass;
    TextView tv_address, tv_churchname, tv_envolpe, tv_totalamount, tv_receiptNo, tv_date, tv_approvedcode;
    Button btn_payment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historydetails);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_churchname = (TextView) findViewById(R.id.tv_churchname);
        tv_envolpe = (TextView) findViewById(R.id.tv_envolpe);
        tv_totalamount = (TextView) findViewById(R.id.tv_totalamount);
        tv_receiptNo = (TextView) findViewById(R.id.tv_receiptNo);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_approvedcode = (TextView) findViewById(R.id.tv_approvedcode);
        btn_payment = (Button) findViewById(R.id.btn_payment);

        final SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryDetailActivity.this, ChurchDetailActivity.class);
                startActivity(intent);
            }
        });
        Gson gson = new Gson();
        String json = settings.getString("MyObject", "");
        churchModelClass = gson.fromJson(json, ChurchModelClass.class);

        tv_receiptNo.setText(churchModelClass.getStripTractionID());
        tv_totalamount.setText(churchModelClass.getDonation_amount());
        tv_approvedcode.setText("Successed");

        tv_churchname.setText(churchModelClass.getCname());
        tv_address.setText(churchModelClass.getAddress() + " " + churchModelClass.getCity() + " " + churchModelClass.getState() + " " + churchModelClass.getCountry() + " " + churchModelClass.getZipcode());


        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        tv_date.setText(churchModelClass.getCreated_on());

    }
}
