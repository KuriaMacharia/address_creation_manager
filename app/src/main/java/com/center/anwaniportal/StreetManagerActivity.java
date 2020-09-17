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

public class StreetManagerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SearchView.OnQueryTextListener, OnMapReadyCallback {

    private StreetListAdapter streetListAdapter;
    private AddressAdapter addressAdapter;
    ListView streetList;
    FirebaseFirestore db;
    private GoogleMap mMap;

    private ArrayList<Street> listAddress, listDateAddress;
    private ArrayList<String> listRegion;
    private ArrayList<Region> listAllRegion;
    private ArrayList<Street> listFilteredStreet;
    public static ArrayList<Street> listAllAddress= new ArrayList<Street>();
    ArrayList<Street> listStreet;
    String countyId, regionId, County, Region, Street, dateType, startDate, endDate, longitude1, latitude1, locationAddress, la, lo,
            stlongitude1, stlatitude1, stlocationAddress, stla, stlo;;
    Spinner regionSpin, streetSpin;
    TextView numberTxt, startTxt, endTxt, clearDateTxt, listTxt, mapTxt;
    ConstraintLayout mapConstraint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_manager);
        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listAddress = new ArrayList<Street>();
        listDateAddress = new ArrayList<Street>();
        listAllAddress= new ArrayList<Street>();
        listFilteredStreet= new ArrayList<Street>();
        listStreet = new ArrayList<Street>();
        listRegion = new ArrayList<String>();
        listAllRegion = new ArrayList<Region>();

        streetList = (ListView) findViewById(R.id.list_address);
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
                startActivity(new Intent(StreetManagerActivity.this, MainActivity.class));
            }
        });

        mapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapTxt.setBackground(getResources().getDrawable(R.drawable.custom_clear_button_click));
                listTxt.setBackground(getResources().getDrawable(R.drawable.custom_nutton_click));
                mapTxt.setTextColor(getResources().getColor(R.color.tabsBar));
                listTxt.setTextColor(getResources().getColor(R.color.White));

                streetList.setVisibility(View.GONE);
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

                streetList.setVisibility(View.VISIBLE);
                mapConstraint.setVisibility(View.GONE);
            }
        });

        LoadRegions();
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
            mMap.clear();

            for (int i = 0; i < listAddress.size(); i++) {

                longitude1 = listAddress.get(i).getEntrylongitude();
                latitude1 = listAddress.get(i).getEntrylatitude();
                locationAddress = listAddress.get(i).getRoad();

                Double long1 = Double.parseDouble(longitude1);
                Double lat1 = (Double.parseDouble(latitude1) - 100);

                la = listAddress.get(0).getEntrylatitude();
                lo = listAddress.get(0).getEntrylongitude();

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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Region = String.valueOf(regionSpin.getSelectedItem());
        listAddress.clear();
        LoadStreets();
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
        streetListAdapter.filter(text);
        return false;
    }

    private void LoadRegions() {
        db.collection("counties").document(countyId).collection("regions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Region miss = document.toObject(Region.class);
                                listAllRegion.add(miss);

                                for (int a = 0; a < listAllRegion.size(); a++) {
                                        listRegion.add(listAllRegion.get(a).getRegion());
                            }

                            Set<String> set1 = new HashSet<>(listRegion);
                            listRegion.clear();
                            listRegion.addAll(set1);

                            ArrayAdapter scsc = new ArrayAdapter(StreetManagerActivity.this, android.R.layout.simple_spinner_item, listRegion);
                            scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            regionSpin.setAdapter(scsc);

                                regionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        Region = String.valueOf(regionSpin.getSelectedItem());
                                        GetRegionId();
                                        if(listAllAddress.size()>0) {
                                            listAllAddress.clear();
                                            listAddress.clear();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                        }
                    }
                    }
                });
    }

    private void GetRegionId() {
        db.collection("counties").document(countyId).collection("regions")
                .whereEqualTo("region", Region)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                regionId=document.getId();
                                LoadStreets();
                            }
                        }
                    }
                });
    }

    private void LoadStreets(){

        db.collection("counties").document(countyId).collection("regions")
                .document(regionId).collection("roads")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Street miss = document.toObject(Street.class);
                                listAddress.add(miss);
                                listAllAddress.add(miss);
                                SetAddressMarkers();
                            }

                            numberTxt.setText(String.valueOf(listAddress.size()));
                            streetListAdapter = new StreetListAdapter(StreetManagerActivity.this);
                            streetList.setAdapter(streetListAdapter);
                            }
                    }
                });
    }
}
