package in.sisoft.apps.ecrm.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import in.sisoft.apps.ecrm.Activities.AddLeadsActivity;
import in.sisoft.apps.ecrm.SharedPreferences.SharedPref;


public class IncomingCallReceiver extends BroadcastReceiver {
//        Context mContext;

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;



    @Override
        public void onReceive(Context context, Intent intent) {

       // Toast.makeText(context, "Call Phone Status:On Receive" , Toast.LENGTH_SHORT).show();

        if (!SharedPref.getInstance(context).returnUser().isEmpty()) {
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            } else {

                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                onCallStateChanged(context, state, number);
            }
        }
    }


    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;

               // Toast.makeText(context, "Incoming Call Ringing" , Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                   // Toast.makeText(context, "Outgoing Call Started" , Toast.LENGTH_SHORT).show();
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                  //  Toast.makeText(context, "Ringing but no pickup" + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();
                    //--------------------------------------------------make an entry here

                    //firing intent here

                    boolean x = contactExists(context,savedNumber);
                    Log.d("TAG", "onCallStateChanged:"+ x);

                    if(!x) {

                        Intent iToActivity2 = new Intent(context, AddLeadsActivity.class);
                        iToActivity2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        iToActivity2.putExtra("phoneNo", savedNumber);
                        iToActivity2.putExtra("launch_origin", "incoming");
                        context.startActivity(iToActivity2);
                    }

                }
                else if(isIncoming){
                    //------------------------------------------------------make an entry here

                    //firing intent here

                    boolean y = contactExists(context,savedNumber);
                    Log.d("TAG", "onCallStateChanged:"+ y);

                    if(!y) {
                        Intent iToActivity2 = new Intent(context, AddLeadsActivity.class);
                        iToActivity2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        iToActivity2.putExtra("phoneNo", savedNumber);
                        iToActivity2.putExtra("launch_origin", "incoming");
                        context.startActivity(iToActivity2);
                    }

                   // Toast.makeText(context, "Incoming " + savedNumber + " Call time " + callStartTime  , Toast.LENGTH_SHORT).show();
                }
                
                else{

                  //  Toast.makeText(context, "outgoing " + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();

                }

                break;
        }
        lastState = state;
    }

    public boolean contactExists(Context ctx, String number) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = ctx.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                Log.d("TAG", "contactExists:");
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        Log.d("TAG", "ContaxtExists: contact does not exist");
        return false;
    }
}










