package in.sisoft.apps.ecrm.Activities;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import in.sisoft.apps.ecrm.Location.LocationTracker;
import in.sisoft.apps.ecrm.R;

public class WelcomeActivity extends AppCompatActivity {


    LocationTracker locationTracker;
    RelativeLayout layout;
    Button btn_go;
    SupportMapFragment mapFragment;
    LatLng latLng;
    TextView tv_location_status;
    boolean isLocFound = false;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_for_location);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btn_go = findViewById(R.id.btn_go);
        btn_go.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            finish();
        });

        tv_location_status = findViewById(R.id.loc_status);

        LocationTracker.isGpsEnable(this);
        locationTracker = LocationTracker.getInstance();
        locationTracker.setContext(this);
        locationTracker.setActivity(this);
        locationTracker.init();
        locationTracker.chkPermission();
        locationTracker.startLocationUpdate();



            locationTracker.setupLocationChangeListener(()->{
                Location loc = locationTracker.getLocation();
                btn_go.setVisibility(View.VISIBLE);
                tv_location_status.setText("your current location");
                locationTracker.stopLocationUpdate();
                setMapFragment(loc.getLatitude(),loc.getLongitude());
                isLocFound = true;
            });



                 thread = new Thread(() -> {
                    try {
                        Thread.sleep(6000);
                        if(!isLocFound){

                            runOnUiThread(() -> {
                                Location loc = locationTracker.getLocation();
                                if(loc==null){
                                    tv_location_status.setText("failed to fetch location");
                                    setMapFragment(0,0);
                                    btn_go.setVisibility(View.VISIBLE);
                                }
                                else {
                                    tv_location_status.setText("this is your last location");
                                    setMapFragment(loc.getLatitude(),loc.getLongitude());
                                    btn_go.setVisibility(View.VISIBLE);
                                }
                            });

                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });thread.start();

    }
    private void setMapFragment(double loc_lat,double loc_long){

        latLng = new LatLng(loc_lat,loc_long);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            googleMap.clear();
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);


            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Sisoft")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationTracker.chkPermission();
        locationTracker.startLocationUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationTracker.stopLocationUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
 //       locationTracker.stopLocationUpdate();
        if(thread.isAlive()){
            thread.interrupt();
        }


    }
    @Override
    protected void onStart() {
        super.onStart();
        locationTracker.chkPermission();
  //      locationTracker.startLocationUpdate();

    }


}
