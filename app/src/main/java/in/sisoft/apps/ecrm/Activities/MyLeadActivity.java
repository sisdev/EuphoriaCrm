package in.sisoft.apps.ecrm.Activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.sisoft.apps.ecrm.Adapters.LeadAdapter;
import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.Location.LocationTracker;
import in.sisoft.apps.ecrm.Location.MyPosition;
import in.sisoft.apps.ecrm.Models.Lead;
import in.sisoft.apps.ecrm.R;
import in.sisoft.apps.ecrm.RequestHandler;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;

public class MyLeadActivity extends AppCompatActivity implements MyPosition.LatLongListener {


    private  static final String TAG = "MyLeadActivity";
    LeadAdapter adapter;
    //widget
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tv_no_lead_hint;
    LocationTracker locationTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lead);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("My Leads");
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        tv_no_lead_hint = findViewById(R.id.tv_no_lead_hint);

        progressBar = findViewById(R.id.progressBar);
/*
        MyPosition position = new MyPosition(this);
        position.setUpListener(this);
        position.getDeviceLocation();
*/
        //testing Location Tracker
        locationTracker = LocationTracker.getInstance();
        locationTracker.setContext(this);
        locationTracker.setActivity(this);
        locationTracker.chkPermission();
      //  locationTracker.checkGPSStatus();
        LocationTracker.isGpsEnable(this);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);

    }


    @Override
    protected void onStart() {
        super.onStart();
        Location loc = locationTracker.getLocation();
        if(loc==null){
            getData(0,0);
        }
        else {
            getData(loc.getLatitude(), loc.getLongitude());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getData(double loc_lat, double loc_long) {
        progressBar.setVisibility(View.VISIBLE);
        tv_no_lead_hint.setVisibility(View.INVISIBLE);
        final StringRequest request = new StringRequest(Request.Method.POST, AppGlobal.MY_LEAD_URL,
                response -> {

                    try {
                        JSONArray array = new JSONArray(response);
                        parseData(array);
                    } catch (JSONException e) {
                        Log.d(TAG," Exception \n"+e.getMessage());
                        progressBar.setVisibility(View.GONE);
                        tv_no_lead_hint.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (e.getMessage().contains("Incorrect")){
                            AppGlobal.logoutAuthError(this);
                        }
                    }
                }, error -> {
                    Toast.makeText(MyLeadActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    tv_no_lead_hint.setVisibility(View.INVISIBLE);

                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String , String> param = new HashMap<>();
                SharedPref.getInstance(MyLeadActivity.this);

                param.put("username", SharedPref.getInstance(MyLeadActivity.this).returnUser());
                param.put("org_code", SharedPref.getInstance(MyLeadActivity.this).returnORG_ID());
                param.put("auth_token",SharedPref.getInstance(MyLeadActivity.this).returnAuthCode());
                param.put("loc_lat", String.valueOf(loc_lat));
                param.put("loc_long", String.valueOf(loc_long));

                Log.d(TAG,"user : "+
                        SharedPref.getInstance(MyLeadActivity.this).returnUser()+"\norg_code : "+
                        SharedPref.getInstance(MyLeadActivity.this).returnORG_ID()+"\nauth_code : "+
                        SharedPref.getInstance(MyLeadActivity.this).returnUser()+"\n"+loc_lat+"\n"+loc_long);
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
                Log.d(TAG,""+e.getMessage());
            }
        }
        adapter = new LeadAdapter(MyLeadActivity.this,AppGlobal.leadList);
        recyclerView.setAdapter(adapter);
        if(adapter.getItemCount()>0){
            tv_no_lead_hint.setVisibility(View.INVISIBLE);
        }
        else tv_no_lead_hint.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Total leads : "+adapter.getItemCount(), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lead_search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                return false;
            }
        });
        return true;
    }

    @Override
    public void onLatLogClick(double loc_lat, double loc_long) {
        getData(loc_lat,loc_long);
    }


}
