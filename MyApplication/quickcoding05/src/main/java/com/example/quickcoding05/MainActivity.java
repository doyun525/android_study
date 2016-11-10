package com.example.quickcoding05;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText input;
    Button inbtn, inbtn2;
    TextView outString, outInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List intList = new LinkedList<Integer>();
        final List StringList = new LinkedList<String>();


        input = (EditText) findViewById(R.id.input);
        inbtn = (Button) findViewById(R.id.inbtn);
        inbtn2 = (Button) findViewById(R.id.inbtn2);
        outString = (TextView) findViewById(R.id.outString);
        outInt = (TextView) findViewById(R.id.outInt);

        List inputStringList = new ArrayList<String>();
        final List inputIntList = new ArrayList<Integer>();

        inbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = input.getText().toString();
                try {
                    int i = Integer.parseInt(s);
                    intList.add(i);
                }catch (NumberFormatException ex){
                    StringList.add(s);
                }
                input.setText("");
            }
        });
        inbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String out = "";
                for(int j = 0; j<intList.size();j++)
                    out+=intList.get(j)+" ";
                outInt.setText("입력된 숫자 : "+out);
                out="";
                for(int j = 0; j<StringList.size();j++)
                    out+=StringList.get(j)+" ";
                outString.setText("입력된 문자열 : "+out);
            }
        });

    }
}
