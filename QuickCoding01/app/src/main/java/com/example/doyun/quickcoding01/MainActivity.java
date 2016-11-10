package com.example.doyun.quickcoding01;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btngetMin;
    Button btngetMax;
    TextView tvValue;
    TextView tvResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int[] value = {1,20,10,4,5,6,2,14,63};
        final MyMaxValue max = new MyMaxValue(value);
        final MyMinValue min = new MyMinValue(value);

        btngetMax = (Button)findViewById(R.id.btngetMax);
        btngetMin = (Button)findViewById(R.id.btngetMin);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvValue = (TextView)findViewById(R.id.tvValue);
        String a = "";
        for(int i=0;i<value.length;i++){
            if(i==value.length-1) a+=value[i];
            else a+=value[i]+", ";
        }
        tvValue.setText("Value : \n"+a);

        btngetMax.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int result = max.getValues();
                tvResult.setText("GetResult : " + result);
            }
        });
        btngetMin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int result = min.getValues();
                tvResult.setText("GetResult : " + result);
            }
        });

    }
}
