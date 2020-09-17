package com.center.anwaniportal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class UserListAdapter extends ArrayAdapter<Users> {
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<Users> listAllUs;
    public UserListAdapter(Context context ){
        super(context, 0);
        mContext=context;
        inflater = LayoutInflater.from(mContext);
        this.listAllUs=new ArrayList<Users>();
        this.listAllUs.addAll(UsersActivity.listAllUsers);

    }

    public class ViewHolder {
        TextView name;
        TextView latTxt;
        TextView lonTxt;
    }

    @Override
    public int getCount() {
        return UsersActivity.listAllUsers.size();
    }

    @Override
    public Users getItem(int position) {
        return UsersActivity.listAllUsers.get(position);
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
        holder.name.setText(UsersActivity.listAllUsers.get(position).getName());
        holder.latTxt.setText(UsersActivity.listAllUsers.get(position).getPhone());
        holder.lonTxt.setText(UsersActivity.listAllUsers.get(position).getEmail());

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        UsersActivity.listAllUsers.clear();
        if (charText.length() == 0) {
            UsersActivity.listAllUsers.addAll(listAllUs);
        } else {
            for (Users wp : listAllUs) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    UsersActivity.listAllUsers.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}

