package hnweb.com.findchurches.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.model.ChurchModelClass;

/**
 * Created by PC-21 on 08-May-18.
 */

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.church_info, null);

        TextView tv_churchname = view.findViewById(R.id.tv_churchname);
        TextView tv_address = view.findViewById(R.id.tv_address);
        ImageView iv_church = view.findViewById(R.id.iv_church);


        ChurchModelClass infoWindowData = (ChurchModelClass) marker.getTag();

        int imageId = context.getResources().getIdentifier(infoWindowData.getCimg().toLowerCase(),
                "drawable", context.getPackageName());
        iv_church.setImageResource(imageId);

        tv_churchname.setText(infoWindowData.getCname());
        tv_address.setText(infoWindowData.getAddress() + " " + infoWindowData.getCity() + " " + infoWindowData.getState() + " " + infoWindowData.getCountry() + " " + infoWindowData.getZipcode());


        return view;
    }
}

