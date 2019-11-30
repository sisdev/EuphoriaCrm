package in.sisoft.apps.ecrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.ArrayList;

import in.sisoft.apps.ecrm.Activities.LoginActivity;
import in.sisoft.apps.ecrm.Models.Lead;
import in.sisoft.apps.ecrm.Models.Task;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;

public class AppGlobal {


    ///////////////////////////////constant///////////////////////////////
    //url
    private static String HOME_URL="http://www.sisoft.in/training/admin/webapi/";
   /* private static String HOME_URL="http://www.innoforia.com/shubham/crm-webapi/";*/
    public static final String LOGIN_URL = HOME_URL+"ws-login.php";
    public static final String WORK_REPORT_URL = HOME_URL+"ws-sales-work-report.php";
    public static final String ADD_LEAD_URL = HOME_URL+"ws-lead-add.php";
    public static final String MY_TASK_URL = HOME_URL+"ws-mytask.php";
    public static final String MY_LEAD_URL = HOME_URL+"ws-myleads.php";
    public static final String MY_TASK_UPDATE_URL = HOME_URL+"ws-mytask-update.php";
    public static final String MY_SEARCH_LEAD_URL = HOME_URL+"ws-search-leads.php";
    //email pattern
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    ///////////////////////////////non-constant///////////////////////////////
    //list
    public static ArrayList<Task>  taskList = new ArrayList<>();
    public static ArrayList<Lead>  leadList = new ArrayList<>();

    //methods
    public static void AppExit(Activity activity)
    {
        activity.finish();
        Intent intent= new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
        System.exit(0);
    }

  /*  public static boolean isInternetConnected(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // Check if wifi or mobile network is available or not. If any of them is
        // available or connected then it will return true, otherwise false;
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }
        return mobile != null && mobile.isConnected();
    }*/

    public static void logoutAuthError(Context context){
        SharedPref.getInstance(context).deleteAuthCode();
        SharedPref.getInstance(context).deleteUser();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
