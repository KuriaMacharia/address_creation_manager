package com.center.anwaniportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkActivity extends AppCompatActivity {
    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";
    public static final String role1 = "roleKey";
    public static final String status1 = "statusKey";

    TextView stationTxt, addStationTxt, addCountyTxt, addRegionTxt;
    Spinner countySpin, countyRegionSpin;
    ListView regionList;
    EditText countyEdt, regionEdt;
    ConstraintLayout stationCons, addStationCons;
    String Phone, Email, Name, Supervisor, myCounty, myRegion, myRole, myStatus, myId, County, stationId, theRegion, regionStatus;

    private FirebaseFirestore db;
    private ArrayList<Station> listRegions;
    private ArrayList<Station> listStations;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> listCounty;
    StationAdapter stationAdapter, stationAdapterStart;
    ProgressDialog FstartDialog;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        db = FirebaseFirestore.getInstance();

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        Name = sharedpreferences.getString(name1,"");
        Supervisor = sharedpreferences.getString(supervisor1,"");

        myCounty = sharedpreferences.getString(county1,"");
        myRegion = sharedpreferences.getString(region1,"");
        myRole = sharedpreferences.getString(role1,"");
        myStatus = sharedpreferences.getString(status1,"");

        stationTxt=(TextView) findViewById(R.id.txt_station_station);
        addStationTxt=(TextView) findViewById(R.id.txt_add_station_station);
        countySpin=(Spinner) findViewById(R.id.spin_county_station);
        regionList=(ListView) findViewById(R.id.list_regions_stations);

        addCountyTxt=(TextView) findViewById(R.id.txt_add_county_station);
        addRegionTxt=(TextView) findViewById(R.id.txt_add_region_station);
        countyRegionSpin=(Spinner) findViewById(R.id.spin_county_add_station);
        countyEdt=(EditText) findViewById(R.id.edt_county_add_station);
        regionEdt=(EditText) findViewById(R.id.edt_region_add_station);
        stationCons=(ConstraintLayout) findViewById(R.id.cons_regions_list_station);
        addStationCons=(ConstraintLayout) findViewById(R.id.cons_add_station);

        listCounty = new ArrayList<>();
        listRegions = new ArrayList<Station>();
        listStations = new ArrayList<Station>();

        FetchRegions();

        ImageView homeImg=(ImageView) findViewById(R.id.img_home);
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WorkActivity.this, MainActivity.class));
            }
        });

        stationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stationCons.setVisibility(View.VISIBLE);
                addStationCons.setVisibility(View.GONE);
            }
        });

        addStationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stationCons.setVisibility(View.GONE);
                addStationCons.setVisibility(View.VISIBLE);
            }
        });
    }

    private void FetchRegions(){

        db.collection("checker")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Station miss = document.toObject(Station.class);
                        listRegions.add(miss);

                        for (int i = 0; i < listRegions.size(); i++) {
                            listCounty.add(listRegions.get(i).getCounty());
                        }

                        Set<String> set = new HashSet<>(listCounty);
                        listCounty.clear();
                        listCounty.addAll(set);
                        listCounty.add(0, "---Select---");
                        County=listCounty.get(0);

                        stationAdapterStart = new StationAdapter(WorkActivity.this, listRegions);
                        regionList.setAdapter(stationAdapterStart);

                        ArrayAdapter scsc = new ArrayAdapter(WorkActivity.this, android.R.layout.simple_spinner_item, listCounty);
                        scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        countySpin.setAdapter(scsc);

                        countySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                County = String.valueOf(countySpin.getSelectedItem());

                                db.collection("checker").whereEqualTo("county", County)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for(QueryDocumentSnapshot document : task.getResult()) {
                                                Station miss = document.toObject(Station.class);
                                                listStations.add(miss);

                                                stationAdapter = new StationAdapter(WorkActivity.this, listStations);
                                                regionList.setAdapter(stationAdapter);

                                                regionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                        theRegion=((TextView) view.findViewById(R.id.txt_region_name_item)).getText().toString();
                                                        final String theST=((TextView) view.findViewById(R.id.txt_start_time_region_item)).getText().toString();
                                                        final String theET=((TextView) view.findViewById(R.id.txt_end_time_region_item)).getText().toString();
                                                        final String theRadius=((TextView) view.findViewById(R.id.txt_radius_region_item)).getText().toString();
                                                        String theCounty=((TextView) view.findViewById(R.id.txt_county_region_item)).getText().toString();
                                                        regionStatus=((TextView) view.findViewById(R.id.txt_region_status_region_item)).getText().toString();

                                                        stationId="";
                                                        GetStationId();

                                                        //if (regionStatus.contentEquals("Ongoing")) {

                                                            LayoutInflater inflater = LayoutInflater.from(WorkActivity.this);
                                                            final View dialogView = inflater.inflate(R.layout.work_station_dialog, null);
                                                            final AlertDialog dialogBuilder = new AlertDialog.Builder(WorkActivity.this).create();
                                                            dialogBuilder.setView(dialogView);

                                                            TextView countyTxt = (TextView) dialogView.findViewById(R.id.txt_county_name_dialog);
                                                            final TextView regionTxt = (TextView) dialogView.findViewById(R.id.txt_region_name_dialog);
                                                            final EditText radiusEdt = (EditText) dialogView.findViewById(R.id.edt_radius_dialog);
                                                            final EditText startTimeEdt = (EditText) dialogView.findViewById(R.id.edt_start_time_dialog);
                                                            final EditText endTimeEdt = (EditText) dialogView.findViewById(R.id.edt_end_time_dialog);
                                                            final Switch statusSwitch = (Switch) dialogView.findViewById(R.id.switch_region_dialog);
                                                            saveBtn=(Button) dialogView.findViewById(R.id.btn_save_dialog);
                                                            saveBtn.setVisibility(View.GONE);
                                                            Button cancelBtn=(Button) dialogView.findViewById(R.id.btn_cancel_dialog);

                                                            countyTxt.setText(theCounty);
                                                            regionTxt.setText(theRegion);
                                                            radiusEdt.setText(theRadius);
                                                            startTimeEdt.setText(theST);
                                                            endTimeEdt.setText(theET);
                                                            if(regionStatus.contentEquals("Ongoing")){
                                                                statusSwitch.setText("Ongoing");
                                                                statusSwitch.setChecked(true);
                                                            }else{
                                                                statusSwitch.setText("Complete");
                                                                statusSwitch.setChecked(true);
                                                            }

                                                            /*if (!stationId.isEmpty()){
                                                                saveBtn.setVisibility(View.VISIBLE);
                                                            }*/

                                                            statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                                @Override
                                                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                                    if (b){
                                                                        statusSwitch.setText("Ongoing");
                                                                        regionStatus="Ongoing";
                                                                    }else{
                                                                        statusSwitch.setText("Complete");
                                                                        regionStatus="Complete";
                                                                    }

                                                                }
                                                            });

                                                            saveBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {

                                                                    FstartDialog = new ProgressDialog(WorkActivity.this, R.style.mydialog);
                                                                    FstartDialog.setMessage("Updating. Please wait...");
                                                                    FstartDialog.setIndeterminate(false);
                                                                    FstartDialog.setCancelable(false);
                                                                    FstartDialog.show();

                                                                    Date currentDate = new Date();
                                                                    Map<String, Object> thestation = new HashMap<>();
                                                                    thestation.put("radius", radiusEdt.getText().toString());
                                                                    thestation.put("starttime", startTimeEdt.getText().toString());
                                                                    thestation.put("endtime", endTimeEdt.getText().toString());
                                                                    thestation.put("regionstatus", regionStatus);


                                                                    db.collection("checker").document(stationId)
                                                                            .set(thestation, SetOptions.merge())
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    UpdateList();
                                                                                    Toast.makeText(WorkActivity.this,"Station Updated Successfully",Toast.LENGTH_LONG).show();
                                                                                    FstartDialog.dismiss();
                                                                                    dialogBuilder.dismiss();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Toast.makeText(WorkActivity.this,"Station Update Failed",Toast.LENGTH_LONG).show();
                                                                                    FstartDialog.dismiss();
                                                                                    //dialogBuilder.dismiss();
                                                                                }
                                                                            });

                                                                }
                                                            });

                                                            cancelBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    dialogBuilder.dismiss();
                                                                }
                                                            });

                                                            dialogBuilder.show();
                                                        //}
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
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

    private void GetStationId(){
        db.collection("checker").whereEqualTo("county", County).whereEqualTo("region", theRegion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               stationId = document.getId();
                               saveBtn.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(WorkActivity.this, "Get Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void UpdateList(){
        listStations.clear();

        db.collection("checker").whereEqualTo("county", County)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Station miss = document.toObject(Station.class);
                        listStations.add(miss);

                        stationAdapter = new StationAdapter(WorkActivity.this, listStations);
                        regionList.setAdapter(stationAdapter);
                        }
                    }}
                });

    }
}
