package com.center.anwaniportal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {
    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String myid1 = "myidKey";
    public static final String Email1 = "emailKey";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";
    public static final String role1 = "roleKey";
    public static final String status1 = "statusKey";

    TextView nameStartTxt, nameTxt, emailEdt, roleTxt, supervisorTxt, countyTxt, regionTxt, statusTxt;
    EditText phoneEdt;
    Button saveBtn;

    String Phone, Email, Name, Supervisor, myCounty, myRegion, myRole, myStatus, myId;
    SharedPreferences sharedpreferences;
    FirebaseFirestore db;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
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
        myId=sharedpreferences.getString(myid1,"");

        nameStartTxt=(TextView) findViewById(R.id.txt_start_name_account);
        nameTxt=(TextView) findViewById(R.id.txt_officer_name_account);
        roleTxt=(TextView) findViewById(R.id.txt_role_account);
        supervisorTxt=(TextView) findViewById(R.id.txt_supervisor_account);
        countyTxt=(TextView) findViewById(R.id.txt_county_account);
        regionTxt=(TextView) findViewById(R.id.txt_region_account);
        statusTxt=(TextView) findViewById(R.id.txt_status_account);
        emailEdt=(TextView) findViewById(R.id.edt_email_account);
        phoneEdt=(EditText) findViewById(R.id.edt_phone_account);
        saveBtn=(Button) findViewById(R.id.btn_save_account);

        nameTxt.setText(Name);
        roleTxt.setText(myRole);
        supervisorTxt.setText(Supervisor);
        countyTxt.setText(myCounty);
        regionTxt.setText(myRegion);
        statusTxt.setText(myStatus);
        emailEdt.setText(Email);
        phoneEdt.setText(Phone);

        String nameInitial=Name.substring(0,1);
        nameStartTxt.setText(nameInitial);

        ImageView homeImg=(ImageView) findViewById(R.id.img_home);
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this, MainActivity.class));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(AccountActivity.this, R.style.mydialog);
                pDialog.setMessage("Loading Profile. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();


                Map<String, Object> user = new HashMap<>();
                    user.put("phone", phoneEdt.getText().toString());
                    user.put("email", emailEdt.getText().toString());

                db.collection("users").document(myId)
                        .set(user, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AccountActivity.this,"Account Updated Successfully",Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pDialog.dismiss();
                                Toast.makeText(AccountActivity.this,"Account Update Failed",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
