package com.example.doyun.quickcoding01;

/**
 * Created by doyun on 2016-09-29.
 */

public class MyMaxValue extends MyValues {
    public MyMaxValue(int[] n) {
        super(n);
    }

    @Override
    public int getValues() {
        int max =Value[0];
        for(int i =1;i<Value.length;i++)
            max= (max < Value[i]) ? Value[i] : max;
        return max;
    }
}
