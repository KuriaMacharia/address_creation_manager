package com.center.anwaniportal;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends ArrayAdapter<Users> {
    private ArrayList<Users> listUser;
    public UserAdapter(Context context, List<Users> object){
        super(context,0, object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.user_item,parent,false);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.txt_officer_name_txt);
        TextView roleTxt = (TextView) convertView.findViewById(R.id.txt_position_txt);

        Users address = getItem(position);

        titleTextView.setText(address.getName());
        roleTxt.setText(address.getRole());

        return convertView;
    }

}
