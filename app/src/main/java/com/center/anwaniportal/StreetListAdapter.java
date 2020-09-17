package com.center.anwaniportal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class StreetListAdapter extends ArrayAdapter<Street> {
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<Street> listAll;
    public StreetListAdapter(Context context ){
        super(context, 0);
        mContext=context;
        inflater = LayoutInflater.from(mContext);
        this.listAll=new ArrayList<Street>();
        this.listAll.addAll(StreetManagerActivity.listAllAddress);

    }

    public class ViewHolder {
        TextView name;
        TextView latTxt;
        TextView lonTxt;
    }

    @Override
    public int getCount() {
        return StreetManagerActivity.listAllAddress.size();
    }

    @Override
    public Street getItem(int position) {
        return StreetManagerActivity.listAllAddress.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.address_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) convertView.findViewById(R.id.txt_address_number);
            holder.latTxt=(TextView) convertView.findViewById(R.id.txt_latitude_item);
            holder.lonTxt= (TextView) convertView.findViewById(R.id.txt_longitude_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(StreetManagerActivity.listAllAddress.get(position).getRoad());
        holder.latTxt.setText(StreetManagerActivity.listAllAddress.get(position).getEntrylatitude());
        holder.lonTxt.setText(StreetManagerActivity.listAllAddress.get(position).getEntrylongitude());

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        StreetManagerActivity.listAllAddress.clear();
        if (charText.length() == 0) {
            StreetManagerActivity.listAllAddress.addAll(listAll);
        } else {
            for (Street wp : listAll) {
                if (wp.getRoad().toLowerCase(Locale.getDefault()).contains(charText)) {
                    StreetManagerActivity.listAllAddress.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}

