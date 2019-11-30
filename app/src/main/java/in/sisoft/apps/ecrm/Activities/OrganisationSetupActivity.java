package in.sisoft.apps.ecrm.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import in.sisoft.apps.ecrm.Location.LocationTracker;
import in.sisoft.apps.ecrm.R;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;

public class OrganisationSetupActivity extends AppCompatActivity implements View.OnClickListener {

    EditText ed_org_code,ed_org_code_conform;
    Button btn_submit;
    String org= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisation_setup);

        org = getIntent().getStringExtra("org");
        LocationTracker.isGpsEnable(this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        init();
    }

    private void init() {
        //init widgets
        ed_org_code = findViewById(R.id.ed_org_code);
        ed_org_code_conform = findViewById(R.id.ed_org_code_conform);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int _id = v.getId();

        if (_id==R.id.btn_submit){
            if(LocationTracker.isGpsEnable(this)){
                if(conformCode()){
                    SharedPref.getInstance(OrganisationSetupActivity.this)
                            .storeOrganisationId(ed_org_code.getText().toString());
                    Toast.makeText(this, "success!", Toast.LENGTH_SHORT).show();
                    if(org.equals("0")){
                        Intent intent = new Intent(this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else onBackPressed();

                }
            }

        }
    }

    private boolean conformCode(){

        String code = ed_org_code.getText().toString().trim();
        String code_confirm = ed_org_code_conform.getText().toString().trim();

        if(code.equals("") || code_confirm.equals("")){
            Toast.makeText(this, "fields are blanks !", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(!code.equals(code_confirm)){
            Toast.makeText(this, "Code not match!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;

    }

}
