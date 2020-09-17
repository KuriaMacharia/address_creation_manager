package com.center.anwaniportal;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends ArrayAdapter<Address> {
    private ArrayList<Address> listAddress;
    public AddressAdapter(Context context, List<Address> object){
        super(context,0, object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.address_item,parent,false);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.txt_address_number);
        TextView latitudeTxt = (TextView) convertView.findViewById(R.id.txt_latitude_item);
        TextView longitudeTxt = (TextView) convertView.findViewById(R.id.txt_longitude_item);

        Address address = getItem(position);

        titleTextView.setText(address.getFulladdress());
        latitudeTxt.setText(address.getLatitude());
        longitudeTxt.setText(address.getLongitude());

        return convertView;
    }

}

