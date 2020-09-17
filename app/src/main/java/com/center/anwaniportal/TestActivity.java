package com.center.anwaniportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.center.anwaniportal.Helper.HttpJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    private static final String KEY_SUCCESS = "success";
    private static final String KEY_EMPLOYEE_NAME = "NAME";
    private static final String KEY_ADDRESS = "ADDRESS";

    private static final String BASE_URL = "http://ec2-18-130-59-68.eu-west-2.compute.amazonaws.com/app/";
    private static String STRING_EMPTY = "";

    EditText nameEdt, addressEdt;
    Button addBtn;

    private int success;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        nameEdt=(EditText) findViewById(R.id.edt_name_test);
        addressEdt=(EditText) findViewById(R.id.edt_address_test);
        addBtn=(Button) findViewById(R.id.btn_add_test);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddEmployee().execute();
            }
        });
    }

    private class AddEmployee extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TestActivity.this);
            pDialog.setMessage("Adding Business. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_EMPLOYEE_NAME, nameEdt.getText().toString());
            httpParams.put(KEY_ADDRESS, addressEdt.getText().toString());

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "androidtest.php", "POST", httpParams);
            if(success==1)
                try {
                    success = jsonObject.getInt(KEY_SUCCESS);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 0) {
                        Toast.makeText(TestActivity.this,
                                "Success",
                                Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(TestActivity.this,
                                "Some error occurred",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}
