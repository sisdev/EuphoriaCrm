package in.sisoft.apps.ecrm.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.Objects;

import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.R;

public class ViewLeadActivity extends AppCompatActivity {

    int position;

    //widgets
    private TextView qry_source;
    private TextView name;
    private TextView address;
    private TextView street;
    private TextView sector;
    private TextView market;
    private TextView phone;
    private TextView city;
    private TextView qry_type;
    private TextView district;
    private TextView email;
    private TextView qry_details;
    private TextView qry_status;
    private TextView company_name;
    private TextView req_dtm;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lead);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(R.string.title_lead_detail);
        toolbar.setNavigationOnClickListener(v -> {
          onBackPressed();
        });

            position = Objects.requireNonNull(getIntent().getExtras()).getInt("position");
            initializeWidgets();
            setText();

    }

    private void setText() {

        qry_source.setText(AppGlobal.leadList.get(position).getQry_source());
        name.setText(AppGlobal.leadList.get(position).getName());
        address.setText(AppGlobal.leadList.get(position).getAddress());
        street.setText(AppGlobal.leadList.get(position).getStreet());
        sector.setText(AppGlobal.leadList.get(position).getSector());
        market.setText(AppGlobal.leadList.get(position).getMarket());
        phone.setText(AppGlobal.leadList.get(position).getPhone());
        city.setText(AppGlobal.leadList.get(position).getCity());
        qry_type.setText(AppGlobal.leadList.get(position).getQry_type());
        district.setText(AppGlobal.leadList.get(position).getDistrict());
        email.setText(AppGlobal.leadList.get(position).getEmail());
        qry_details.setText(AppGlobal.leadList.get(position).getQry_details());
        qry_status.setText(AppGlobal.leadList.get(position).getQry_status());
        company_name.setText(AppGlobal.leadList.get(position).getCompany_name());
        req_dtm.setText(AppGlobal.leadList.get(position).getReq_dtm());
    }

    private void initializeWidgets() {

        qry_source = findViewById(R.id.query_source);
        qry_type = findViewById(R.id.query_type);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        street = findViewById(R.id.street);
        sector = findViewById(R.id.sector);
        market = findViewById(R.id.market);
        phone = findViewById(R.id.phone_no);
        city = findViewById(R.id.city);
        district = findViewById(R.id.district);
        email = findViewById(R.id.email_id);
        qry_details = findViewById(R.id.course_product);
        qry_status = findViewById(R.id.status);
        company_name = findViewById(R.id.company_name);
        req_dtm = findViewById(R.id.date_time);

    }
}