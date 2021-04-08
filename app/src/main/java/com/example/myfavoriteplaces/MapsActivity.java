package com.example.myfavoriteplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager=null;
    Geocoder geocoder=null;
    String address="";
    List<Address> addresses=null;
    LatLng currentLocation=null;
    ArrayList<LatLng> latLngs=null;
    Intent gotIntent;
    boolean firstTime=true;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED ){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
            }
        }
    }
    public void setMap(Location currentlocation){
        try {
            mMap.clear();
            if (latLngs.size()==0) {
                addresses = geocoder.getFromLocation(currentlocation.getLatitude(), currentlocation.getLongitude(), 1);
                if (addresses.size() > 0 && addresses.get(0).getAddressLine(0) != null)
                    address = addresses.get(0).getAddressLine(0);
                else
                    address = "" + new Date();
                currentLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLocation).title(address));
            }
              for (int i=0;i<latLngs.size();i++){
                  addresses=geocoder.getFromLocation(latLngs.get(i).latitude,latLngs.get(i).longitude,1);
                  if (addresses.size()>0&& addresses.get(0).getAddressLine(0)!=null)
                      address=addresses.get(0).getAddressLine(0);
                  else
                      address=""+new Date();
                  currentLocation =latLngs.get(i);
                  mMap.addMarker(new MarkerOptions().position(currentLocation).title(address));
              }
            if (firstTime) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                firstTime = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        latLngs= new ArrayList<LatLng>();
        gotIntent=getIntent();
        if (gotIntent!=null && gotIntent.getDoubleExtra("lat",-0)!=-0){
            LatLng latLng =new LatLng(gotIntent.getDoubleExtra("lat",-0),gotIntent.getDoubleExtra("lng",0));
            latLngs.add(latLng);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        locationManager= (LocationManager)getSystemService(LOCATION_SERVICE);
        geocoder=new Geocoder(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            Location lastKnownLocation =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            setMap(lastKnownLocation);
            if (latLngs.size()==0)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setMap(location);
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        try {
            latLngs.add(latLng);
            List<Address> choseAddresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            String choseAddress="";
            if (choseAddresses.size()>0){
                choseAddress=choseAddresses.get(0).getAddressLine(0);
            }
            mMap.addMarker(new MarkerOptions().position(latLng).title(choseAddress));
            MainActivity.placesList.add(choseAddress);
            MainActivity.locationList.add(latLng);
            Toast.makeText(getApplicationContext(),"Location Is Added",Toast.LENGTH_LONG).show();
            MainActivity.arrayAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}