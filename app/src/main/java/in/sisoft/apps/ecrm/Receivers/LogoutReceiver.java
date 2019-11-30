package in.sisoft.apps.ecrm.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import in.sisoft.apps.ecrm.Activities.LoginActivity;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;


public class LogoutReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        if(!SharedPref.getInstance(context).returnUser().equals("") &&
                !SharedPref.getInstance(context).returnAuthCode().equals("")){
            SharedPref.getInstance(context).deleteUser();
            SharedPref.getInstance(context).deleteAuthCode();
            Toast.makeText(context, "logout", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(context,LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent1);
        }


    }
}