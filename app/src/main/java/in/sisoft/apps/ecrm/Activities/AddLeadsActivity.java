package in.sisoft.apps.ecrm.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.Location.LocationTracker;
import in.sisoft.apps.ecrm.Location.MyPosition;
import in.sisoft.apps.ecrm.R;
import in.sisoft.apps.ecrm.RequestHandler;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;


public class AddLeadsActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout layout_name;
    TextInputLayout layout_number;
    TextInputLayout layout_email_id;
    TextInputLayout layout_course_product;
    TextInputLayout layout_company_name;
    TextInputLayout layout_street_name;
    TextInputLayout layout_sector;
    TextInputLayout layout_market;

    EditText ed_name;
    EditText ed_number;
    EditText ed_email_id;
    EditText ed_course_product;
    EditText ed_company_name;
    EditText ed_street_name;
    EditText ed_sector;
    EditText ed_market;

    Spinner spn_query_source;
    Spinner spn_query_type;
    Spinner spn_city;
    Spinner spn_district;

    TextView tv_date;
    TextView tv_time;
    Button   btn_add_lead;


    private static final String TAG = "AddLeadActivity";

    private int year, month, day;
    private int hour, minute;
    String strName, strEmail_id, strCourse_product, strCompany_name, strStreet_name, strSector,
            strNumber, strMarket, strDate, strTime,
            strQuery_source = "Website",
            strQuery_type = "Training",
            strCity = "Indirapuram",
            strDistrict = "Ghaziabad";

    Calendar myCalendar;
    private Pattern pattern;
    private double loc_lat, loc_long;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_leads);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(R.string.title_my_leads);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        pattern = Pattern.compile(AppGlobal.EMAIL_PATTERN);
        MyPosition position = new MyPosition(this);
        position.getDeviceLocation();
        position.setUpListener((loc_lat1, loc_long1) -> {
            loc_lat = loc_lat1;
            loc_long = loc_long1;
        });

        if(getIntent().getStringExtra("launch_origin").equalsIgnoreCase("incoming")){
            final Dialog dg;
            dg = new Dialog(this);
            dg.setContentView(R.layout.dialog);

            dg.setCancelable(false);
            dg.setCanceledOnTouchOutside(false);

            Button b1 = dg.findViewById(R.id.btn1);
            Button b2 = dg.findViewById(R.id.btn2);

            b1.setOnClickListener(v -> {

                ed_number.setText(getIntent().getStringExtra("phoneNo"));
                dg.cancel();
            });

            b2.setOnClickListener(v -> {
                finish();
                dg.cancel();
            });

            dg.show();
        }

        initializeView();
        spinnerSetup();
        setupDateAndTimePicker();
        getStringFromSpinner();


    }

    @Override
    protected void onResume() {
        super.onResume();

        //testing
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);


        // not working error :- not reaching on onLocationChanged

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private boolean validationEditText() {
        boolean result = true;

        // name
        String name = ed_name.getText().toString();
        if (name.equals("") || name.length() < 3) {
            layout_name.setError("Name can't be blank or not less than 3 letter");
            result = false;
        }
        else
            layout_name.setErrorEnabled(false);

        //number
        String number = ed_number.getText().toString();
        if (number.equals("")) {
            layout_number.setError("number can't be blank");
            result = false;
        }
        else if (number.length()<10){
            layout_number.setError("number length less than 10");
            result = false;
        }
        else
            layout_number.setErrorEnabled(false);


        //email_id
        String email = ed_email_id.getText().toString();
        if (email.equals("") || !validateEmail(email)) {
            layout_email_id.setError("email format incorrect");
            result = false;
        }
        else
            layout_email_id.setErrorEnabled(false);



        //course_product
        String course_product = ed_course_product.getText().toString();
        if (course_product.equals("")) {
            layout_course_product.setError("course product can't be blank");
            result = false;
        }
        else
            layout_course_product.setErrorEnabled(false);

        //company_name
        String company_name = ed_company_name.getText().toString();
        if (company_name.equals("")) {
            layout_company_name.setError("company name can't be blank");
            result = false;
        }
        else
            layout_company_name.setErrorEnabled(false);

        //street_name
        String street_name = ed_street_name.getText().toString();
        if (street_name.equals("")) {
            layout_street_name.setError("street can't be blank");
            result = false;
        }
        else
            layout_street_name.setErrorEnabled(false);
        //sector
        String sector = ed_sector.getText().toString();
        if (sector.equals("")) {
            layout_sector.setError("sector can't be blank");
            result = false;
        }
        else
            layout_sector.setErrorEnabled(false);

        //market
        String market = ed_market.getText().toString();
        if (market.equals("")) {
            layout_market.setError("market can't be blank");
            result = false;
        }
        else
            layout_market.setErrorEnabled(false);


        return result;

    }

    public boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void setupDateAndTimePicker() {

        //current date
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String current_date = mdformat.format(calendar.getTime());
        tv_date.setText(current_date);
        strDate = tv_date.getText().toString();

        //current time
        Date d=new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        String currentDateTimeString = sdf.format(d);
        tv_time.setText(currentDateTimeString);
        strTime = tv_time.getText().toString();


        myCalendar = Calendar.getInstance();
        year= myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        minute = myCalendar.get(Calendar.MINUTE);
    }
    private void spinnerSetup() {

        ArrayAdapter<CharSequence> adapter_query_source =  ArrayAdapter.createFromResource(this,
                R.array.array_spn_query_source,R.layout.spinner_layout);
        adapter_query_source.setDropDownViewResource(R.layout.spinner_layout);
        spn_query_source.setAdapter(adapter_query_source);


        ArrayAdapter<CharSequence> adapter_query_type =  ArrayAdapter.createFromResource(this,
                R.array.array_spn_query_type,R.layout.spinner_layout);
        adapter_query_type.setDropDownViewResource(R.layout.spinner_layout);
        spn_query_type.setAdapter(adapter_query_type);

        ArrayAdapter<CharSequence> adapter_district =  ArrayAdapter.createFromResource(this,
                R.array.array_spn_district,R.layout.spinner_layout);
        adapter_district.setDropDownViewResource(R.layout.spinner_layout);
        spn_district.setAdapter(adapter_district);

        ArrayAdapter<CharSequence> adapter_city =  ArrayAdapter.createFromResource(this,
                R.array.array_spn_city,R.layout.spinner_layout);
        adapter_city.setDropDownViewResource(R.layout.spinner_layout);
        spn_city.setAdapter(adapter_city);

    }
    @SuppressLint("ClickableViewAccessibility")
    private void initializeView() {

         layout_name = findViewById(R.id.layout_name);
         layout_number = findViewById(R.id.layout_number);
         layout_email_id = findViewById(R.id.layout_email_id);
         layout_course_product = findViewById(R.id.layout_course_product);
         layout_company_name = findViewById(R.id.layout_company_name);
         layout_street_name = findViewById(R.id.layout_street_name);
         layout_sector = findViewById(R.id.layout_sector);
         layout_market = findViewById(R.id.layout_market);

         ed_name = findViewById(R.id.ed_name);
         ed_number = findViewById(R.id.ed_number);
         ed_email_id = findViewById(R.id.ed_email_id);
         ed_course_product = findViewById(R.id.ed_course_product);
         ed_company_name = findViewById(R.id.ed_company_name);
         ed_street_name = findViewById(R.id.ed_street_name);
         ed_sector = findViewById(R.id.ed_sector);
         ed_market = findViewById(R.id.ed_market);

         spn_query_source = findViewById(R.id.spn_query_source);
         spn_query_type = findViewById(R.id.spn_query_type);
         spn_city = findViewById(R.id.spn_city);
         spn_district = findViewById(R.id.spn_district);

         tv_date = findViewById(R.id.tv_date);
         tv_date.setOnClickListener(this);
         tv_time = findViewById(R.id.tv_time);
         tv_time.setOnClickListener(this);

         btn_add_lead = findViewById(R.id.btn_add_lead);
         btn_add_lead.setOnClickListener(this);


         if(getIntent().getStringExtra("launch_origin").equalsIgnoreCase("list_call_activity")
                 ||getIntent().getStringExtra("launch_origin").equalsIgnoreCase("incoming")){
             ed_number.setText(getIntent().getStringExtra("phoneNo"));
         }

        ed_number.setOnTouchListener((v1, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (ed_number.getRight() - ed_number.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    Intent i2 = new Intent(this,ListCallsActivity.class);
                    startActivity(i2);

                    return true;
                }
            }
            return false;
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int view_id = v.getId();

        if(view_id == R.id.tv_date){
            datePicker();
        }
        if(view_id == R.id.tv_time){
            timePicker();
        }
        if(view_id==R.id.btn_add_lead){

            if(validationEditText()){
                if (LocationTracker.isGpsEnable(this)) {
                    getStringFromEditText();
                    addLeadOperation();
                }



            }
        }


    }

    private void getStringFromSpinner() {

        spn_query_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strQuery_source = spn_query_source.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spn_query_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strQuery_type = spn_query_type.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spn_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strCity = spn_city.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spn_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strDistrict = spn_district.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getStringFromEditText() {
        strName = ed_name.getText().toString();
        strNumber = ed_number.getText().toString();
        strEmail_id = ed_email_id.getText().toString();
        strCourse_product = ed_course_product.getText().toString();
        strCompany_name = ed_company_name.getText().toString();
        strStreet_name = ed_street_name.getText().toString();
        strSector = ed_sector.getText().toString();
        strMarket = ed_market.getText().toString();

    }

    private void timePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {

                    tv_time.setText(hourOfDay + ":" + minute);
                    strTime = tv_time.getText().toString();
                }, hour
                , minute,
                true);
        timePickerDialog.show();
    }
    private void datePicker(){

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    tv_date.setText(year + "-" + (monthOfYear + 1) + "-" +dayOfMonth );
                    strDate = tv_date.getText().toString();

                }, year, month, day);
        datePickerDialog.show();
    }

    private void addLeadOperation()
    {
       sendingBtnProperties();
        StringRequest request = new StringRequest(Request.Method.POST, AppGlobal.ADD_LEAD_URL,
                response -> {
                    Log.d("String_request","onResponse");

                    try {
                        JSONObject jObj = new JSONObject(response);
                        String status = jObj.getString("status");
                        clearEditText();
                        hide_keyboard();

                        Toast.makeText(AddLeadsActivity.this,status, Toast.LENGTH_SHORT).show();
                        sendAfterBtnProperties();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        sendAfterBtnProperties();
                        if(e.getMessage().contains("auth")){
                            AppGlobal.logoutAuthError(this);
                        }
                     }

                },
                error -> {

                    Toast.makeText(AddLeadsActivity.this, "volley error \n"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    sendAfterBtnProperties();

                }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String , String> param = new HashMap<>();


                param.put("name",strName);
                param.put("email",strEmail_id);
                param.put("phone_num",strNumber);
                param.put("qry_type",strQuery_type);
                param.put("street",strStreet_name);
                param.put("sector",strSector);
                param.put("market",strMarket);
                param.put("city",strCity);
                param.put("district",strDistrict);
                param.put("qry",strCourse_product);
                param.put("date",strDate);
                param.put("time",strTime);
                param.put("source",strQuery_source);
                param.put("comp_name",strCompany_name);
                param.put("loc_lat",String.valueOf(loc_lat));
                param.put("loc_long",String.valueOf(loc_long));
                param.put("username", SharedPref.getInstance(AddLeadsActivity.this).returnUser());
                param.put("org_code", SharedPref.getInstance(AddLeadsActivity.this).returnORG_ID());
                param.put("auth_token",SharedPref.getInstance(AddLeadsActivity.this).returnAuthCode());

                Log.d(TAG, "user : "+SharedPref.getInstance(AddLeadsActivity.this).returnUser()+"\n"+
                "org_code : "+SharedPref.getInstance(AddLeadsActivity.this).returnORG_ID()+"\nauth_code : "+
                        SharedPref.getInstance(AddLeadsActivity.this).returnAuthCode());



                return param;
            }
        };
        RequestHandler.getInstance(AddLeadsActivity.this).addToRequestQueue(request);
    }

    private void sendingBtnProperties() {
        btn_add_lead.setAlpha(0.4f);
        btn_add_lead.setClickable(false);
        btn_add_lead.setText(R.string.adding);
    }
    private void sendAfterBtnProperties() {
        btn_add_lead.setAlpha(1f);
        btn_add_lead.setClickable(true);
        btn_add_lead.setText(R.string.ADD_LEAD);
    }


    public void clearEditText(){

        ed_name.setText(R.string.blank_text);
        ed_number.setText(R.string.blank_text);
        ed_email_id.setText(R.string.blank_text);
        ed_course_product .setText(R.string.blank_text);
        ed_company_name .setText(R.string.blank_text);
        ed_street_name.setText(R.string.blank_text);
        ed_sector .setText(R.string.blank_text);
        ed_market .setText(R.string.blank_text);

    }

    private void hide_keyboard(){
        LinearLayout mainLayout;
        mainLayout = findViewById(R.id.myLinearLayout);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
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
