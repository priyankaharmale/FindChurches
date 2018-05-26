package hnweb.com.findchurches.adaptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.model.ChurchModelClass;

import static hnweb.com.findchurches.utility.SharedPreference.PREFS_NAME;


/**
 * Created by hnwebmarketing on 1/27/2018.
 */

public class CharityListAdaptor extends RecyclerView.Adapter<CharityListAdaptor.MyViewHolder> {
    private ArrayList<ChurchModelClass> churchModelClasses;
    Context context;
    Drawable drawable;

    public CharityListAdaptor(ArrayList<ChurchModelClass> churchModelClasses, Context context) {

        this.churchModelClasses = churchModelClasses;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_churchName, tv_churchAddress;
        public ImageView iv_church;
        public ProgressBar progress_item;

        public MyViewHolder(View view) {
            super(view);
            tv_churchName = (TextView) view.findViewById(R.id.tv_churchname);
            tv_churchAddress = (TextView) view.findViewById(R.id.tv_churchaddress);
            iv_church = (ImageView) view.findViewById(R.id.iv_church);
            progress_item = (ProgressBar) view.findViewById(R.id.progress);
            drawable = ContextCompat.getDrawable(context, R.drawable.worship_icon_gray1);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_churchlist, parent, false);
        System.out.println("adaptor_favoritelist." + churchModelClasses.size());

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ChurchModelClass churchModelClass = churchModelClasses.get(position);
        holder.tv_churchName.setText(churchModelClass.getCname());
        holder.tv_churchAddress.setText(churchModelClass.getAddress());
        holder.tv_churchAddress.setText(churchModelClass.getAddress() + " " + churchModelClass.getCity() + " " + churchModelClass.getState() + " " + churchModelClass.getCountry() + " " + churchModelClass.getZipcode());

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


    }

    @Override
    public int getItemCount() {
        return churchModelClasses.size();
    }


    public void filterList(ArrayList<ChurchModelClass> filterdNames) {
        this.churchModelClasses = filterdNames;
        notifyDataSetChanged();
    }

}
