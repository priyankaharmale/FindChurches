package hnweb.com.findchurches.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.Calendar;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.model.ChurchModelClass;

public class RecipetDetailActivity extends AppCompatActivity {
    ChurchModelClass churchModelClass;
    TextView tv_address, tv_churchname, tv_envolpe, tv_totalamount, tv_receiptNo, tv_date, tv_approvedcode, tv_history;
    Button btn_payment;
    EditText et_commnet;
    ImageView iv_twitter, iv_facebook;
    ShareDialog shareDialog;
    boolean isAppInstalled;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_recipt);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_churchname = (TextView) findViewById(R.id.tv_churchname);
        tv_envolpe = (TextView) findViewById(R.id.tv_envolpe);
        tv_totalamount = (TextView) findViewById(R.id.tv_totalamount);
        tv_receiptNo = (TextView) findViewById(R.id.tv_receiptNo);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_approvedcode = (TextView) findViewById(R.id.tv_approvedcode);
        btn_payment = (Button) findViewById(R.id.btn_payment);
        iv_twitter = (ImageView) findViewById(R.id.iv_twitter);
        iv_facebook = (ImageView) findViewById(R.id.iv_facebook);
        et_commnet = (EditText) findViewById(R.id.et_commnet);
        tv_history = (TextView) findViewById(R.id.tv_history);
        shareDialog = new ShareDialog(this);

        isAppInstalled = appInstalledOrNot("com.facebook");


        final SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1

        tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipetDetailActivity.this, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipetDetailActivity.this, ChurchDetailActivity.class);
                startActivity(intent);
            }
        });
        Gson gson = new Gson();
        String json = settings.getString("MyObject", "");
        churchModelClass = gson.fromJson(json, ChurchModelClass.class);

        tv_envolpe.setText(settings.getString("envelop_no", ""));
        tv_receiptNo.setText(settings.getString("Receipt_id", ""));
        tv_totalamount.setText(settings.getString("donation_amount", ""));
        tv_approvedcode.setText(settings.getString("Approval_code", ""));

        tv_churchname.setText(churchModelClass.getCname());
        tv_address.setText(churchModelClass.getAddress() + " " + churchModelClass.getCity() + " " + churchModelClass.getState() + " " + churchModelClass.getCountry() + " " + churchModelClass.getZipcode());


        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        tv_date.setText(mydate);

        iv_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_commnet.getText().toString().equals("")) {
                    et_commnet.setError("Please Enter the Post");
                } else {

                    TweetComposer.Builder builder = new TweetComposer.Builder(RecipetDetailActivity.this)
                            .text(et_commnet.getText().toString());
                    builder.show();
                    //  share();
                }
            }
        });


        iv_facebook.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCheckPermission")
            @Override
            public void onClick(View view) {

                if (et_commnet.getText().toString().equals("")) {
                    et_commnet.setError("Please Enter the Post");
                } else {
                    if (isAppInstalled) {
                        // checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);

                        //   pushAppointmentsToCalender(SharePostActivity.this, "sdfsd", "sdfsd", "Pune", 1, 12 - 04 - 2018, true, true);

                        // Show facebook ShareDialog

                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(et_commnet.getText().toString())

                                    // .setImageUrl(Uri.parse("https://www.numetriclabz.com/wp-content/uploads/2015/11/114.png"))
                                    .build();
                            shareDialog.show(linkContent);
                        }
                    } else {
                       /* Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                        // Add data to the intent, the receiving app will decide
                        // what to do with it.
                        share.putExtra(Intent.EXTRA_SUBJECT, et_commnet.getText().toString());

                        startActivity(Intent.createChooser(share, "Share"));*/

                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(et_commnet.getText().toString())

                                // .setImageUrl(Uri.parse("https://www.numetriclabz.com/wp-content/uploads/2015/11/114.png"))
                                .build();
                        shareDialog.show(linkContent);
                    }
                }
            }
        });

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
