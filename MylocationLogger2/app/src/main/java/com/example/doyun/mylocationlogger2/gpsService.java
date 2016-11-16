package com.example.doyun.mylocationlogger2;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.doyun.mylocationlogger2.DB.LocationDBHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;


public class gpsService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    Location lastedLocation;

    int LocationCheckCycle;
    long lastedTime;

    LocationDBHelper locationDBHelper;
    SQLiteDatabase db;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public gpsService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("test", "onDestroy");

        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        db.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test", "onStartCommand "+intent);
        if(intent!=null)
            LocationCheckCycle = intent.getExtras().getInt("location_cycle");
        Log.d("test", ""+LocationCheckCycle);
        if(db==null)
            db = locationDBHelper.getWritableDatabase();
        Log.d("test", "db:"+db);

        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
        else {
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test", "서비스 onCreate");

        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();
        }

        locationDBHelper = new LocationDBHelper(this);

        lastedTime = 0;

        Toast.makeText(gpsService.this, "백그라운드 서비스 실행", Toast.LENGTH_SHORT).show();
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LocationCheckCycle/2);
        mLocationRequest.setFastestInterval(LocationCheckCycle/4);

        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        }
        startLocationUpdates();
    }
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);

            if (locationAvailability.isLocationAvailable()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                Toast.makeText(this, "Location Unavialable", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        double distance = 0;
        long time = System.currentTimeMillis();
        if(location.getAccuracy()<=20 && time -lastedTime>=LocationCheckCycle) {
            if (lastedLocation == null) {
                lastedLocation = new Location("");
                lastedLocation.setLatitude(location.getLatitude());
                lastedLocation.setLongitude(location.getLongitude());
                saveLocation(location, time);
            } else if ((distance = lastedLocation.distanceTo(location)) > 10) {
                saveLocation(location, time);
                lastedLocation.setLatitude(location.getLatitude());
                lastedLocation.setLongitude(location.getLongitude());
            }
            Log.d("test", "LocationCheckCycle " + LocationCheckCycle);
        }
    }

    private void saveLocation(Location location, long time){
        Calendar calendar = Calendar.getInstance();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        myLocation myLocation = new myLocation(longitude, latitude);
        myLocation.saveLocation(db, calendar,LocationCheckCycle);
        float accuracy = location.getAccuracy();
        lastedTime=time;
        Toast.makeText(gpsService.this, "경도:" + longitude + ", 위도:" + latitude + " 정확도:" + accuracy + " 위치제공:" + location.getProvider(), Toast.LENGTH_SHORT).show();
        Log.d("test", "경도:" + longitude + ", 위도:" + latitude + " 정확도:" + accuracy + " 위치제공:" + location.getProvider());
    }
}
