package com.jason9075.womanyhackathonteacher.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.jason9075.womanyhackathonteacher.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jason9075 on 2016/12/4.
 */

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private String name;
    private Date date;

    public MyInfoWindowAdapter(Context context, String name, Date date) {
        this.context = context;
        this.name = name;
        this.date = (date==null)? new Date() : date;    //cloud user沒資料會是null
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_marker_view, null);
        LinearLayout backGroundLinearLayout = (LinearLayout) view.findViewById(R.id.item_map_background_linearlayout);
        TextView nameTextView = (TextView) view.findViewById(R.id.item_map_name_textview);
        TextView dateTextView = (TextView) view.findViewById(R.id.item_map_date_textview);

        nameTextView.setText(name);
        dateTextView.setText(new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault()).format(date));

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


}
