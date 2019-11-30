package in.sisoft.apps.ecrm.Location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

public class MyPosition implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Activity activity ;
    private Boolean mLocationPermissionsGranted = false;
    private double log_lat;
    private double log_long;
    private static final String TAG ="MyPosition";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;




    private LatLongListener listener;
    public void setUpListener(LatLongListener listener){
        this.listener = listener;
    }


    public MyPosition(Activity activity){
        this.activity = activity;
        getLocationPermission();


    }
    public void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        try{
            if(mLocationPermissionsGranted){
                Log.d(TAG,"permission : permission granted");
                Task task = mFusedLocationProviderClient.getLastLocation();
                task.addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        Log.d(TAG, "onComplete: success!");
                        Location location = (Location) task1.getResult();
                        if(location!=null){
                            Log.d(TAG, "onComplete: location found!");
                            log_lat = location.getLatitude();
                            log_long = location.getLongitude();
                            listener.onLatLogClick(log_lat,log_long);
                        }
                        else{
                            Log.d(TAG,"onComplete : location not found");
                            Toast.makeText(activity, "Location not found! close the app and try again", Toast.LENGTH_SHORT).show();
                        }
                    }else{

                        Log.d(TAG, "onComplete: error onComplete");
                        Toast.makeText(activity, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Log.d(TAG,"permission not granted");
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }


    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(activity, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(activity, COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;

            }else{
                ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;

                }
            }
        }
    }
    public interface LatLongListener{
        void onLatLogClick(double loc_lat, double loc_long);
    }
}

