package in.sisoft.apps.ecrm;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.sisoft.apps.ecrm.Activities.HomeActivity;
import in.sisoft.apps.ecrm.Activities.UpdateMyTaskActivity;
import in.sisoft.apps.ecrm.Models.Task;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;


public class GetMyTaskData {
    private Context context;
    private MyListener MyListener;
    private ProgressBar progressBar;
    private static final String TAG = "GetMyTaskData";



    public GetMyTaskData(Context context, ProgressBar progressBar){
        this.context = context;
        this.progressBar = progressBar;
    }

    public GetMyTaskData(Context context){
        this.context  = context;
    }


    public void getData(double loc_lat , double loc_long) {

        if(context instanceof HomeActivity) {

            progressBar.setVisibility(View.VISIBLE);
        }
        final StringRequest request = new StringRequest(Request.Method.POST, AppGlobal.MY_TASK_URL,

                response -> {

                    Log.d("response_print",response);

                    try {

                        JSONArray array = new JSONArray(response);
                        parseData(array);
                        Log.d("getMyTaskData","");

                    } catch (JSONException e) {
                        Log.d(TAG," Exception \n"+e.getMessage());
                        if(e.getMessage().contains("Incorrect")){
                            Log.d("getMyTaskData","Exception : "+e.getMessage());
                            AppGlobal.logoutAuthError(context);
                        }
                    }
                },
                error -> {
                    Toast.makeText(context, "Volley Error", Toast.LENGTH_SHORT).show();

                    if(context instanceof HomeActivity){
                        progressBar.setVisibility(View.INVISIBLE);
                        MyListener.callbackOnError();
                    }
                }){



            @Override
            protected Map<String, String> getParams() {
                HashMap<String , String> param = new HashMap<>();

                 param.put("username", SharedPref.getInstance(context).returnUser());
                 param.put("org_code", SharedPref.getInstance(context).returnORG_ID());
                param.put("auth_token",SharedPref.getInstance(context).returnAuthCode());
                param.put("loc_lat", String.valueOf(loc_lat));
                param.put("loc_long", String.valueOf(loc_long));

                Log.d(TAG,"\nuser : "+SharedPref.getInstance(context).returnUser()+"\n"
                +"org_code : "+SharedPref.getInstance(context).returnORG_ID()+"\n"+
                "auth_code : "+SharedPref.getInstance(context).returnAuthCode()+"\nloc_lat : "+loc_lat+"\n"
                +loc_long);



                return param;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                500,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void parseData(JSONArray array) {

        AppGlobal.taskList.clear();
        Toast.makeText(context, ""+array.length(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);

                String id = String.valueOf(jsonObject.getInt("id"));
                String strLeadQryId = jsonObject.getString("trng_query_id");
                String strName = jsonObject.getString("name");
                String strPhoneNo = jsonObject.getString("phone_no");
                String strQryDetails = jsonObject.getString("qry_details");
                String strAssignedUser = jsonObject.getString("assigned_user");
                String strTaskDateTime = jsonObject.getString("datetime");
                String strTaskType = jsonObject.getString("task_type");
                String strNarration = jsonObject.getString("narration");
                String strStatus = jsonObject.getString("status");
                String strCreatedBy = jsonObject.getString("created_by");

                Task task = new Task(id,strLeadQryId,strName,strPhoneNo,strQryDetails,strAssignedUser,
                        strTaskDateTime,strTaskType,strNarration,strStatus,strCreatedBy);
                AppGlobal.taskList.add(task);



            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Exception "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if(context instanceof HomeActivity){
            progressBar.setVisibility(View.INVISIBLE);
            MyListener.callbackOnSuccess();
        }
        if(context instanceof UpdateMyTaskActivity){

            ((UpdateMyTaskActivity) context).finish();
        }


    }

    public void setMyListener(MyListener myListener){
        this.MyListener = myListener;
    }


    public interface MyListener {
        void callbackOnSuccess();
        void callbackOnError();
    }
}
