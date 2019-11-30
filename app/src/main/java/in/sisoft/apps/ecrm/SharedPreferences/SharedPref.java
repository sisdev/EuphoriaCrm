package in.sisoft.apps.ecrm.SharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private static SharedPref sharedPref;
    private static SharedPreferences mSharedPref;
    private static final String LOGIN_PREF = "login_pref";
    private static final String ORGANISATION_ID = "org_id";
    private static final String AUTH_CODE = "auth_code";
    private static final String SHARED_PREF= "shared_preference";

    private SharedPref(Context context){
        if(mSharedPref==null){
            mSharedPref = context.getSharedPreferences(SHARED_PREF, Activity.MODE_PRIVATE);
        }
    }

    public static SharedPref getInstance(Context context)
    {
        if(sharedPref == null){
            sharedPref = new SharedPref(context);
        }
        return sharedPref;
    }

//////////// user /////////////////////
    public  void storeUser(String user){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(LOGIN_PREF,user);
        editor.apply();
    }
    public  void deleteUser(){

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(LOGIN_PREF);
        editor.apply();
    }
    public  String returnUser(){
      return mSharedPref.getString(LOGIN_PREF,"");
    }


    ///////// organisation code////////////


    public  void storeOrganisationId(String org_id){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ORGANISATION_ID,org_id);
        editor.apply();
    }
    public  void deleteORG_ID(){

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(ORGANISATION_ID);
        editor.apply();
    }
    public  String returnORG_ID(){
        return mSharedPref.getString(ORGANISATION_ID,"");
    }


    /////////Authentication code

    public  void storeAuthCode(String auth_code){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(AUTH_CODE,auth_code);
        editor.apply();
    }
    public  void deleteAuthCode(){

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(AUTH_CODE);
        editor.apply();
    }
    public  String returnAuthCode(){
        return mSharedPref.getString(AUTH_CODE,"");
    }




}
