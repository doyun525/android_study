package com.example.doyun.mylocationlogger2;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class MarkerListActivity extends Activity {
    public static final int EVENT_POPUP_SAVE = 0;
    public static final int EVENT_POPUP_VIEW = 1;

    private  int mode;

    Intent intent;

    LatLng latLng;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            Log.d("test", "list result code "+resultCode);
            if(resultCode == 1){
                setResult(1, new Intent().putExtra("latlng",latLng));
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.info_window_layout);
            intent = getIntent();
            Log.d("test", "inten " + intent);

            latLng = intent.getParcelableExtra("latlng");
            View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
            ListView listView = (ListView) findViewById(R.id.list);

            ArrayList<? extends HashMap<String, Object>> list ;

            list = intent.getParcelableArrayListExtra("list");

            Log.d("test", "list " + list);

            ListAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, new String[]{"name", "time"}, new int[]{android.R.id.text1, android.R.id.text2});
            Log.d("test", "list " + list);
            Log.d("test", "adapter " + adapter.getItem(0) + "list view " + listView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MarkerListActivity.this, "item " + (HashMap<String, Object>) parent.getAdapter().getItem(position), Toast.LENGTH_SHORT);
                    Intent intent = new Intent(MarkerListActivity.this, EventActivity.class);
                    HashMap<String, Object> h = (HashMap<String, Object>) parent.getAdapter().getItem(position);
                    intent.putExtra("hashMap", h);
                    intent.putExtra("latlng", latLng);
                    if(h.containsKey("name")){
                        mode = EVENT_POPUP_VIEW;
                    }
                    else{
                        mode = EVENT_POPUP_SAVE;
                    }
                    Log.d("test","h "+h.containsKey("name"));
                    Log.d("test", "click item  " + (HashMap<String, Object>) parent.getAdapter().getItem(position));
                    intent.putExtra("mode",mode);
                    startActivityForResult(intent,0);

                }
            });
        }catch (Exception ex){
            Log.d("exception","exception "+ ex.getMessage());
        }

    }
}
