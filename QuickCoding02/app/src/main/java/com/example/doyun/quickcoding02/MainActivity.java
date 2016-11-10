package com.example.doyun.quickcoding02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Random;

import static android.widget.Toast.*;


public class MainActivity extends AppCompatActivity {
    Button send, bigger, smaller, bingo;
    TextView tvresult;
    Random rand = new Random();
    public int num = rand.nextInt();
    int max, min, count = 0, countMax = 0, countMin = 0;
    int state=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = (Button) findViewById(R.id.btSend);
        bigger = (Button) findViewById(R.id.btBig);
        smaller = (Button) findViewById(R.id.btSma);
        bingo = (Button) findViewById(R.id.btBingo);
        tvresult = (TextView) findViewById(R.id.tvR);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num = rand.nextInt(1000);
                tvresult.setText("Your Number is " + num);
                count = 0; countMax = 0; countMin = 0;state=0;
            }
        });

        bigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countMin == 0 && state!=1) {
                    min = num;
                    num += rand.nextInt(1000);
                } else {
                    min = num;
                    num = num + (max - min) / 2;
                    state=1;
                }
                tvresult.setText("Your Number is " + num);
                countMin=0;
                countMax++;
                count++;
            }
        });
        smaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countMax == 0 && state!=1) {
                    max = num;
                    num -= rand.nextInt(1000);
                } else {
                    max = num;
                    num  -= (max - min) / 2;
                    state=1;
                }
                tvresult.setText("Your Number is " + num);
                countMax=0;
                countMin++;
                count++;
            }
        });
        bingo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = count + "회 만에 성공";
                //tvresult.setText(s);
                Toast.makeText(MainActivity.this,  s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
