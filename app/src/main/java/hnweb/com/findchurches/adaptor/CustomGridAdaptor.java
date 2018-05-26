package hnweb.com.findchurches.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import hnweb.com.findchurches.R;

public class CustomGridAdaptor extends BaseAdapter {
    Context context;
    String logos[];
    LayoutInflater inflter;
    TextView tv_cost;

    public CustomGridAdaptor(Context applicationContext, String[] logos) {
        this.context = applicationContext;
        this.logos = logos;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return logos.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.adaptor_donation_costlist, null); // inflate the layout
        TextView textView = (TextView) view.findViewById(R.id.tv_cost);
        textView.setText(logos[i]); // set logo images
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }
}
