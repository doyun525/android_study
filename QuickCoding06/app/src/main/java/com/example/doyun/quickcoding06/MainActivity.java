package com.example.doyun.quickcoding06;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    Sensor sensor_accelerometer;
    TextView textView;
    int step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor_accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor_accelerometer, SensorManager.SENSOR_DELAY_UI);

        textView = (TextView)findViewById(R.id.textView2);
        step = 0;

    }
    float lastX, lastY,lastZ;
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x,y,z;
            x=event.values[0];
            y=event.values[1];
            z=event.values[2];
            Log.d("test", "x "+event.values[0] + " y "+event.values[1] + " z "+event.values[2]);
            if(Math.abs(lastZ-z)>10){
                step++;
                textView.setText("your step : "+step);
            }
            lastZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
