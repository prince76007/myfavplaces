package com.example.myfavoriteplaces;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> placesList=new ArrayList<String>();
    static ArrayList<LatLng> locationList=new ArrayList<LatLng>();
    static SharedPreferences sharedPreferences;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placesList.add("Add a new place");
        locationList.add(new LatLng(0,0));
        sharedPreferences=this.getSharedPreferences("com.example.myfavoriteplaces",MODE_PRIVATE);
        try {
            placesList= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("placesList",ObjectSerializer.serialize(placesList)));
            ArrayList<Float> lat= (ArrayList<Float>) ObjectSerializer.deserialize(sharedPreferences.getString("latList",ObjectSerializer.serialize(new String())));
            ArrayList<Float> lng= (ArrayList<Float>) ObjectSerializer.deserialize(sharedPreferences.getString("lngList",ObjectSerializer.serialize(new String())));
            locationList.clear();
            for (int i=0;i<lat.size();i++){
                locationList.add(new LatLng((double)lat.get(i),(double)lng.get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView=(ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,placesList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getApplicationContext(),MapsActivity.class);
                if(position==0){
                    startActivityForResult(intent,1);
                }else{
                    intent.putExtra("lat",locationList.get(position).latitude);
                    intent.putExtra("lng",locationList.get(position).longitude);
                    startActivityForResult(intent,2);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            sharedPreferences.edit().putString("placesList",ObjectSerializer.serialize(placesList)).apply();
            ArrayList<Float> lat = new ArrayList<Float>();
            ArrayList<Float> lng = new ArrayList<Float>();
            for (LatLng coords : locationList){
                lat.add((float)coords.latitude);
                lng.add((float)coords.longitude);
            }
            sharedPreferences.edit().putString("latList",ObjectSerializer.serialize(lat)).apply();
            sharedPreferences.edit().putString("lngList",ObjectSerializer.serialize(lng)).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}