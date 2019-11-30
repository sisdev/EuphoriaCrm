package in.sisoft.apps.ecrm.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.GetMyTaskData;
import in.sisoft.apps.ecrm.Location.LocationTracker;
import in.sisoft.apps.ecrm.R;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout ll_my_leads;
    LinearLayout ll_add_lead;
    LinearLayout ll_work_report;
    LinearLayout ll_my_tasks;
    LinearLayout ll_search_lead;
    TextView tv_available_icon;
    TextView tv_title;
    TextView user;
    Button logout;
    private boolean doubleBackToExitPressedOnce;
    GetMyTaskData getMyTaskData;
    ProgressBar progressBar;

    LocationTracker locationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeView();

        //testing Location Tracker
        locationTracker = LocationTracker.getInstance();
        locationTracker.setContext(this);
        locationTracker.setActivity(this);
        locationTracker.chkPermission();
      //  locationTracker.checkGPSStatus();


        /////////////////////



    }

    @Override
    protected void onStart() {
        super.onStart();
        locationTracker.chkPermission();
        locationTracker.startLocationUpdate();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(SharedPref.getInstance(this).returnUser().equals(R.string.blank_text)){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(LocationTracker.isGpsEnable(this)){

            loadData();
        }
        locationTracker.stopLocationUpdate();

        doubleBackToExitPressedOnce = false;
        if (AppGlobal.taskList.size() > 0) {
            tv_available_icon.setText("" + AppGlobal.taskList.size());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void loadData() {

        Location loc = locationTracker.getLocation();
        Log.d("HomeActivity","load data : on load data");
   //     MyPosition position = new MyPosition(this);
   //     position.getDeviceLocation();
   //     position.setUpListener((loc_lat, loc_long) -> {
   //         Log.d("HomeActivity","load data : on setupListener");



            getMyTaskData = new GetMyTaskData(this, progressBar);
            if(loc==null){
                getMyTaskData.getData(0, 0);
            }
            else {
                getMyTaskData.getData(loc.getLatitude(), loc.getLongitude());
            }

            getMyTaskData.setMyListener(new GetMyTaskData.MyListener() {

                @Override
                public void callbackOnError() {
                    setViewVisivilityInvisible();
                    Log.d("HomeActivity", "setMyListener : onError");
                }

                @Override
                public void callbackOnSuccess() {
                    tv_available_icon.setText("" + AppGlobal.taskList.size());
                    setViewVisivilityVisible();
                    Log.d("HomeActivity", "setMyListener : onSuccess");
                }
            });

        }


    private void initializeView() {

        ll_my_tasks = findViewById(R.id.ll_my_task);
        ll_my_leads = findViewById(R.id.ll_my_leads);
        ll_add_lead = findViewById(R.id.ll_add_leads);
        ll_search_lead = findViewById(R.id.ll_search_lead);
        ll_work_report = findViewById(R.id.ll_work_report);
        tv_available_icon = findViewById(R.id.tv_task_available);
        tv_title = findViewById(R.id.title);
        user = findViewById(R.id.user);
        logout = findViewById(R.id.logout);
        final String username = SharedPref.getInstance(this).returnUser();
        user.setText(username);

        progressBar = findViewById(R.id.progress_circle);
        ll_work_report.setOnClickListener(this);
        ll_add_lead.setOnClickListener(this);
        ll_my_leads.setOnClickListener(this);
        ll_my_tasks.setOnClickListener(this);
        ll_search_lead.setOnClickListener(this);
        logout.setOnClickListener(this);

        setViewVisivilityInvisible();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        int view_id = v.getId();

        if(view_id == R.id.ll_work_report ){
            Intent intent = new Intent(HomeActivity.this,ReportWorkActivity.class);
            startActivity(intent);
        }

        if(view_id == R.id.ll_my_task ){

          Intent intent = new Intent(this,MyTaskActivity.class);
          startActivity(intent);
        }


       if(view_id == R.id.ll_search_lead){

            Intent intent = new Intent(this,SearchLeadActivity.class);
            startActivity(intent);
        }

        if(view_id == R.id.ll_my_leads ){
            if(LocationTracker.isGpsEnable(this)) {
                Intent intent = new Intent(this, MyLeadActivity.class);
                startActivity(intent);
            }
        }
        if(view_id == R.id.ll_add_leads )
        {
            Intent intent = new Intent(this,AddLeadsActivity.class);
            intent.putExtra("launch_origin","home_activity");
            startActivity(intent);
        }
        if(view_id ==R.id.logout){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(getDrawable(R.drawable.power));
            builder.setTitle(R.string.Logout);
            builder.setMessage(R.string.confirm_logout);
            builder.setPositiveButton(R.string.Logout, (dialog, which) -> {
                SharedPref.getInstance(this).deleteUser();
                SharedPref.getInstance(this).deleteAuthCode();//
                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);
                this.finish();
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                dialog.dismiss();
            });
           AlertDialog alert = builder.create();
            alert.show();
        }
    }


   private void setViewVisivilityInvisible(){

       ll_my_leads.setAlpha(0.4f);
       ll_add_lead.setAlpha(0.4f);
       ll_work_report.setAlpha(0.4f);
       ll_my_tasks.setAlpha(0.4f);
       ll_search_lead.setAlpha(0.4f);
       tv_available_icon.setAlpha(0.4f);
       tv_title.setAlpha(0.4f);
       logout.setAlpha(0.4f);
       user.setAlpha(0.4f);

       ll_my_leads.setClickable(false);
       ll_search_lead.setClickable(false);
       ll_add_lead.setClickable(false);
       ll_work_report.setClickable(false);
       ll_my_tasks.setClickable(false);
       tv_available_icon.setClickable(false);
       tv_title.setClickable(false);
       logout.setClickable(false);

   }
    private void setViewVisivilityVisible(){
        ll_my_leads.setAlpha(1f);
        ll_add_lead.setAlpha(1f);
        ll_work_report.setAlpha(1f);
        ll_search_lead.setAlpha(1f);
        ll_my_tasks.setAlpha(1f);
        tv_available_icon.setAlpha(1f);
        tv_title.setAlpha(1f);
        logout.setAlpha(1f);
        user.setAlpha(1f);

        ll_my_leads.setClickable(true);
        ll_add_lead.setClickable(true);
        ll_search_lead.setClickable(true);
        ll_work_report.setClickable(true);
        ll_my_tasks.setClickable(true);
        tv_available_icon.setClickable(true);
        tv_title.setClickable(true);
        logout.setClickable(true);


    }


    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(HomeActivity.this,"double click to exit", Toast.LENGTH_SHORT).show();

        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;

            }
        }, 2000);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 121 ){

            if(LocationTracker.isGpsEnable(this)){
                loadData();
            }

        }
    }

}
