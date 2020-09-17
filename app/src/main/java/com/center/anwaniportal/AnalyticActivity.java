package com.center.anwaniportal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.center.anwaniportal.Helper.CheckNetWorkStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AnalyticActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Context context;
    LocationManager locationManager;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;

    ListView coordinatesList, distanceList;
    Button startBtn, stopBtn, resetBtn;
    TextView coordinatesTxt, distanceCountTxt, coordinatesCountTxt, longitudeTxt;
    String latCount, latListCount, difCount, difListCount, myLatitude, myLongitude;

    ArrayList<String> listCoordinates;
    ArrayList<Double> lisLat;
    ArrayList<Double> resultLat;
    ArrayList<Double> latitudeList;
    ArrayList<Double> lisLon;
    ArrayList<Double> resultLong;
    ArrayList<Double> longitudeList;
    List<Double> listAverageDistance;
    ArrayList<Double> listDistance;
    ArrayList<HashMap<String, String>> distanceListRe;
    Double diffMax, latitude, longitude;
    Double lat1, lat2, lon1, lon2, selLat, selLon, selDis, reLong2;
    int counter;
    ListAdapter adapter;
    private GoogleMap mMap;
    TextWatcher textwatcher;
    ArrayAdapter scs, ccs, ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();
        setUpGClient();
        distanceListRe = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        coordinatesList=(ListView) findViewById(R.id.list_coordinates);
        distanceList=(ListView) findViewById(R.id.list_distances);
        startBtn=(Button) findViewById(R.id.btn_start_gps);
        stopBtn=(Button) findViewById(R.id.btn_stop_gps);
        resetBtn=(Button) findViewById(R.id.btn_reset_gps);
        coordinatesTxt=(TextView) findViewById(R.id.txt_coordinates_gps);
        distanceCountTxt=(TextView) findViewById(R.id.txt_distance_count);
        coordinatesCountTxt=(TextView) findViewById(R.id.txt_coordinates_count);
        longitudeTxt=(TextView) findViewById(R.id.txt_longitude_gps);

        listCoordinates = new ArrayList<>();
        listDistance = new ArrayList<Double>();
        lisLat =new ArrayList<Double>();
        lisLon =new ArrayList<Double>();
        resultLat =new ArrayList<Double>();
        resultLong =new ArrayList<Double>();
        latitudeList =new ArrayList<Double>();
        longitudeList =new ArrayList<Double>();
        listAverageDistance =new ArrayList<>();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }
                getMyLocation();


            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinatesTxt.addTextChangedListener(textwatcher);
                distanceListRe.clear();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(AnalyticActivity.this, AnalyticActivity.class));
            }
        });

        ccs = new ArrayAdapter(AnalyticActivity.this, android.R.layout.simple_spinner_item, lisLon);
        ccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        scs = new ArrayAdapter(AnalyticActivity.this, android.R.layout.simple_spinner_item, lisLat);
        scs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coordinatesList.setAdapter(scs);

        coordinatesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selLat=lisLat.get(i);
                    selLon=lisLon.get(i);
                    selDis=listDistance.get(i-2);
                    SetMarker();
                }
            });

        ss = new ArrayAdapter(AnalyticActivity.this, android.R.layout.simple_spinner_item, listDistance);
        ss.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceList.setAdapter(ss);

        textwatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counter++;
                lisLat.add((mylocation.getLatitude()));
                lisLon.add(mylocation.getLongitude());

                int a=lisLat.size();

                if(lisLat.size()>2) {

                    lat1 = lisLat.get(a-1);
                    lon1 = lisLon.get(a-1);
                    lat2 = lisLat.get(a - 2);
                    lon2 = lisLon.get(a - 2);

                    Location loc1 = new Location("");
                    loc1.setLatitude(lat1);
                    loc1.setLongitude(lon1);

                    Location loc2 = new Location("");
                    loc2.setLatitude(lat2);
                    loc2.setLongitude(lon2);

                    float distanceInMeters = loc1.distanceTo(loc2);

//Add the coordinates storage list
                    HashMap<String, String> map = new HashMap<>();
                    map.put("latitude", String.valueOf(Double.valueOf(lat2)));
                    map.put("longitude", String.valueOf(Double.valueOf(lon2)));
                    map.put("distance", String.valueOf(distanceInMeters));
                    distanceListRe.add(map);

//Compute distance list and process accuracy

                        listDistance.add(Double.parseDouble(String.valueOf(distanceInMeters)));
                        ss.notifyDataSetChanged();

                        int j=listDistance.size();

                        if(listDistance.size()>15) {
                            if (listDistance.get(j - 1) < 0.03
                                    && listDistance.get(j - 2) < 0.03
                                    && listDistance.get(j - 3) < 0.03
                                    && listDistance.get(j - 4) < 0.03
                                    && listDistance.get(j - 5) < 0.03) {

                                coordinatesTxt.removeTextChangedListener(textwatcher);
                                ss.clear();
                                scs.clear();
                                lisLat.clear();
                                lisLon.clear();
                                listCoordinates.clear();
                                listDistance.clear();
//Coordinates to be set as address point

                                myLatitude = distanceListRe.get(j-2).get("latitude");
                                myLongitude = distanceListRe.get(j-2).get("longitude");
//Viewing the point on map
                                selDis = Double.parseDouble(distanceListRe.get(j-2).get("distance"));
                                selLat=Double.parseDouble(myLatitude);
                                selLon=Double.parseDouble(myLongitude);
                                SetMarker();
                            }
                        }


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private synchronized void setUpGClient() {
        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();

        } else {

        }
    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(AnalyticActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =                     LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    locationRequest = new LocationRequest();
                    locationRequest.setInterval(20);
                    locationRequest.setFastestInterval(20);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(AnalyticActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(AnalyticActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(AnalyticActivity.this,"Connection Successful",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(AnalyticActivity.this,"Connection Failed",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
        getMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
            if (googleApiClient!=null){
                if (googleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
                    googleApiClient.disconnect();
                }
            } else {

            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            latitude = mylocation.getLatitude();
            longitude = mylocation.getLongitude();
            coordinatesTxt.setText(Double.toString(latitude));
            longitudeTxt.setText(Double.toString(longitude));
            scs.notifyDataSetChanged();
            ccs.notifyDataSetChanged();

            distanceCountTxt.setText(String.valueOf(listDistance.size()));
            coordinatesCountTxt.setText(String.valueOf(lisLat.size()));
        }

        StartScan();
    }

    private void StartScan(){

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-1.289238, 36.820442);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void SetMarker(){

        mMap.clear();
        LatLng building = new LatLng(selLat, selLon);
        mMap.addMarker(new MarkerOptions().position(building).title(String.valueOf(selDis)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(building));
        Log.e("PlaceLL", String.valueOf(selDis));

    }
}
