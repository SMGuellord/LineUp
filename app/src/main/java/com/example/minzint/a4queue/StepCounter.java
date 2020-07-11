package com.example.minzint.a4queue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.*;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StepCounter extends Activity implements SensorEventListener {

    // ===================================================================
    // VARIABLES

    // THIS IS FOR THE FOOTSTEP COUNTER
    private SensorManager sensorManager;
    private TextView footstepCount;
    boolean activityRunning;
    private long steps = 0;

    // THIS IS FOR THE LOCATION
    private Button locationButton;
    private TextView locationTextView;
    private LocationManager locationManager;
    private LocationListener locationListener;

    // ===================================================================
    // END OF VARIABLES


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        // GET THE TEXT VIEW
        footstepCount = findViewById(R.id.count);

        // GET THE SYSTEM SERVICE
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // GET LOCATION BUTTON AND TEXT VIEW
        locationButton = findViewById(R.id.locationButton);
        locationTextView = findViewById(R.id.locationView);

        // LOCATION MANAGER
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // LOCATION LISTENER
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                locationTextView.append("\n " + location.getLatitude() + " " + location.getLongitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent myIntent = new Intent(getApplicationContext(), LimitedFunctionalityNotify.class);
                startActivity(myIntent);
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);

            return;
        }
        }else{
            configureButton();

        }
        // the third paramater indicated the amount of distance before the loation will change - measured in meters
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    configureButton();
                }
                return;
        }

    }

    private void configureButton() {

        locationButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

            }});


    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (countSensor != null) {

            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);

        } else {

            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {

        super.onPause();
        activityRunning = false;
        sensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        if (activityRunning) {
            if (footstepCount != null) {

                if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    steps++;
                }

                footstepCount.setText(String.valueOf(steps));

            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}