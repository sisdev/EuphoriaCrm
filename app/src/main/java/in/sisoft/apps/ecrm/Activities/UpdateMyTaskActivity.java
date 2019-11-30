package in.sisoft.apps.ecrm.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.GetMyTaskData;
import in.sisoft.apps.ecrm.Location.LocationTracker;
import in.sisoft.apps.ecrm.R;
import in.sisoft.apps.ecrm.RequestHandler;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;


public class UpdateMyTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UpdateMyTaskActivity";
    //widgets
    private Spinner spinner_task_type;
    private Spinner spinner_status;
    private TextView tv_date;
    private TextView tv_time;
    private EditText ed_output;
    private EditText ed_name;
    private EditText ed_narration;
    private TextInputLayout textInputLayout_name;
    private TextInputLayout textInputLayout_narration;
    private Button btn_update_task;
    //classes

    private int position;
    private String strTaskType = "Call";
    private String strStatus = "New";
    private String strDate;
    private String strTime;
    private int minute;
    private int hour;

    LocationTracker locationTracker;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_my_task);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Update Task");
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        position = Objects.requireNonNull(getIntent().getExtras()).getInt("position");

        setUpWidgets();
        setupSpinner();

        //setting up date and time
        String datetime = AppGlobal.taskList.get(position).getTaskDateTime();
        String pre_date = datetime.substring(0,10);
        tv_date.setText(pre_date);
        strDate = pre_date;

        String pre_time = datetime.substring(11);
        tv_time.setText(pre_time);
        strTime = pre_time;


        if(strStatus.equals("New")){
            ed_output.setVisibility(View.GONE);
        }
        else {
            ed_output.setVisibility(View.VISIBLE);
        }

        locationTracker = LocationTracker.getInstance();
        locationTracker.setContext(this);
        locationTracker.setActivity(this);
        locationTracker.chkPermission();
        //locationTracker.checkGPSStatus();
        LocationTracker.isGpsEnable(this);

    }
    private void setUpWidgets() {

        tv_date = findViewById(R.id.tv_date);
        tv_date.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        ed_output= findViewById(R.id.ed_output);
        ed_name = findViewById(R.id.ed_name);
        ed_name.setText(AppGlobal.taskList.get(position).getName());
        ed_narration = findViewById(R.id.ed_narration);
        ed_narration.setText(AppGlobal.taskList.get(position).getNarration());
        textInputLayout_name = findViewById(R.id.layout_name);
        textInputLayout_narration = findViewById(R.id.layout_narration);
        btn_update_task = findViewById(R.id.btn_update_task);
        btn_update_task.setOnClickListener(this);


    }
    private void setupSpinner() {

        //spinner task type
        spinner_task_type = findViewById(R.id.spn_task_type);
        ArrayAdapter<CharSequence> adapter_task_task =  ArrayAdapter.createFromResource(this,
                R.array.array_spn_task_type,R.layout.spinner_layout);
        adapter_task_task.setDropDownViewResource(R.layout.spinner_layout);
        spinner_task_type.setAdapter(adapter_task_task);

        spinner_task_type.setSelection(getSelectedPosition());
        spinner_task_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strTaskType = spinner_task_type.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //spinner status

        spinner_status = findViewById(R.id.spn_status);
        ArrayAdapter<CharSequence> adapter_status =  ArrayAdapter.createFromResource(this,
                R.array.array_spn_status,R.layout.spinner_layout);
        adapter_status.setDropDownViewResource(R.layout.spinner_layout);
        spinner_status.setAdapter(adapter_status);

        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strStatus = spinner_status.getSelectedItem().toString();
                if(!strStatus.equals("New"))
                { ed_output.setVisibility(View.VISIBLE);
                }
                else {
                    ed_output.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void timePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {

                    ////////
                    String hour1;
                    String minute1;

                    if(hourOfDay<10){
                        hour1 = "0"+hourOfDay;
                    }
                    else hour1 = String.valueOf(hourOfDay);

                    ////
                    if(minute<10){
                        minute1 = "0"+(minute);
                    }
                    else   minute1 =String.valueOf(minute);

                    ///////


                    tv_time.setText(hour1 + ":" + minute1);
                    strTime = tv_time.getText().toString();
                }, hour
                , minute,
                true);
        timePickerDialog.show();
    }
    private void datePicker(){


        Calendar myCalendar = Calendar.getInstance();


        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH);
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year12, monthOfYear, dayOfMonth) -> {
                   ///////////

                    String day1;
                    String month1;
                    String year1;
                    if(dayOfMonth<10){
                        day1 = "0"+dayOfMonth;
                    }
                    else day1 = String.valueOf(dayOfMonth);


                    if(monthOfYear<10){
                        month1 = "0"+(monthOfYear+1);
                    }
                    else   month1 =String.valueOf(monthOfYear+ 1);

                    year1 = String.valueOf(year12);
                    tv_date.setText(year1 + "-" + (month1) + "-" +day1 );
                    strDate = tv_date.getText().toString();

                    //////////

                }, year, month, day);
        datePickerDialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if(_id==R.id.tv_date){
            datePicker();
        }
        if(_id == R.id.tv_time){
            timePicker();
        }
        if(_id == R.id.btn_update_task){
            updateOperation();


        }
    }
    private void updateOperation() {
        Location loc = locationTracker.getLocation();
        double loc_lat;
        double loc_long;
        if(loc==null){
            loc_lat=0;
            loc_long=0;
        }
        else {
             loc_lat = loc.getLatitude();
             loc_long = loc.getLongitude();
        }


       if( textInputValidation()){

           String update_id = AppGlobal.taskList.get(position).getId();
           String trng_query_id = AppGlobal.taskList.get(position).getLeadQryId();
           String user = SharedPref.getInstance(this).returnUser();
           String narration = ed_narration.getText().toString();
           String output = ed_output.getText().toString();

           sendingBtnProperties();
           StringRequest request = new StringRequest(Request.Method.POST, AppGlobal.MY_TASK_UPDATE_URL,
                   response ->

                   {

                       try {
                           JSONObject jObj = new JSONObject(response);
                           String message = jObj.getString("status");
                           Toast.makeText(UpdateMyTaskActivity.this,message, Toast.LENGTH_SHORT).show();
                           if(message.contains("Successfully!")){
                               GetMyTaskData data = new GetMyTaskData(UpdateMyTaskActivity.this);
                               data.getData(loc_lat,loc_long);


                           }
                           sendAfterBtnProperties();


                       } catch (JSONException e) {

                           Toast.makeText(getApplicationContext(), "Json Exception:\n " + e.getMessage(), Toast.LENGTH_LONG).show();
                            sendAfterBtnProperties();
                           if (e.getMessage().contains("No")){
                               AppGlobal.logoutAuthError(this);
                           }
                       }

                   }, error -> {
               Toast.makeText(UpdateMyTaskActivity.this, "Volley Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
               sendAfterBtnProperties();
           }){

               @Override
               protected Map<String, String> getParams() throws AuthFailureError {
                   HashMap<String , String> param = new HashMap<>();
                   param.put("id",update_id);
                   param.put("username",user);
                   param.put("trng_query_id",trng_query_id);
                   param.put("narration",narration);
                   param.put("task_type",strTaskType);
                   param.put("status",strStatus);
                   param.put("date",strDate);
                   param.put("time",strTime);
                   param.put("output",output);
                   param.put("org_code",SharedPref.getInstance(UpdateMyTaskActivity.this).returnORG_ID());
                   param.put("auth_token",SharedPref.getInstance(UpdateMyTaskActivity.this).returnAuthCode());
                   param.put("loc_lat", String.valueOf(loc_lat));
                   param.put("loc_long", String.valueOf(loc_long));
                   return param;
               }
           };
           RequestHandler.getInstance(this).addToRequestQueue(request);


           Log.d(TAG,update_id+"\n"+user+"\n"+narration+"\n"+
                   strTaskType+"\n"+strStatus+"\n"+strDate+"\n"+strTime+"\n"+output+"\n"+trng_query_id+"\n"+loc_lat+
                   "\n"+loc_long+"\n"+SharedPref.getInstance(UpdateMyTaskActivity.this).returnORG_ID()+"\n"+
                   SharedPref.getInstance(UpdateMyTaskActivity.this).returnAuthCode());



       }
    }
    private void sendingBtnProperties() {
        btn_update_task.setAlpha(0.4f);
        btn_update_task.setClickable(false);
        btn_update_task.setText(R.string.updating);
    }
    private void sendAfterBtnProperties() {
        btn_update_task.setAlpha(1f);
        btn_update_task.setClickable(true);
        btn_update_task.setText(R.string.TASK_UPDATE);
    }
    private boolean textInputValidation() {
        boolean result=true;
        String name = ed_name.getText().toString();
        if(name.equals("")){
            textInputLayout_name.setError("Name can't be empty");
            result = false;
        }
        else {
            textInputLayout_name.setErrorEnabled(false);
        }
        //narration
        String narration = ed_narration.getText().toString();
        if(narration.equals("")){
            textInputLayout_narration.setError("Narration can't be empty");
            result = false;
        }
        else {
            textInputLayout_narration.setErrorEnabled(false);
        }

    return result;
    }
    private int getSelectedPosition(){

        String selectedText = AppGlobal.taskList.get(position).getTaskType();
        if(selectedText.equalsIgnoreCase("Call")){
            return 0;
        }
        else if(selectedText.equalsIgnoreCase("Meeting")){
            return 1;
        }
        else if (selectedText.equalsIgnoreCase("Demo")){
            return 2;
        }
        else {
            return 3;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 121){
           // locationTracker.checkGPSStatus();
            locationTracker.isGpsEnable(this);
          //  CheckGpsStatus.isGpsEnable(this);
        }
    }


}
