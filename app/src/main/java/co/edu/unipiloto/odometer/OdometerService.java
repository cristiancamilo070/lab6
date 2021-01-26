package co.edu.unipiloto.odometer;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Random;



public class OdometerService extends Service {

    private final IBinder binder= new OdometerBinder();
    private LocationListener listener;
    private LocationManager locationManager;
    private static double distanceInMeters;
    private static Location lastLocation=null;
    public static final String PERMISSION_STRING
            =Manifest.permission.ACCESS_FINE_LOCATION;

    public class OdometerBinder extends Binder{
        OdometerService getOdometer(){
            return OdometerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (lastLocation==null){
                    lastLocation=location;
                }
                distanceInMeters+=location.distanceTo(lastLocation);
                lastLocation=location;
            }
            @Override
            public void onProviderDisabled(String arg0) {}
            @Override
            public void onProviderEnabled(String arg0) {}
            @Override
            public void onStatusChanged(String arg0,int arg1,Bundle bundle) {}
        };

        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,PERMISSION_STRING)
                ==PackageManager.PERMISSION_GRANTED){
            String provider=locationManager.getBestProvider(new Criteria(),true);
            if (provider!=null){
                locationManager.requestLocationUpdates(provider,1000,1,listener);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager!=null && listener!=null){
            if (ContextCompat.checkSelfPermission(this,PERMISSION_STRING)
                    ==PackageManager.PERMISSION_GRANTED){
                locationManager.removeUpdates(listener);
            }
            locationManager=null;
            listener=null;
        }
    }

    public double getDistanceMillas(){
        return this.distanceInMeters/1609.344;
    }
    public double getDistanceMetros(){
        return this.distanceInMeters;
    }
}