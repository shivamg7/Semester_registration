package com.example.shivamgupta.firebaseapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;

public class FormActivity3 extends AppCompatActivity implements LocationListener {

    private FirebaseAuth firebaseAuth;
    private TextView latitude, longitude;
    private EditText A1, A2;
    private Button next, getLocation;
    private String Ad1, Ad2;
    private Double Lat_itude, Long_itude;
    private LocationManager locationManager;
    private AddressResultReceiver mResultReceiver;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location location, mLastKnownLocation;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form3);
        extras = getIntent().getExtras();
        location = new Location("myLocation");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(FormActivity3.this);
        mResultReceiver = new AddressResultReceiver(null);
        UI();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(FormActivity3.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                fetchAddressButtonHander();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate()) {
                    extras.putString("A1", A1.getText().toString());
                    extras.putString("A2", A1.getText().toString());
                        extras.putDouble("Latitude", (Lat_itude!=null)?Lat_itude:0.0);
                        extras.putDouble("Longitude", Long_itude!=null?Long_itude:0.0);

                    Intent it = new Intent(FormActivity3.this, FormActivity4.class);
                    it.putExtras(extras);
                    startActivity(it);
                } else {
                    Toast.makeText(FormActivity3.this, "Invalid data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UI() {
        next = (Button) findViewById(R.id.btNext);
        getLocation = (Button) findViewById(R.id.btLocation);
        A1 = (EditText) findViewById(R.id.etA1);
        A2 = (EditText) findViewById(R.id.etA2);
        latitude = (TextView) findViewById(R.id.tvLat);
        longitude = (TextView) findViewById(R.id.tvLong);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private boolean Validate() {
        if (A1.getText().toString().isEmpty() || A2.getText().toString().isEmpty() ) {
            return false;
        }
        return true;
    }

    public void getLocation() {
        try {
            Location location = new Location("dummyprovider");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location==null){
                Toast.makeText(FormActivity3.this, "GPS Provider failed", Toast.LENGTH_SHORT).show();
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location == null){
                    //Toast.makeText(FormActivity3.this, "Network Provider failed", Toast.LENGTH_SHORT).show();
                }else{
                    onLocationChanged(location);
                   // Toast.makeText(FormActivity3.this,"Lati"+location.getLatitude()+" Long :"+ location.getLongitude(),Toast.LENGTH_SHORT).show();
                }
            }else{
                onLocationChanged(location);
                //Toast.makeText(FormActivity3.this,"Lati"+location.getLatitude()+" Long :"+ location.getLongitude(),Toast.LENGTH_SHORT).show();
            }

        } catch (SecurityException e) {
            Toast.makeText(FormActivity3.this, "GPS Not working", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        try {
        latitude.setText((getString(R.string.latitude, location.getLatitude())));
        longitude.setText((getString(R.string.longitude, location.getLongitude())));
        Lat_itude = location.getLatitude();
        Long_itude = location.getLongitude();
       // Toast.makeText(FormActivity3.this,"ppLati"+location.getLatitude()+" Long :"+ location.getLongitude(),Toast.LENGTH_SHORT).show();

           /* Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            A1.setText(addresses.get(0).getAddressLine(0) + ", " +
                    addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2));*/
        } catch (Exception e) {
            Toast.makeText(FormActivity3.this, "GPS Failed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(FormActivity3.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logoutMenu:
                Logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void Logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(FormActivity3.this, MainActivity.class));
    }

    class AddressResultReceiver extends ResultReceiver {

        String mAddressOutput;
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }

            //Display the address string
            //or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            displayAddressOutput();

            //Show a toast message of an address was found
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(FormActivity3.this, R.string.address_found, Toast.LENGTH_SHORT).show();
            }
        }

        private void displayAddressOutput() {
            Toast.makeText(FormActivity3.this,mAddressOutput,Toast.LENGTH_SHORT).show();
           // A1.setText(mAddressOutput);
        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastKnownLocation);
        Toast.makeText(FormActivity3.this,"Geocoder Started",Toast.LENGTH_SHORT).show();
        startService(intent);
    }

    private void fetchAddressButtonHander() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastKnownLocation = location;

                        // In some rare cases the location returned can be null
                        if (mLastKnownLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(FormActivity3.this,
                                    R.string.no_geocoder_available,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new location
                        startIntentService();
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        //TODO
    }
}


