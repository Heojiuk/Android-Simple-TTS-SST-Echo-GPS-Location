package kr.ac.mokwon.ice.testandroid;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
    public double latitude, longitude, altitude;



    @Override
    public void onLocationChanged(Location location) {
        latitude =   location.getLatitude();     //위도
        longitude =  location.getLongitude();    //경도
        altitude = location.getAltitude();  //해발고도


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
