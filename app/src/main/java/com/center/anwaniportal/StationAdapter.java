package com.center.anwaniportal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationAdapter extends ArrayAdapter<Station> {

    private Activity activity;
    private ArrayList<Station> data;
    private static LayoutInflater inflater = null;
    String incidenceNumber;

    public StationAdapter(Activity a, ArrayList<Station> d) {
        super(a, 0, d);
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getRegionstatus().contentEquals("Ongoing")) {
            return 0;
        } else {
            return 1;
        }
    }

    public int getCount() {
        return data.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        int type = getItemViewType(position);
        if (convertView == null)
            if (type == 0) {
                vi = inflater.inflate(R.layout.region_item, null);
            } else {
                vi = inflater.inflate(R.layout.region_item_closed, null);
            }

        if (type == 0) {

            TextView regionTxt = (TextView) vi.findViewById(R.id.txt_region_name_item);
            TextView startTimeTxt = (TextView) vi.findViewById(R.id.txt_start_time_region_item);
            TextView endTimeTxt = (TextView) vi.findViewById(R.id.txt_end_time_region_item);
            TextView radiusTxt = (TextView) vi.findViewById(R.id.txt_radius_region_item);
            TextView countyTxt = (TextView) vi.findViewById(R.id.txt_county_region_item);
            TextView statusTxt = (TextView) vi.findViewById(R.id.txt_region_status_region_item);

            Station station = getItem(position);

            regionTxt.setText(station.getRegion());
            startTimeTxt.setText(station.getStarttime());
            endTimeTxt.setText(station.getEndtime());
            radiusTxt.setText(station.getRadius());
            countyTxt.setText(station.getCounty());
            statusTxt.setText(station.getRegionstatus());

        } else {

            TextView regionTxt = (TextView) vi.findViewById(R.id.txt_region_name_item);
            TextView startTimeTxt = (TextView) vi.findViewById(R.id.txt_start_time_region_item);
            TextView endTimeTxt = (TextView) vi.findViewById(R.id.txt_end_time_region_item);
            TextView radiusTxt = (TextView) vi.findViewById(R.id.txt_radius_region_item);
            TextView countyTxt = (TextView) vi.findViewById(R.id.txt_county_region_item);
            TextView statusTxt = (TextView) vi.findViewById(R.id.txt_region_status_region_item);

            Station station = getItem(position);

            regionTxt.setText(station.getRegion());
            startTimeTxt.setText(station.getStarttime());
            endTimeTxt.setText(station.getEndtime());
            radiusTxt.setText(station.getRadius());
            countyTxt.setText(station.getCounty());
            statusTxt.setText(station.getRegionstatus());
        }
        return vi;
    }

}
