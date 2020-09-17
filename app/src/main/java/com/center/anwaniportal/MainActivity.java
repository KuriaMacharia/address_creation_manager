package com.center.anwaniportal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";
    public static final String role1 = "roleKey";
    public static final String status1 = "statusKey";

    String countyId, regionId, County, Region, Street;

    TextView addressTxt, streetTxt, regionsTxt;
    ConstraintLayout addressCons, userCons, streetMapCons, accountCons, workCons, gpsCons;
    ImageView keyImg;
    private FirebaseFirestore db;
    private ArrayList<Address> listAddress;
    ArrayList<String> listStreet;
    ArrayList<Users> listUser;
    private ArrayList<Region> listRegion;

    String Phone, Email, Name, Supervisor, myCounty, myRegion, myRole, myStatus, myId;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyImg=(ImageView) findViewById(R.id.img_key);

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        Name = sharedpreferences.getString(name1,"");
        Supervisor = sharedpreferences.getString(supervisor1,"");

        myCounty = sharedpreferences.getString(county1,"");
        myRegion = sharedpreferences.getString(region1,"");
        myRole = sharedpreferences.getString(role1,"");
        myStatus = sharedpreferences.getString(status1,"");

        if (myCounty.contentEquals("All")){
            myCounty= "Kiambu County";
            keyImg.setVisibility(View.VISIBLE);
        }

        db = FirebaseFirestore.getInstance();
        GetCountyId();

        pDialog = new ProgressDialog(MainActivity.this, R.style.mydialog);
        pDialog.setMessage("Loading Profile. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_more_vert_white_24dp);
        toolbar.setOverflowIcon(drawable);

        listAddress = new ArrayList<Address>();
        listStreet = new ArrayList<String>();
        listUser = new ArrayList<Users>();

        addressTxt=(TextView) findViewById(R.id.txt_addresses_count);
        streetTxt =(TextView) findViewById(R.id.txt_street_count);
        regionsTxt=(TextView) findViewById(R.id.txt_regions_count);
        addressCons=(ConstraintLayout) findViewById(R.id.cons_address_manager);
        userCons=(ConstraintLayout) findViewById(R.id.cons_user_manager);
        streetMapCons=(ConstraintLayout) findViewById(R.id.cons_street_map);
        accountCons=(ConstraintLayout) findViewById(R.id.cons_account);
        workCons=(ConstraintLayout) findViewById(R.id.cons_work_manager);
        gpsCons=(ConstraintLayout) findViewById(R.id.cons_gps);

        db.collection("address").whereEqualTo("verificationstatus", "Verified")
                .whereEqualTo("activestatus", "Active")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                regionId=document.getId();
                                Address miss = document.toObject(Address.class);
                                listAddress.add(miss);

                                for (int i = 0; i < listAddress.size(); i++) {
                                    listStreet.add(listAddress.get(i).getRoad());
                                }

                                Set<String> set = new HashSet<>(listStreet);
                                listStreet.clear();
                                listStreet.addAll(set);

                                addressTxt.setText(String.valueOf(listAddress.size()));
                                streetTxt.setText(String.valueOf(listStreet.size()));

                            }
                        }
                    }
                });

        db.collection("users")
                .whereEqualTo("status", "Active")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Users miss;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                miss=document.toObject(Users.class);
                                listUser.add(miss);

                                regionsTxt.setText(String.valueOf(listUser.size()));
                            }
                        } else {
                        }
                    }
                });

        addressCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddressManagerActivity.class);
                Bundle location = new Bundle();
                location.putString("county", myCounty);
                location.putString("countyid", countyId);
                location.putString("region", Region);

                i.putExtras(location);
                startActivity(i);
            }
        });

        streetMapCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, StreetManagerActivity.class);
                Bundle location = new Bundle();
                location.putString("county", myCounty);
                location.putString("countyid", countyId);
                location.putString("region", Region);

                i.putExtras(location);
                startActivity(i);
            }
        });

        accountCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
            }
        });

        userCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
            }
        });

        workCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WorkActivity.class));
            }
        });

        gpsCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AnalyticActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_logout) {

            //startActivity(new Intent(HomeActivity.this, MainActivity.class));

            sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void GetCountyId(){
        db.collection("counties").whereEqualTo("county", myCounty)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                countyId = document.getId();
                                LoadRegion();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Get Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void LoadRegion() {

        db.collection("counties").document(countyId).collection("regions")
                //.whereEqualTo("county", County)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        listRegion = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Region miss = document.toObject(Region.class);
                                listRegion.add(miss);
                            }
                            ArrayList<String> list = new ArrayList<String>();
                            for (int i = 0; i < listRegion.size(); i++) {
                                list.add(listRegion.get(i).getRegion());
                            }

                            Set<String> set = new HashSet<>(list);
                            list.clear();
                            list.addAll(set);

                            Region = list.get(0);
                            pDialog.dismiss();

                        } else {
                            Toast.makeText(MainActivity.this, "Get Failed", Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }
                    }
                });
    }

}
