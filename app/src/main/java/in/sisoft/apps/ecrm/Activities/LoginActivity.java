package in.sisoft.apps.ecrm.Activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.Location.LocationTracker;
import in.sisoft.apps.ecrm.R;
import in.sisoft.apps.ecrm.Receivers.LogoutReceiver;
import in.sisoft.apps.ecrm.RequestHandler;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener/*,
        MyPosition.LatLongListener*/{

    //widgets
    EditText ed_username;
    EditText ed_password;
    Button btn_login;
    Button btn_org_setup;
    Button btn_about_app;
    TextView tv_org_setup;
    TextView tv_about_app;

    LocationTracker locationTracker;

    //static
    private static final int ERROR_DIALOG_REQUEST =1;
    private static final String TAG ="LoginActivity";



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams
                .FLAG_FULLSCREEN);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().hide();
        }


        locationTracker = LocationTracker.getInstance();
        locationTracker.setContext(this);
        locationTracker.setActivity(this);
        locationTracker.chkPermission();

        LocationTracker.isGpsEnable(this);


        final String strLogin = SharedPref.getInstance(this).returnUser();
        initializeView();

        if(SharedPref.getInstance(this).returnORG_ID().equals("")){
                    Intent intent = new Intent(this,OrganisationSetupActivity.class);
                    intent.putExtra("org","0");
                    startActivity(intent);
                    finish();
        }

        if (!strLogin.equals("")){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
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

    private void initializeView() {
        ed_password = findViewById(R.id.ed_password);
        ed_username = findViewById(R.id.ed_username);
        ed_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed_username.getText().toString().contains(" ")) {
                    ed_username.setError("username should not contain space");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_org_setup = findViewById(R.id.btn_setting);
        btn_org_setup.setOnClickListener(this);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_about_app = findViewById(R.id.btn_about_app);
        btn_about_app.setOnClickListener(this);
        tv_about_app = findViewById(R.id.tv_about_app);
        tv_org_setup=findViewById(R.id.tv_org_code);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_login){

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


            hide_keyboard();
            if(LocationTracker.isGpsEnable(this)){
                    String username = ed_username.getText().toString();
                    String password = ed_password.getText().toString();
                    if (!username.equals("") && !password.equals(""))
                    {

                        if (!username.contains(" "))
                        {
                            loginOperation(username, password,loc_lat,loc_long);
                        }
                        else
                            Toast.makeText(this, "username should not contain space", Toast.LENGTH_SHORT).show();
                    }
                    else

                        Toast.makeText(this, "username and password can't be blank", Toast.LENGTH_SHORT).show();
                }

        }

        if(v.getId() == R.id.btn_setting){
            Intent intent = new Intent(this,OrganisationSetupActivity.class);
            intent.putExtra("org","1");
            startActivity(intent);


        }
        if(v.getId()==R.id.btn_about_app){

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_about_app);
            dialog.setCanceledOnTouchOutside(false);
            ScrollView linearLayout = dialog.findViewById(R.id.layout);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int widthLcl = (int) (displayMetrics.widthPixels*0.85f);
            int heightLcl = (int) (displayMetrics.heightPixels*0.7f);
            FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
            paramsLcl.width = widthLcl;
            paramsLcl.height =heightLcl ;
            paramsLcl.gravity = Gravity.CENTER;
            linearLayout .setLayoutParams(paramsLcl);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // dialog.getWindow().getAttributes().windowAnimations =R.style.DialogTheme2;
            dialog.show();


        }

    }

    private void loginOperation(final String username, final String password, double loc_lat, double loc_long) {
        LoginWait();
        setEditextDisable();
        StringRequest request = new StringRequest(Request.Method.POST, AppGlobal.LOGIN_URL,
                response -> {

            Log.d(TAG,response);
                    buttonAd();

                    try {
                        JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("Status_Code");
                            String message = jObj.getString("Login_Msg");
                            String role = jObj.getString("role");


                            if(status.equals("1"))
                            {
                                setLogoutTime();
                                SharedPref.getInstance(this).storeUser(username);
                                String auth_token = jObj.getString("auth_token");
                                SharedPref.getInstance(this).storeAuthCode(auth_token);
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                Log.d(TAG,"login message : "+message+"\n"+
                                auth_token);
                            }
                            else {
                                Toast.makeText(LoginActivity.this,""+message, Toast.LENGTH_SHORT).show();
                                setEditextEnable();
                            }
                    } catch (JSONException e) {
                        setEditextEnable();
                        Toast.makeText(getApplicationContext(), "Json Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }, error -> {
                    buttonAd();
                    Toast.makeText(LoginActivity.this, "json Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    setEditextEnable();
                }){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String , String> param = new HashMap<>();

                param.put("username",username);
                param.put("password",password);
                param.put("loc_lat",String.valueOf(loc_lat));
                param.put("loc_long",String.valueOf(loc_long));
                param.put("org_code",SharedPref.getInstance(LoginActivity.this).returnORG_ID());


                Log.d(TAG,"\n"+loc_lat+"\n"+loc_long+"\n"+username+"\n"+
                        password+"\norg_code : "+SharedPref.getInstance(LoginActivity.this).returnORG_ID());
                return param;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
    }
    private void buttonAd() {

        btn_login.setText("LOGIN");
        btn_login.setClickable(true);
        btn_login.setAlpha(1f);
        btn_about_app.setClickable(true);
        btn_about_app.setAlpha(1f);
        btn_org_setup.setClickable(true);
        btn_org_setup.setAlpha(1f);
        tv_org_setup.setAlpha(1f);
        tv_about_app.setAlpha(1f);
        Log.d("String_request","onResponse");

    }
    private void hide_keyboard(){
        ScrollView mainLayout;

        mainLayout = findViewById(R.id.myLinearLayout);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
        ///
    }
    public boolean isServicesOK(){
        Log.d(TAG,"isServicesOK : checking google service version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(available== ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK : google play service is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServicesOK : an error occur but can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity
                    .this,available,ERROR_DIALOG_REQUEST);
            dialog.show();

        }
        else {
            Toast.makeText(this, "sorry , we can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void setEditextDisable(){
        ed_username.setFocusable(false);
        ed_username.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        ed_username.setClickable(false); // user navigates with wheel and selects widget

        ed_password.setFocusable(false);
        ed_password.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        ed_password.setClickable(false); // user navigates with wheel and selects widget

        ed_username.setAlpha(0.5f);
        ed_password.setAlpha(0.5f);

}
    private void setEditextEnable(){
        ed_username.setFocusable(true);
        ed_username.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
        ed_username.setClickable(true); // user navigates with wheel and selects widget

        ed_password.setFocusable(true);
        ed_password.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
        ed_password.setClickable(true); // user navigates with wheel and selects widget

        ed_username.setAlpha(1f);
        ed_password.setAlpha(1f);
        ///

    }
    private void LoginWait() {
        btn_login.setText(R.string.please_wait_login);
        btn_login.setClickable(false);
        btn_login.setAlpha(0.7f);
        //
        btn_about_app.setClickable(false);
        btn_about_app.setAlpha(0.7f);
        btn_org_setup.setClickable(false);
        btn_org_setup.setAlpha(0.7f);
        tv_org_setup.setAlpha(0.7f);
        tv_about_app.setAlpha(0.7f);

    }
    private void setLogoutTime(){
        Intent intent = new Intent(this, LogoutReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC,System.currentTimeMillis() + (72*60*60*1000),
                pendingIntent);
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
