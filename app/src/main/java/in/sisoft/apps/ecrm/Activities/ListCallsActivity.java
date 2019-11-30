package in.sisoft.apps.ecrm.Activities;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import in.sisoft.apps.ecrm.Adapters.CallCursorAdapter;
import in.sisoft.apps.ecrm.R;

public class ListCallsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    ListView lv;
    Cursor managedCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calls);
        lv = (ListView) findViewById(R.id.listView);
        getCallDetails();
    }

    private void getCallDetails() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_CALL_LOG"}, 10);
            Toast.makeText(this, "Missing Permission", Toast.LENGTH_LONG).show();
            return;
        }
        setupData();
    }

    private void setupData() {
        ContentResolver cr = getContentResolver();

/*
        String[] strFields = {
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.DURATION,
        };*/

        String strOrder = CallLog.Calls.DATE + " DESC";

        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DAY_OF_MONTH, -2);
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        String fromDate = String.valueOf(cal1.getTimeInMillis());

        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_MONTH, 1);
        cal2.set(Calendar.HOUR, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        String toDate = String.valueOf(cal2.getTimeInMillis());
        String[] whereValue = {fromDate, toDate};

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "permission is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null,
                CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue, strOrder);
        CallCursorAdapter cca = new CallCursorAdapter(this,managedCursor,0);
        lv.setAdapter(cca);
        lv.setOnItemClickListener(this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case 10:

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();
                        setupData();
                    }

                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (SecurityException ex) {
            Log.d("Call Log", ex.getMessage());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        managedCursor.moveToFirst();
        managedCursor.move(position);
        String strPhone = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.NUMBER));
        Intent intent = new Intent(this, AddLeadsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("phoneNo", strPhone);
        intent.putExtra("launch_origin","list_call_activity");

        startActivity(intent);
        finish();

    }
}