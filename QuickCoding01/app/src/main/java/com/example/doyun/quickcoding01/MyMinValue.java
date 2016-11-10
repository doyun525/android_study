package com.example.doyun.quickcoding01;

/**
 * Created by doyun on 2016-09-29.
 */

public class MyMinValue extends MyValues {

    public MyMinValue(int[] n) {
        super(n);
    }

    @Override
    public int getValues() {
        int min=Value[0];
        for(int i =1;i<Value.length;i++)
            min = (min > Value[i]) ? Value[i] : min;
        return min;
    }
}

