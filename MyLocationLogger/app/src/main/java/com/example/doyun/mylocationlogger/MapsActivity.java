package com.example.doyun.mylocationlogger;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.doyun.mylocationlogger.DB.LocationDBHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final int DATE_DIALOG_ID = 0;

    private int mYear, mMonth, mDay;
    int LocationCheckCycle = 1000;
    final CharSequence[] cycleItem = {"30초","1분","5분","10분","15분"};

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private DatePickerDialog datePickerDialog;

    LocationDBHelper locationDBHelper;
    SQLiteDatabase location_db;

    final Calendar c;
    Date d;
    Intent sintent;

    TextView viewDate;
    TextView tv_cycle;

    Button nextday;
    Button previousday;
    Button DBdelet;
    Button CycleSetting;
    Button MapRenew;
    ToggleButton gpsToggle;

    List<myLocation> myLocationList;
    List<Marker> mMarkerList;
    List<Polyline> mPolylineList;

    Marker marker;

    String date;
    String time;

    MapsActivity() {
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        d = c.getTime();
        date =(new SimpleDateFormat("yyyy-MM-dd").format(d));
    }

    public String dateChange(Calendar c, int i){
        c.add(c.DATE, i);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        d = c.getTime();
        return  (new SimpleDateFormat("yyyy-MM-dd").format(d));
    }

    private void setMarkerbyDay(SQLiteDatabase db, String date) {

        mMap.clear();

        String sb = "SELECT * from contact WHERE date=?";

        int lastedTime = 0;
        Log.d("test",""+db);
        if(db ==null) {
            location_db = locationDBHelper.getReadableDatabase();

        }
        Cursor cursor = db.rawQuery(sb, new String[]{date});
        Log.d("test","cusor "+cursor);
        myLocation mylocation;
        LatLng lastedlatLng = null;
        mMarkerList = new ArrayList<Marker>();
        mPolylineList = new ArrayList<Polyline>();
        myLocationList = new ArrayList<myLocation>();
        int color = Color.RED;
        while (cursor.moveToNext()) {
            mylocation = new myLocation(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            myLocationList.add(mylocation);
            LatLng latLng = mylocation.getLatLng();
            int time = mylocation.getTime();

            boolean MarkerOverlap = false;
            Location location = new Location("");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);

            if (lastedlatLng != null) {

                if (time - lastedTime >= mylocation.checkCycle*1.0/(1000*60) * 5) {
                    Log.d("test", "time : "+time+"lasttime : "+lastedTime);
                    color = (color == Color.RED) ? Color.BLUE : Color.RED;
                }
                PolylineOptions rectOptions = new PolylineOptions().add(lastedlatLng, latLng).clickable(true).color(color).width(15);
                mPolylineList.add(mMap.addPolyline(rectOptions));
                lastedlatLng = latLng;
            }
            else {
                lastedlatLng = latLng;
            }
            lastedTime=time;

            for (int i = 0; i < mMarkerList.size(); i++) {
                Location slocation = new Location("");
                LatLng ln = mMarkerList.get(i).getPosition();
                slocation.setLatitude(ln.latitude);
                slocation.setLongitude(ln.longitude);

                if (location.distanceTo(slocation) <= 1) {
                    Marker m = mMarkerList.get(i);
                    String s = m.getSnippet();
                    s += "\n" + mylocation.s_time;
                    m.setSnippet(s);
                    MarkerOverlap = true;
                    break;
                }
            }
            if (!MarkerOverlap) {
                Marker m = mMap.addMarker(new MarkerOptions().position(latLng).snippet(mylocation.s_time));
                mMarkerList.add(m);
            }
        }
        if (mMarkerList.size() != 0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(mMarkerList.get(mMarkerList.size() - 1).getPosition()),300,null);
        }
    }
    public boolean isServiceRunningCheck(String serviceName) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        location_db.close();
        super.onDestroy();


    }

    @Override
    protected void onStart() {
        //if(!mGoogleApiClient.isConnected()){
        //mGoogleApiClient.connect();
        //}
        //location_db = locationDBHelper.getReadableDatabase();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            //onCreate(savedInstanceState);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            //onCreate(savedInstanceState);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            //onCreate(savedInstanceState);
            return;
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();
        }
        mGoogleApiClient.connect();


        locationDBHelper = new LocationDBHelper(this, "mnt/sdcard/mylo.db");
        location_db = locationDBHelper.getWritableDatabase();

        datePickerDialog = new DatePickerDialog(this, mDataSetListener, mYear, mMonth - 1, mDay);

        sintent = new Intent(MapsActivity.this, gpsService.class);
        LocationCheckCycle = 30*1000;
        sintent.putExtra("location_cycle", LocationCheckCycle);

        viewDate = (TextView) findViewById(R.id.textView);
        tv_cycle = (TextView)findViewById(R.id.tv_cycle);

        nextday = (Button) findViewById(R.id.nextday);
        previousday = (Button) findViewById(R.id.previousday);
        DBdelet = (Button) findViewById(R.id.DBDelet);
        CycleSetting = (Button) findViewById(R.id.cycleSetting);
        MapRenew = (Button) findViewById(R.id.mapRenew);

        gpsToggle = (ToggleButton) findViewById(R.id.gpsToggle);

        viewDate.setText(date);
        tv_cycle.setText("주기 : "+"30초");

        gpsToggle.setChecked(isServiceRunningCheck("com.example.doyun.mylocationlogger.gpsService"));

        gpsToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsToggle.isChecked()) {
                    startService(sintent);
                } else if (!gpsToggle.isChecked()) {
                    Log.d("test", "nock");
                    stopService(sintent);
                }
            }
        });
        viewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        nextday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = dateChange(c,1);
                viewDate.setText(date);
                datePickerDialog = new DatePickerDialog(MapsActivity.this, mDataSetListener, mYear, mMonth - 1, mDay);
                setMarkerbyDay(location_db, date);
            }
        });
        previousday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = dateChange(c,-1);
                viewDate.setText(date);
                datePickerDialog = new DatePickerDialog(MapsActivity.this, mDataSetListener, mYear, mMonth - 1, mDay);
                setMarkerbyDay(location_db, date);
            }
        });
        DBdelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase.deleteDatabase(new File(LocationDBHelper.F_path));
                location_db=null;
            }
        });
        CycleSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder seletCycle = new AlertDialog.Builder(MapsActivity.this);
                seletCycle.setTitle("주기를 선택하세요");
                seletCycle.setItems(cycleItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                LocationCheckCycle = 30*1000;
                                break;
                            case 1:
                                LocationCheckCycle = 60*1000;
                                break;
                            case 2:
                                LocationCheckCycle = 60*1000*5;
                                break;
                            case 3:
                                LocationCheckCycle = 60*1000*10;
                                break;
                            case 4:
                                LocationCheckCycle = 60*1000*15;
                                break;
                        }
                        tv_cycle.setText("주기 : "+cycleItem[which]);
                        sintent.putExtra("location_cycle", LocationCheckCycle);
                        if(isServiceRunningCheck("com.example.doyun.mylocationlogger.gpsService")){
                            startService(sintent);
                        }
                        Toast.makeText(MapsActivity.this, ""+LocationCheckCycle, Toast.LENGTH_SHORT).show();
                    }
                });
                seletCycle.create().show();
            }
        });
        MapRenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarkerbyDay(location_db, date);
            }
        });

    }

    private DatePickerDialog.OnDateSetListener mDataSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yaerSelect, int monthOfyear, int dayOfmonth) {
            c.set(yaerSelect, monthOfyear, dayOfmonth);
            mYear = yaerSelect;
            mMonth = monthOfyear + 1;
            mDay = dayOfmonth;
            d = c.getTime();
            date =(new SimpleDateFormat("yyyy-MM-dd").format(d));
            viewDate.setText(date);
            mMap.clear();
            setMarkerbyDay(location_db, date);
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        mMap.setMyLocationEnabled(true);

        setMarkerbyDay(location_db, date);

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(final Marker marker) {
                if(mMarkerList.contains(marker))
                    deletDBTime(marker);
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Location location = new Location("A");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                for(final Marker marker : mMarkerList) {
                    Location location2 = new Location("B");
                    location2.setLatitude(marker.getPosition().latitude);
                    location2.setLongitude(marker.getPosition().longitude);
                    if(location.distanceTo(location2)<1) {
                        deletDBTime(marker);
                        break;
                    }
                }
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                if(marker!=null) marker.remove();
                int index = mPolylineList.indexOf(polyline);
                String stime = myLocationList.get(index).s_time;
                String etime = myLocationList.get(index+1).s_time;
                List<LatLng> latLngList = polyline.getPoints();
                Location location = new Location("A");
                location.setLatitude(latLngList.get(0).latitude);
                location.setLongitude(latLngList.get(0).longitude);
                Location location2 = new Location("B");
                location2.setLatitude(latLngList.get(1).latitude);
                location2.setLongitude(latLngList.get(1).longitude);
                Log.d("test", "latlng0 "+latLngList.get(0) + " latlng1 "+latLngList.get(1));
                Log.d("test", "latlng list"+latLngList);

                double d = location.distanceTo(location2);
                String s = String.format("%.2f",d);
                Toast.makeText(MapsActivity.this, stime+"~"+etime+" "+s+"m", Toast.LENGTH_SHORT).show();

                double la = (latLngList.get(1).latitude+latLngList.get(0).latitude)/2;
                double lo = (latLngList.get(1).longitude+latLngList.get(0).longitude)/2;
                LatLng latLng  = new LatLng(la, lo) ;

                marker = mMap.addMarker(new MarkerOptions().position(latLng).snippet(stime+"~"+etime+" "+s+"m").alpha(0).infoWindowAnchor(0.5f,1));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, null);
                marker.showInfoWindow();
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                String snipeet = marker.getSnippet();

                TextView tvsnipeet = (TextView) v.findViewById(R.id.tv_snippet);
                tvsnipeet.setText(snipeet);

                Log.d("test", "Marker latlng"+marker.getPosition());
                return v;
            }
        });
    }

    public void deletDBTime(final Marker marker){
        String s = marker.getSnippet()+"\n전부 삭제";
        final CharSequence[] time = s.split("\n");
        Log.d("test", ""+ time[0].toString());
        final AlertDialog.Builder seletDeletTime = new AlertDialog.Builder(MapsActivity.this);
        seletDeletTime.setTitle("삭제할 시간을 선택하세요");
        seletDeletTime.setItems(time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("해당 시간정보를 삭제 하시겠습니까?");
                if(which == time.length-1) builder.setMessage(marker.getSnippet());
                else builder.setMessage(time[which]);
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which1) {
                        Log.d("test", ""+ which);
                        if(which == time.length-1){
                            for(int i=0;i<time.length-1;i++)
                                location_db.delete("contact","date=? and time=?", new String[]{date, time[i].toString()});
                        }
                        else location_db.delete("contact","date=? and time=?", new String[]{date, time[which].toString()});
                        setMarkerbyDay(location_db, date);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()),300,null);
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });
        seletDeletTime.create().show();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lo = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng latlng = new LatLng(lo.getLatitude(), lo.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 19));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
