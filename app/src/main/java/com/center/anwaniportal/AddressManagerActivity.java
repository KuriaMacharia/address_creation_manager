package com.center.anwaniportal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.center.anwaniportal.Helper.CheckNetWorkStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AddressManagerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SearchView.OnQueryTextListener, OnMapReadyCallback {

    private AddressListAdapter addressListAdapter;
    private AddressAdapter addressAdapter;
    ListView addressList;
    FirebaseFirestore db;
    WriteBatch batch;
    private GoogleMap mMap;

    private ArrayList<Address> listAddress, listDateAddress;
    private ArrayList<String> listRegion;
    private ArrayList<Address> listFilteredStreet;
    public static ArrayList<Address> listAllAddress= new ArrayList<Address>();
    ArrayList<String> listStreet;
    String countyId, regionId, County, Region, Street, dateType, startDate, endDate, longitude1, latitude1, locationAddress, la, lo,
            stlongitude1, stlatitude1, stlocationAddress, stla, stlo;
    Spinner regionSpin, streetSpin;
    TextView numberTxt, startTxt, endTxt, clearDateTxt, listTxt, mapTxt;
    ConstraintLayout mapConstraint;

    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar c;
    private Context ctx = this;
    Date dateStart, dateEnd, dateObj;
    SimpleDateFormat dateS;
    String newDateStr, newDateEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);
        db = FirebaseFirestore.getInstance();
        batch = db.batch();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listAddress = new ArrayList<Address>();
        listDateAddress = new ArrayList<Address>();
        listAllAddress= new ArrayList<Address>();
        listFilteredStreet= new ArrayList<Address>();
        listStreet = new ArrayList<String>();
        listRegion = new ArrayList<String>();

        mYear= Calendar.getInstance().get(Calendar.YEAR);
        mMonth=Calendar.getInstance().get(Calendar.MONTH)+1;
        mDay=Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ;

        addressList = (ListView) findViewById(R.id.list_address);
        mapTxt=(TextView) findViewById(R.id.txt_address_map) ;
        listTxt= (TextView) findViewById(R.id.txt_address_list);
        mapConstraint= (ConstraintLayout) findViewById(R.id.cons_map_address);


        regionSpin=(Spinner) findViewById(R.id.spin_region_address);
        streetSpin=(Spinner) findViewById(R.id.spin_street_address);
        numberTxt=(TextView) findViewById(R.id.txt_address_number);
        startTxt=(TextView) findViewById(R.id.txt_start_date);
        endTxt=(TextView) findViewById(R.id.txt_end_date);
        clearDateTxt=(TextView) findViewById(R.id.txt_clear_date_filter);

        Bundle loca=getIntent().getExtras();
        if (loca != null) {
            County=String.valueOf(loca.getCharSequence("county"));
            Region=String.valueOf(loca.getCharSequence("region"));
            countyId=String.valueOf(loca.getCharSequence("countyid"));
        }

        ImageView homeImg=(ImageView) findViewById(R.id.img_home);
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressManagerActivity.this, MainActivity.class));
            }
        });

        startTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateType="Start";
                    show_Datepicker();
            }
        });

        endTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateType="End";
                show_Datepicker();
            }
        });

        clearDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listDateAddress.size()>0){
                    listDateAddress.clear();
                }
                LoadAddresses();
                endTxt.setText("End Date");
                startTxt.setText("Start Date");

                clearDateTxt.setVisibility(View.GONE);
            }
        });

        mapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapTxt.setBackground(getResources().getDrawable(R.drawable.custom_clear_button_click));
                listTxt.setBackground(getResources().getDrawable(R.drawable.custom_nutton_click));
                mapTxt.setTextColor(getResources().getColor(R.color.tabsBar));
                listTxt.setTextColor(getResources().getColor(R.color.White));

                addressList.setVisibility(View.GONE);
                mapConstraint.setVisibility(View.VISIBLE);
            }
        });

        listTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listTxt.setBackground(getResources().getDrawable(R.drawable.custom_clear_button_click));
                mapTxt.setBackground(getResources().getDrawable(R.drawable.custom_nutton_click));
                mapTxt.setTextColor(getResources().getColor(R.color.White));
                listTxt.setTextColor(getResources().getColor(R.color.tabsBar));

                addressList.setVisibility(View.VISIBLE);
                mapConstraint.setVisibility(View.GONE);
            }
        });

        LoadAddresses();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-1.289238, 36.820442);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void SetAddressMarkers(){
        if (listAddress.size()>0){

            for (int i = 0; i < listAddress.size(); i++) {

                longitude1 = listAddress.get(i).getLongitude();
                latitude1 = listAddress.get(i).getLatitude();
                locationAddress = listAddress.get(i).getFulladdress();

                Double long1 = Double.parseDouble(longitude1);
                Double lat1 = (Double.parseDouble(latitude1) - 100);

                la = listAddress.get(0).getLatitude();
                lo = listAddress.get(0).getLongitude();

                Double lat2 = (Double.parseDouble(la) - 100);
                Double long2 = Double.parseDouble(lo);

                LatLng building = new LatLng(lat1, long1);
                mMap.addMarker(new MarkerOptions().position(building).title(locationAddress));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(building));
                Log.e("PlaceLL", lat1 + " " + long1);

                LatLng start = new LatLng(lat2, long2);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
            }
        }
    }

    private void LoadAddresses(){
        if(listAddress.size()>0) {
            listAddress.clear();
        }

        db.collection("address").whereEqualTo("county", County)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            Address miss = document.toObject(Address.class);
                            listAddress.add(miss);
                            listAllAddress.add(miss);

                            for (int i = 0; i < listAddress.size(); i++) {
                                listRegion.add(listAddress.get(i).getRegion());
                            }

                            Set<String> set1 = new HashSet<>(listRegion);
                            listRegion.clear();
                            listRegion.addAll(set1);
                            numberTxt.setText(String.valueOf(listAddress.size()));

                            addressListAdapter = new AddressListAdapter(AddressManagerActivity.this);
                            addressList.setAdapter(addressListAdapter);

                            LoadSpinners();

                            SetAddressMarkers();
                        }
                    } else {
                        Toast.makeText(AddressManagerActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private void LoadSpinners(){

        ArrayAdapter scsc = new ArrayAdapter(AddressManagerActivity.this, android.R.layout.simple_spinner_item, listRegion);
        scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpin.setAdapter(scsc);

        regionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Region = String.valueOf(regionSpin.getSelectedItem());
                addressListAdapter.filter(Region);
                numberTxt.setText(String.valueOf(listAllAddress.size()));

                if (listStreet.size()>0){
                    listStreet.clear();
                    listFilteredStreet.clear();
                }

                for (int a = 0; a < listAddress.size(); a++) {
                    if(listAddress.get(a).getRegion().contentEquals(Region)){
                        listFilteredStreet.add(listAddress.get(a));
                    }
                }


                for (int j = 0; j < listFilteredStreet.size(); j++) {
                    listStreet.add(listFilteredStreet.get(j).getRoad());
                }

                Set<String> set = new HashSet<>(listStreet);
                listStreet.clear();
                listStreet.addAll(set);

                ArrayAdapter scs = new ArrayAdapter(AddressManagerActivity.this, android.R.layout.simple_spinner_item, listStreet);
                scs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                streetSpin.setAdapter(scs);

                streetSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Street = String.valueOf(streetSpin.getSelectedItem());
                        addressListAdapter.filter(Street);
                        numberTxt.setText(String.valueOf(listAllAddress.size()));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Region = String.valueOf(regionSpin.getSelectedItem());
        listAddress.clear();
        LoadAddresses();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String text = s;
        addressListAdapter.filter(text);
        return false;
    }

    private void show_Datepicker() {
        c = Calendar.getInstance();
        int mYearParam = mYear;
        int mMonthParam = mMonth-1;
        int mDayParam = mDay;

        DatePickerDialog datePickerDialog = new DatePickerDialog(ctx,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mMonth = monthOfYear + 1;
                        mYear=year;
                        mDay=dayOfMonth;

                        dateS=new SimpleDateFormat(("yyyy-MM-dd"));

                        if (dateType.contentEquals("Start")){
                            startTxt.setText(String.valueOf(mDay) + "/ " + String.valueOf(mMonth) + "/ "+String.valueOf(mYear) );
                            startDate=String.valueOf(mYear) + "-" + String.valueOf(mMonth) + "-"+String.valueOf(mDay);

                        }else if(dateType.contentEquals("End")&& !startTxt.getText().toString().contentEquals("Start Date")){
                            endDate=String.valueOf(mYear) + "-" + String.valueOf(mMonth) + "-"+String.valueOf(mDay);

                            try {
                                dateStart = dateS.parse(startDate);
                                newDateStr = dateS.format(dateS.parse(startDate));

                                dateEnd = dateS.parse(endDate);
                                newDateEnd = dateS.format(dateS.parse(endDate));

                                if (dateStart.before(dateEnd)){
                                    endTxt.setText(String.valueOf(mDay) + "/ " + String.valueOf(mMonth) + "/ "+String.valueOf(mYear) );
                                    LoadDateAddresses();
                                }else{
                                    Toast.makeText(AddressManagerActivity.this,
                                            "End Date must be after Start Date", Toast.LENGTH_LONG).show();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }else{
                            Toast.makeText(AddressManagerActivity.this,
                                    "Must select Start Date", Toast.LENGTH_LONG).show();
                        }
                    }
                }, mYearParam, mMonthParam, mDayParam);

        datePickerDialog.show();
    }

    private void LoadDateAddresses(){
        if(listAddress.size()>0) {
            listAddress.clear();
            listDateAddress.clear();
            listAllAddress.clear();
        }
        clearDateTxt.setVisibility(View.VISIBLE);

            db.collection("address").whereEqualTo("county", County)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            Address miss = document.toObject(Address.class);
                            //listAddress.add(miss);
                            listDateAddress.add(miss);

                            listAllAddress.add(miss);
                            //endTxt.setText(newDateStr);

                            for (int i = 0; i < listDateAddress.size(); i++) {
                                if (listDateAddress.get(i).getTimecreated().after(dateStart)&&
                                        listDateAddress.get(i).getTimecreated().before(dateEnd)||
                                            listDateAddress.get(i).getTimecreated().equals(dateEnd))
                                listAddress.add(listDateAddress.get(i));
                            }

                            for (int i = 0; i < listAddress.size(); i++) {
                                listRegion.add(listAddress.get(i).getRegion());
                            }

                            Set<String> set1 = new HashSet<>(listRegion);
                            listRegion.clear();
                            listRegion.addAll(set1);
                            numberTxt.setText(String.valueOf(listAddress.size()));

                            addressListAdapter = new AddressListAdapter(AddressManagerActivity.this);
                            addressList.setAdapter(addressListAdapter);

                            LoadSpinners();
                            SetAddressMarkers();
                        }
                    } else {
                        Toast.makeText(AddressManagerActivity.this, "Error fecthing document", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
}
