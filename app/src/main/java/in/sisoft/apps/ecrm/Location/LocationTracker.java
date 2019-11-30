package in.sisoft.apps.ecrm.Location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Objects;

import in.sisoft.apps.ecrm.Activities.WelcomeActivity;
import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Sunita on 28-May-2016.
 */
@SuppressLint("Registered")
public class LocationTracker implements LocationListener,ActivityCompat.OnRequestPermissionsResultCallback{

    private Context mContext;
    private Activity current_Activity;
    private String TAG="LocationTracker" ;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;



    private Location location; // location
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 ; // 1 second
    private LocationManager locationManager;
    @SuppressLint("StaticFieldLeak")
    private static LocationTracker instance;

    public void setContext(Context mCtx)
    {
        this.mContext = mCtx;
    }
    public void setActivity(Activity mAct)
    {
        this.current_Activity = mAct;
    }

    public static LocationTracker getInstance() {
        if(instance == null) {
            instance = new LocationTracker();
        }
        return instance;
    }
    private LocationTracker(){ }
    public void init() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        location=getLocation();
    }


    public void startLocationUpdate(){
    try {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


    } catch (SecurityException e) {
        e.printStackTrace();
    }

    }
    public void stopLocationUpdate() {
        locationManager.removeUpdates(this);
    }
    public Location getLocation() {
        try {

            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location == null) {
                 location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
             }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return location;
    }



    // check permission
    public void chkPermission(){

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(mContext, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(current_Activity, permissions, 10);
        }
        if(ContextCompat.checkSelfPermission(mContext, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(current_Activity, permissions, 20);
        }
        if(ActivityCompat.checkSelfPermission(current_Activity, Manifest.permission.READ_CALL_LOG)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(current_Activity, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(current_Activity,new String[]{"android.permission.READ_CALL_LOG","android.permission.READ_CONTACTS"},30);

        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 20:
            {
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                }
            }
            case 30:{
                if(grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(current_Activity, "Permission 1 granted", Toast.LENGTH_LONG).show();
                }

                if(grantResults[1] ==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(current_Activity, "Permission 2 granted", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    // Location Listener callback method
    public void onLocationChanged(Location mLocation) {
        this.location = mLocation ;
        if(current_Activity instanceof WelcomeActivity)
        listener.onLocationChanged();
        Toast.makeText(mContext, ""+mLocation.getLongitude()+"\n"+mLocation.getProvider(), Toast.LENGTH_SHORT).show();
    }
    public void onProviderDisabled(String provider) {
    }
    public void onProviderEnabled(String provider) {
        Log.d("LocationTracker", "OnProviderEnabled:"+provider);
        location = getLocation();
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }





    /////////// checking is gps enable
    public static boolean isGpsEnable(Activity activity)  {

        LocationManager service = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        assert service != null;
        boolean isGpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled)
        {
            showGpsDialog(activity);
            return false;
        }
        return true;
    }
    private static   void showGpsDialog(Activity activity){
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_gps_alert);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthLcl = (int) (displayMetrics.widthPixels*0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels*0.60f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout .setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button btn_gps = dialog.findViewById(R.id.btn_gps);
        btn_gps.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(intent,121);
            dialog.dismiss();
        });

        Button btn_exit = dialog.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(v -> AppGlobal.AppExit(activity));

        dialog.setOnCancelListener(dialog1 ->
        {
            isGpsEnable(activity);
        });
        dialog.show();

    }




    public interface LocationChangeListener{
        void onLocationChanged();
    }

    private LocationChangeListener listener;
    public void setupLocationChangeListener(LocationChangeListener listener){
        this.listener = listener;
    }

}


