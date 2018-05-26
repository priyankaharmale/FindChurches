package hnweb.com.findchurches.adaptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import java.util.ArrayList;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.activity.ChurchDetailActivity;
import hnweb.com.findchurches.activity.HistoryDetailActivity;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.model.ChurchModelClass;
import hnweb.com.findchurches.model.PaymentModelClass;

import static hnweb.com.findchurches.utility.SharedPreference.PREFS_NAME;


/**
 * Created by hnwebmarketing on 1/27/2018.
 */

public class HistoryListAdaptor extends RecyclerView.Adapter<HistoryListAdaptor.MyViewHolder> {
    private ArrayList<ChurchModelClass> paymentModelClasses;
    Context context;
    Drawable drawable;

    public HistoryListAdaptor(ArrayList<ChurchModelClass> paymentModelClasses, Context context) {

        this.paymentModelClasses = paymentModelClasses;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_churchName, tv_churchAddress, tv_camount, date;
        public ImageView iv_church;
        public ProgressBar progress_item;

        public MyViewHolder(View view) {
            super(view);
            tv_churchName = (TextView) view.findViewById(R.id.tv_churchname);
            tv_churchAddress = (TextView) view.findViewById(R.id.tv_churchaddress);
            tv_camount = (TextView) view.findViewById(R.id.tv_camount);
            date = (TextView) view.findViewById(R.id.date);
            iv_church = (ImageView) view.findViewById(R.id.iv_church);
            progress_item = (ProgressBar) view.findViewById(R.id.progress);
            drawable = ContextCompat.getDrawable(context, R.drawable.worship_icon_gray1);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_historylist, parent, false);
        System.out.println("adaptor_favoritelist." + paymentModelClasses.size());

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ChurchModelClass churchModelClass = paymentModelClasses.get(position);
        holder.tv_camount.setText("$" + churchModelClass.getDonation_amount());
        holder.date.setText(churchModelClass.getCreated_on());
        holder.tv_churchName.setText(churchModelClass.getCname());
        holder.tv_churchAddress.setText(churchModelClass.getAddress());

        try {
            Glide.with(context)
                    .load(churchModelClass.getCimg())
                    .error(drawable)
                    .centerCrop()
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.progress_item.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progress_item.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.iv_church);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HistoryDetailActivity.class);

                // intent2.putExtra("EventList", selected_Date_events);
                context.startActivity(intent);

                SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
                SharedPreferences.Editor editor = settings.edit();

                Gson gson = new Gson();
                String json = gson.toJson(churchModelClass);
                editor.putString("MyObject", json);


                editor.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentModelClasses.size();
    }


    public void filterList(ArrayList<ChurchModelClass> filterdNames) {
        this.paymentModelClasses = filterdNames;
        notifyDataSetChanged();
    }

}
