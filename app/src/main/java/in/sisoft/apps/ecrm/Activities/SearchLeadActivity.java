package in.sisoft.apps.ecrm.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.sisoft.apps.ecrm.Adapters.SearchLeadAdapter;
import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.Location.LocationTracker;
import in.sisoft.apps.ecrm.Models.Lead;
import in.sisoft.apps.ecrm.R;
import in.sisoft.apps.ecrm.RequestHandler;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;

public class SearchLeadActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton search_lead;
    public double loc_lat,loc_long;
    private static final String TAG = "SearchLeadActivity";
    private int adapterCount =0;
    private Dialog dialog;

    private TextView tv_no_search_hint ;
    LocationTracker locationTracker;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lead);

        tv_no_search_hint = findViewById(R.id.tv_no_search_hint);

        progressBar = findViewById(R.id.progressBar);
        search_lead = findViewById(R.id.search_lead);
        search_lead.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //

/*
        MyPosition position = new MyPosition(this);
        position.getDeviceLocation();
        position.setUpListener((loc_lat1, loc_long1) -> {
            loc_lat = loc_lat1;
            loc_long = loc_long1;
            searchDialog();
        });
*/



        locationTracker = LocationTracker.getInstance();
        locationTracker.setContext(this);
        locationTracker.setActivity(this);
        locationTracker.chkPermission();
        LocationTracker.isGpsEnable(this);

        searchDialog();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {

        int v_id= v.getId();
        if(v_id==R.id.search_lead){
            searchDialog();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void searchDialog() {
        locationTracker.startLocationUpdate();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_search_lead);
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout linearLayout = dialog.findViewById(R.id.layout);
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


        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> {

            if((adapterCount>0)){
                tv_no_search_hint.setVisibility(View.INVISIBLE);
            }
            else {
                tv_no_search_hint.setVisibility(View.VISIBLE);
            }
            dialog.dismiss();
            locationTracker.stopLocationUpdate();

        });

        Button search_lead = dialog.findViewById(R.id.search_lead);
        search_lead.setOnClickListener(v -> {

            EditText edProduct_course = dialog.findViewById(R.id.product_course);
            EditText edPerson_name = dialog.findViewById(R.id.person_name);
            EditText edCompany_name = dialog.findViewById(R.id.company_name);

            String strProductCourse = edProduct_course.getText().toString();
            String strPersonName = edPerson_name.getText().toString();
            String strCompanyName = edCompany_name.getText().toString();
            if(strProductCourse.equals("") && strPersonName.equals("") && strCompanyName.equals(""))
            {
                Toast.makeText(this, "1 field is compulsory!", Toast.LENGTH_SHORT).show();
            }
            else {

                    getData(strProductCourse,strPersonName,strCompanyName);
                    dialog.dismiss();
                    locationTracker.stopLocationUpdate();
                }



        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(adapterCount>0){
                    tv_no_search_hint.setVisibility(View.INVISIBLE);
                }
                else {
                    tv_no_search_hint.setText("Press search icon to make a search");
                    tv_no_search_hint.setVisibility(View.VISIBLE);
                }
            }
        });

        dialog.show();
    }

    public void getData(String strProductCourse, String strPersonName, String strCompanyName) {
        progressBar.setVisibility(View.VISIBLE);
        tv_no_search_hint.setVisibility(View.INVISIBLE);
        search_lead.setAlpha(0.4f);
        search_lead.setClickable(false);
        final StringRequest request = new StringRequest(Request.Method.POST, AppGlobal.MY_SEARCH_LEAD_URL,
                response -> {
                    try {
                        tv_no_search_hint.setVisibility(View.INVISIBLE);
                        JSONArray array = new JSONArray(response);
                        parseData(array);
                    } catch (JSONException e) {
                        tv_no_search_hint.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG," Exception \n"+e.getMessage());
                        Toast.makeText(this, " Exception"+ "\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (e.getMessage().contains("No")){
                            AppGlobal.logoutAuthError(this);
                        }
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    search_lead.setAlpha(1f);
                    search_lead.setClickable(true);
                }, error -> {

            tv_no_search_hint.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            search_lead.setAlpha(1f);
            search_lead.setClickable(true);
            Toast.makeText(SearchLeadActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();


        }){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String , String> param = new HashMap<>();
                Location loc = locationTracker.getLocation();
                if(loc == null){
                    loc_lat=0;
                    loc_long=0;
                }
                else {
                    loc_lat = loc.getLatitude();
                    loc_long = loc.getLongitude();
                }

                param.put("product_name",strProductCourse);
                param.put("contact_name",strPersonName);
                param.put("company_name",strCompanyName);
                param.put("org_code", SharedPref.getInstance(SearchLeadActivity.this).returnORG_ID());
                param.put("username", SharedPref.getInstance(SearchLeadActivity.this).returnUser());
                param.put("auth_token",SharedPref.getInstance(SearchLeadActivity.this).returnAuthCode());
                param.put("loc_lat", String.valueOf(loc_lat));
                param.put("loc_long", String.valueOf(loc_long));

                Log.d(TAG,"\n"+""+strProductCourse+"\n"+strPersonName+"\n"+strCompanyName+
                        "\n"+loc_lat+"\n"+loc_long+"\norg_code: "+
                        SharedPref.getInstance(SearchLeadActivity.this).returnORG_ID()+"\nauth_token : "
                        +SharedPref.getInstance(SearchLeadActivity.this).returnAuthCode()+"\nusername : "+
                SharedPref.getInstance(SearchLeadActivity.this).returnUser());
                return param;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
    }


    private void parseData(JSONArray array) {

        AppGlobal.leadList.clear();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);

                String id = String.valueOf(jsonObject.getInt("id"));
                String qry_source = jsonObject.getString("qry_source");
                String name = jsonObject.getString("name");
                String address = jsonObject.getString("address");
                String street = jsonObject.getString("add_street");
                String sector = jsonObject.getString("add_sector");
                String market = jsonObject.getString("add_market");
                String phone = jsonObject.getString("phone_no");
                String city = jsonObject.getString("add_city");
                String qry_type = jsonObject.getString("qry_type");
                String district = jsonObject.getString("add_distict");
                String email = jsonObject.getString("emailID");
                String qry_details = jsonObject.getString("qry_details");
                String qry_status = jsonObject.getString("qry_status");
                String company_name = jsonObject.getString("comp_name");
                String req_dtm = jsonObject.getString("req_dtm");

                Lead lead = new Lead(id,qry_source,name,address,street,sector,market,
                        phone,city,qry_type,district,email,qry_details,qry_status,company_name,req_dtm);
                AppGlobal.leadList.add(lead);

            } catch (JSONException e) {
                e.printStackTrace();


            }
        }
        SearchLeadAdapter adapter = new SearchLeadAdapter(SearchLeadActivity.this, AppGlobal.leadList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapterCount = adapter.getItemCount();
        if(adapter.getItemCount()>0){
            tv_no_search_hint.setVisibility(View.INVISIBLE);
        }
        else tv_no_search_hint.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Total leads : "+ adapter.getItemCount(), Toast.LENGTH_SHORT).show();

    }



    @Override
    public void onBackPressed() {

        super.onBackPressed();
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