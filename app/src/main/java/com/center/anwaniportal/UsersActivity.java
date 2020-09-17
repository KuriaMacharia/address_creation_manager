package com.center.anwaniportal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UsersActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener{

    public static final String name1 = "nameKey";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String myid1 = "myidKey";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";
    public static final String role1 = "roleKey";
    public static final String status1 = "statusKey";

    String[] county = {"---Select---", "Nairobi City County", "Kiambu County", "Mombasa County", "Kisumu County", "Eldoret County",
                        "Nakuru County"};
    String[] theRole = {"---Select---", "Admin", "County Manager", "Regional Manager", "Data Verification Agent", "Street Data Agent",
                "Data Collection Agent"};

    private UserListAdapter userListAdapter;
    private UserAdapter userAdapter;

    TextView teamSizeTxt, selectedFilterTxt, addTxt, usersTxt;
    Spinner countySpin, regionSpin, roleSpin, supervisorSpin, countyAddSpin, regionAddSpin, roleAddSpin;
    ListView usersList;
    ConstraintLayout usersCons, addCons;
    EditText nameEdt, phoneEdt, emailEdt;
    Button saveBtn;

    String Phone, Email, Name, Supervisor, myCounty, myRegion, myRole, myStatus, myId, lisCounty, lisRegion, lisRole, lisSupervisor,
            oSupervisor, selectedCounty, selectedCountyId,selectedRegion, selectedRole, Role;
    SharedPreferences sharedpreferences;
    FirebaseFirestore db;

    ArrayList<Users> listUser, listUserRegion, listUserSupervisor;
    private ArrayList<String> listRegion, listSelectRegion;
    private ArrayList<String> listCounty;
    private ArrayList<String> listRole;
    private ArrayList<String> listSupervisor;
    public static ArrayList<Users> listAllUsers= new ArrayList<Users>();
    private ArrayList<Region> listAddRegion;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        oSupervisor = sharedpreferences.getString(name1,"");
        myId=sharedpreferences.getString(myid1,"");

        userListAdapter = new UserListAdapter(UsersActivity.this);

        nameEdt=(EditText) findViewById(R.id.edt_name_users);
        phoneEdt=(EditText) findViewById(R.id.edt_phone_users);
        emailEdt=(EditText) findViewById(R.id.edt_email_users);
        countyAddSpin=(Spinner) findViewById(R.id.spin_county_add);
        regionAddSpin=(Spinner) findViewById(R.id.spin_region_add);
        roleAddSpin=(Spinner) findViewById(R.id.spin_role_add);
        addCons=(ConstraintLayout) findViewById(R.id.cons_add_users);
        saveBtn=(Button) findViewById(R.id.btn_save_add);

        teamSizeTxt=(TextView) findViewById(R.id.txt_team_size_users);
        selectedFilterTxt=(TextView) findViewById(R.id.txt_selected_filter_user);
        addTxt=(TextView) findViewById(R.id.txt_add_user);
        usersTxt=(TextView) findViewById(R.id.txt_users_user);

        countySpin=(Spinner) findViewById(R.id.spin_county_user);
        regionSpin=(Spinner) findViewById(R.id.spin_region_user);
        roleSpin=(Spinner) findViewById(R.id.spin_role_user);
        supervisorSpin=(Spinner) findViewById(R.id.spin_supervisor_user);
        usersList=(ListView) findViewById(R.id.list_users_user);
        usersCons=(ConstraintLayout) findViewById(R.id.cons_users_users);

        listUser = new ArrayList<Users>();
        listAllUsers = new ArrayList<Users>();
        listUserRegion = new ArrayList<Users>();
        listUserSupervisor = new ArrayList<Users>();
        listRegion = new ArrayList<String>();
        listCounty = new ArrayList<String>();
        listRole = new ArrayList<String>();
        listSupervisor = new ArrayList<String>();
        listAddRegion = new ArrayList<Region>();
        listSelectRegion = new ArrayList<String>();

        countyAddSpin.setOnItemSelectedListener(this);
        roleAddSpin.setOnItemSelectedListener(this);
        //countySpin.setOnItemSelectedListener(this);

        ArrayAdapter cty = new ArrayAdapter(UsersActivity.this, android.R.layout.simple_spinner_item, county);
        cty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countyAddSpin.setAdapter(cty);
        countySpin.setAdapter(cty);

        ArrayAdapter rl = new ArrayAdapter(UsersActivity.this, android.R.layout.simple_spinner_item, theRole);
        rl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleAddSpin.setAdapter(rl);

        GetUsers();

        ImageView homeImg=(ImageView) findViewById(R.id.img_home);
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UsersActivity.this, MainActivity.class));
            }
        });

        addTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTxt.setBackground(getResources().getDrawable(R.drawable.custom_clear_button_click));
                usersTxt.setBackground(getResources().getDrawable(R.drawable.custom_nutton_click));
                addTxt.setTextColor(getResources().getColor(R.color.tabsBar));
                usersTxt.setTextColor(getResources().getColor(R.color.White));

                usersCons.setVisibility(View.GONE);
                addCons.setVisibility(View.VISIBLE);
            }
        });

        usersTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersTxt.setBackground(getResources().getDrawable(R.drawable.custom_clear_button_click));
                addTxt.setBackground(getResources().getDrawable(R.drawable.custom_nutton_click));
                addTxt.setTextColor(getResources().getColor(R.color.White));
                usersTxt.setTextColor(getResources().getColor(R.color.tabsBar));

                usersCons.setVisibility(View.VISIBLE);
                addCons.setVisibility(View.GONE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedCounty.isEmpty()&&!selectedRegion.isEmpty()&&!selectedRole.isEmpty()&&
                        !nameEdt.getText().toString().isEmpty()&&!phoneEdt.getText().toString().isEmpty()&&
                            !emailEdt.getText().toString().isEmpty()) {
                    AddUser();
                }else{
                    Toast.makeText(UsersActivity.this, "Add Missing Field!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void GetUsers(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Users miss;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                miss=document.toObject(Users.class);
                                listUser.add(miss);
                                listAllUsers.add(miss);
                                teamSizeTxt.setText(String.valueOf(listUser.size()));

                                userAdapter = new UserAdapter(UsersActivity.this, listUser);
                                usersList.setAdapter(userAdapter);

        //Role Spinner
                                ArrayAdapter spr = new ArrayAdapter(UsersActivity.this, android.R.layout.simple_spinner_item, theRole);
                                spr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                roleSpin.setAdapter(spr);
                                roleSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        lisRole = String.valueOf(roleSpin.getSelectedItem());
                                        teamSizeTxt.setText(String.valueOf(listUser.size()));
                                        selectedFilterTxt.setText(lisRole);

                                        listUser.clear();

                                        db.collection("users").whereEqualTo("role", lisRole)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Users miss;
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                miss = document.toObject(Users.class);
                                                                listUser.add(miss);
                                                                teamSizeTxt.setText(String.valueOf(listUser.size()));

                                                                userAdapter = new UserAdapter(UsersActivity.this, listUser);
                                                                usersList.setAdapter(userAdapter);
                                                            }
                                                        }else {
                                                        }
                                                    }
                                                });
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

         //County Spinner

                                ArrayList<String> listC = new ArrayList<String>();
                                for (int i = 0; i < listUser.size(); i++) {
                                    listC.add(listUser.get(i).getCounty());
                                }
                                Set<String> set = new HashSet<>(listC);
                                listC.clear();
                                listC.addAll(set);
                                //lisCounty = listC.get(0);

                                ArrayAdapter scsc = new ArrayAdapter(UsersActivity.this, android.R.layout.simple_spinner_item, listC);
                                scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                countySpin.setAdapter(scsc);

                                countySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                        lisCounty = String.valueOf(countySpin.getSelectedItem());
                                        teamSizeTxt.setText(String.valueOf(listUser.size()));
                                        selectedFilterTxt.setText(lisCounty);
                                        listUser.clear();

                                        db.collection("users").whereEqualTo("county", lisCounty)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Users miss;
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                miss = document.toObject(Users.class);
                                                                listUser.add(miss);
                                                                listAllUsers.add(miss);
                                                                teamSizeTxt.setText(String.valueOf(listUser.size()));

                                                                userAdapter = new UserAdapter(UsersActivity.this, listUser);
                                                                usersList.setAdapter(userAdapter);
                                                            }
                 //Supervisor Spin
                                                            for (int j = 0; j < listUser.size(); j++) {
                                                                listSupervisor.add(listUser.get(j).getSupervisor());
                                                            }

                                                            Set<String> set1 = new HashSet<>(listSupervisor);
                                                            listSupervisor.clear();
                                                            listSupervisor.addAll(set1);

                                                            ArrayAdapter spp = new ArrayAdapter(UsersActivity.this, android.R.layout.simple_spinner_item, listSupervisor);
                                                            spp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            supervisorSpin.setAdapter(spp);

                                                            supervisorSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                    lisSupervisor = String.valueOf(supervisorSpin.getSelectedItem());
                                                                    teamSizeTxt.setText(String.valueOf(listUser.size()));
                                                                    listUser.clear();

                                                                    db.collection("users").whereEqualTo("county", lisCounty)
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Users miss;
                                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                            miss = document.toObject(Users.class);
                                                                                            listUser.add(miss);
                                                                                            listAllUsers.add(miss);
                                                                                            teamSizeTxt.setText(String.valueOf(listUser.size()));

                                                                                            userAdapter = new UserAdapter(UsersActivity.this, listUser);
                                                                                            usersList.setAdapter(userAdapter);
                                                                                        }
                                                                                    }else {
                                                                                    }
                                                                                }
                                                                            });

                                                                }

                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                                }
                                                            });

                                                        }else {
                                                                }
                                                            }
                                                        });

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }

                        } else {
                        }
                    }
                });
    }

    private void AddUser(){

        mAuth.createUserWithEmailAndPassword(emailEdt.getText().toString(), phoneEdt.getText().toString())
                .addOnCompleteListener(UsersActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Date currentDate = new Date();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", nameEdt.getText().toString());
                            user.put("supervisor", oSupervisor);
                            user.put("supervisorid", myId);
                            user.put("county", selectedCounty);
                            user.put("region", selectedRegion);
                            user.put("role", Role);
                            user.put("email", emailEdt.getText().toString());
                            user.put("phone", phoneEdt.getText().toString());
                            user.put("status", "Active");
                            user.put("timecreated", currentDate);


                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            startActivity(new Intent(UsersActivity.this, UsersActivity.class));

                                            Toast.makeText(UsersActivity.this, "User Saved", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UsersActivity.this, "Error adding user", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }else{
                            Toast.makeText(UsersActivity.this, "Error registering user", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //selectedRegion = String.valueOf(regionAddSpin.getSelectedItem());
        selectedCounty= String.valueOf(countyAddSpin.getSelectedItem());
        selectedRole= String.valueOf(roleAddSpin.getSelectedItem());

        if (selectedCounty.contentEquals("---Select---")) {

            } else {

                db.collection("counties").whereEqualTo("county", selectedCounty)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        selectedCountyId = document.getId();
                                        LoadRegion();
                                    }
                                } else {
                                    Toast.makeText(UsersActivity.this, "Get Failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
        }

        if (selectedRole.contentEquals("---Select---")) {

        }else {
            Role=selectedRole;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void LoadRegion(){
        if(listAddRegion.size()>0){
            listAddRegion.clear();
        }

            db.collection("counties").document(selectedCountyId).collection("regions")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Region miss = document.toObject(Region.class);
                                    listAddRegion.add(miss);
                                }
                                ArrayList<String> list = new ArrayList<String>();
                                for (int i = 0; i < listAddRegion.size(); i++) {
                                    list.add(listAddRegion.get(i).getRegion());
                                }

                                Set<String> set = new HashSet<>(list);
                                list.clear();
                                list.addAll(set);
                                selectedRegion = list.get(0);

                                ArrayAdapter scsc = new ArrayAdapter(UsersActivity.this, android.R.layout.simple_spinner_item, list);
                                scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                regionAddSpin.setAdapter(scsc);

                                regionAddSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        selectedRegion = String.valueOf(regionAddSpin.getSelectedItem());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                    } else {
                        Toast.makeText(UsersActivity.this, "Fetch Region Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
