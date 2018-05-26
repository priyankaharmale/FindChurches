package hnweb.com.findchurches.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.fragment.ChurchLoctionFragment;
import hnweb.com.findchurches.fragment.DonateNowFragment;
import hnweb.com.findchurches.fragment.EventCalenderFragment;
import hnweb.com.findchurches.fragment.FavoriteListFragment;
import hnweb.com.findchurches.fragment.ProfileFragment;

/**
 * Created by PC-21 on 09-May-18.
 */

public class ChurchDetailActivity extends AppCompatActivity {
    ImageView iv_profile, iv_location, iv_calender, iv_donate, iv_favarite, iv_church;
    String str_ChurchImage, str_back;
    ProgressBar progress;
    String isClick_profile = "1";
    String isClick_loaction = "1";
    String isClick_event = "1";
    String isClick_fav = "1";
    String isClick_donate = "1";

    Button btn_doanteNow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_church_deatil);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        iv_location = (ImageView) findViewById(R.id.iv_location);
        iv_calender = (ImageView) findViewById(R.id.iv_calender);
        iv_donate = (ImageView) findViewById(R.id.iv_donate);
        iv_favarite = (ImageView) findViewById(R.id.iv_favarite);
        progress = (ProgressBar) findViewById(R.id.progress);
        iv_church = (ImageView) findViewById(R.id.iv_church);
        btn_doanteNow = (Button) findViewById(R.id.btn_doanteNow);
        SharedPreferences settings = getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        str_ChurchImage = settings.getString("ChurchImage", null);
        if (getIntent().hasExtra("eventBack")) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                str_back = bundle.getString("eventBack");

            } else {
                Log.e("null", "null");
            }
        }
        if (str_back == null) {
            initView();
        } else {
            isClick_event = "0";
            iv_calender.setImageResource(R.drawable.calendar_white);
            iv_profile.setImageResource(R.drawable.profile);
            iv_location.setImageResource(R.drawable.placeholder);
            iv_favarite.setImageResource(R.drawable.favorite);
            iv_donate.setImageResource(R.drawable.donation);

            initView1();
        }
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick_profile.equals("1")) {
                    iv_profile.setImageResource(R.drawable.profile_white);
                    iv_location.setImageResource(R.drawable.placeholder);
                    iv_calender.setImageResource(R.drawable.calendar);
                    iv_favarite.setImageResource(R.drawable.favorite);
                    iv_donate.setImageResource(R.drawable.donation);

                    isClick_profile = "0";
                    Fragment fragmentT = new ProfileFragment();
                    replace(fragmentT);
                } else {
                    iv_profile.setImageResource(R.drawable.profile);
                    isClick_profile = "1";
                }
            }
        });
        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick_loaction.equals("1")) {
                    iv_location.setImageResource(R.drawable.placeholder_white);
                    iv_profile.setImageResource(R.drawable.profile);
                    iv_calender.setImageResource(R.drawable.calendar);
                    iv_favarite.setImageResource(R.drawable.favorite);
                    iv_donate.setImageResource(R.drawable.donation);

                    isClick_loaction = "0";
                    Fragment fragmentT = new ChurchLoctionFragment();
                    replace(fragmentT);
                } else {
                    iv_location.setImageResource(R.drawable.placeholder);
                    isClick_loaction = "1";
                }

            }
        });
        iv_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isClick_event.equals("1")) {
                    iv_calender.setImageResource(R.drawable.calendar_white);
                    iv_profile.setImageResource(R.drawable.profile);
                    iv_location.setImageResource(R.drawable.placeholder);
                    iv_favarite.setImageResource(R.drawable.favorite);
                    iv_donate.setImageResource(R.drawable.donation);

                    isClick_event = "0";
                    Fragment fragment = new EventCalenderFragment();
                    replace(fragment);
                } else {
                    iv_calender.setImageResource(R.drawable.calendar);
                    isClick_event = "1";
                }


            }
        });
        iv_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick_donate.equals("1")) {
                    iv_donate.setImageResource(R.drawable.donation_white);
                    iv_calender.setImageResource(R.drawable.calendar);
                    iv_profile.setImageResource(R.drawable.profile);
                    iv_location.setImageResource(R.drawable.placeholder);
                    iv_favarite.setImageResource(R.drawable.favorite);
                    isClick_donate = "0";
                    Fragment fragment = new DonateNowFragment();
                    replace(fragment);
                } else {
                    iv_donate.setImageResource(R.drawable.donation);
                    isClick_donate = "1";
                }
            }
        });

        iv_favarite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isClick_fav.equals("1")) {
                    iv_favarite.setImageResource(R.drawable.favorite_white);
                    iv_profile.setImageResource(R.drawable.profile);
                    iv_location.setImageResource(R.drawable.placeholder);
                    iv_calender.setImageResource(R.drawable.calendar);
                    iv_donate.setImageResource(R.drawable.donation);

                    isClick_fav = "0";
                } else {
                    iv_favarite.setImageResource(R.drawable.favorite);
                    isClick_fav = "1";
                }
                Fragment fragment = new FavoriteListFragment();
                replace(fragment);
            }
        });

        try {
            Glide.with(this)
                    .load(str_ChurchImage)
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

        btn_doanteNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChurchDetailActivity.this, DonationCostListActvity.class);SharedPreferences settings;
                startActivity(intent);
            }
        });
        //  initView();
    }

    private void initView() {
        Fragment fragment = new ProfileFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();

    }

    private void initView1() {
        Fragment fragment = new EventCalenderFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();

    }

    private void replace(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
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
