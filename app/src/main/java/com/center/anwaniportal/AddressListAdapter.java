package com.center.anwaniportal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class AddressListAdapter extends ArrayAdapter<Address> {
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<Address> listAll;
    public AddressListAdapter(Context context ){
        super(context, 0);
        mContext=context;
        inflater = LayoutInflater.from(mContext);
        this.listAll=new ArrayList<Address>();
        this.listAll.addAll(AddressManagerActivity.listAllAddress);

    }

    public class ViewHolder {
        TextView name;
        TextView latTxt;
        TextView lonTxt;
    }

    @Override
    public int getCount() {
        return AddressManagerActivity.listAllAddress.size();
    }

    @Override
    public Address getItem(int position) {
        return AddressManagerActivity.listAllAddress.get(position);
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
        holder.name.setText(AddressManagerActivity.listAllAddress.get(position).getFulladdress());
        holder.latTxt.setText(AddressManagerActivity.listAllAddress.get(position).getLatitude());
        holder.lonTxt.setText(AddressManagerActivity.listAllAddress.get(position).getLongitude());

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        AddressManagerActivity.listAllAddress.clear();
        if (charText.length() == 0) {
            AddressManagerActivity.listAllAddress.addAll(listAll);
        } else {
            for (Address wp : listAll) {
                if (wp.getFulladdress().toLowerCase(Locale.getDefault()).contains(charText)) {
                    AddressManagerActivity.listAllAddress.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
