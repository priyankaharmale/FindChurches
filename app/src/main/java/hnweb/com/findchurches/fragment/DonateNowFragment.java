package hnweb.com.findchurches.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.activity.HistoryActivity;

/**
 * Created by PC-21 on 07-May-18.
 */

public class DonateNowFragment extends Fragment {

    View rootView;
    TextView tv_history;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_donatenow, container, false);
        tv_history = (TextView) rootView.findViewById(R.id.tv_history);

        tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}

