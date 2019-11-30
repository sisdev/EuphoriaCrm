package in.sisoft.apps.ecrm.Activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.Location.LocationTracker;
import in.sisoft.apps.ecrm.R;
import in.sisoft.apps.ecrm.RequestHandler;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;

public class ReportWorkActivity extends AppCompatActivity {

    //widget
    private Button btn_send_work;
    private EditText ed_work_report;
    private static final String TAG ="ReportWorkActivity";
    LocationTracker locationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_work);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(R.string.send_reports);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());



        btn_send_work = findViewById(R.id.btn_work_report);
        ed_work_report = findViewById(R.id.ed_work_report);

        btn_send_work.setOnClickListener(v -> {

            if (!ed_work_report.getText().toString().equals("")){
                hide_keyboard();
                sendWorkOperation();
                }
            else {
                Toast.makeText(ReportWorkActivity.this, "report field can't be empty", Toast.LENGTH_SHORT).show();
            }

        });

        locationTracker = LocationTracker.getInstance();
        locationTracker.setContext(this);
        locationTracker.setActivity(this);
        locationTracker.chkPermission();

        LocationTracker.isGpsEnable(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationTracker.startLocationUpdate();

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationTracker.stopLocationUpdate();

    }

    private void sendWorkOperation() {

        double loc_lat,loc_long;
        Location loc = locationTracker.getLocation();
        if(loc!=null){
             loc_lat = loc.getLatitude();
             loc_long = loc.getLongitude();
        }
        else {
            loc_lat = 0;
            loc_long=0;
        }


        final String strWorkReport = ed_work_report.getText().toString();

        SharedPref.getInstance(this);
        final String username = SharedPref.getInstance(this).returnUser();

            StringRequest request = new StringRequest(Request.Method.POST, AppGlobal.WORK_REPORT_URL,
                    response -> {


                        try{
                            ed_work_report.setText(R.string.blank_text);
                            btn_send_work.setText(R.string.SEND_REPORT);
                            Toast.makeText(this, ""+response, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            if(e.getMessage().contains("Incorrect")){
                                AppGlobal.logoutAuthError(this);
                            }
                        }


                        }, error -> {
                        ed_work_report.setText(R.string.blank_text);
                        btn_send_work.setText(R.string.SEND_REPORT);
                        Toast.makeText(ReportWorkActivity.this, "volley error\n"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String , String> param = new HashMap<>();
                    param.put("username",username);
                    param.put("report_text",strWorkReport);
                    param.put("loc_lat",String.valueOf(loc_lat));
                    param.put("loc_long",String.valueOf(loc_long));
                    param.put("org_code",SharedPref.getInstance(ReportWorkActivity.this).returnORG_ID());
                    param.put("auth_token",SharedPref.getInstance(ReportWorkActivity.this).returnAuthCode());

                    Log.d(TAG,"\n"+loc_lat+"\n"+loc_long+"" +
                            "\n"+username+"\n"+strWorkReport+"\n"
                            +SharedPref.getInstance(ReportWorkActivity.this).returnAuthCode()+"\n"+
                            SharedPref.getInstance(ReportWorkActivity.this).returnORG_ID());
                    return param;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(request);

    }
    private void hide_keyboard(){
        LinearLayout mainLayout;
        mainLayout = findViewById(R.id.myLinearLayout);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 121){
            LocationTracker.isGpsEnable(this);
        }
    }

}
