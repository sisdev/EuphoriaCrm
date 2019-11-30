package in.sisoft.apps.ecrm.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Date;

import in.sisoft.apps.ecrm.R;

/**
 * Created by Dell on 01-02-2017.
 */
public class CallCursorAdapter extends CursorAdapter {
    public CallCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.call_details, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor managedCursor) {
       /* if (managedCursor.getPosition()%2!=0){
            view.setBackgroundColor(Color.CYAN);
        }
        else{
            view.setBackgroundColor(Color.MAGENTA);
        }*/
        TextView tvPhone = (TextView) view.findViewById(R.id.textPhone);
        TextView tvType = (TextView) view.findViewById(R.id.textType);
        TextView tvDate = (TextView) view.findViewById(R.id.textDate);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        String phNumber = managedCursor.getString(number);
        String callType = managedCursor.getString(type);
        String callDate = managedCursor.getString(date);
        Date callDayTime = new Date(Long.valueOf(callDate));
        String callDuration = managedCursor.getString(duration);
        String dir = null;
        int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }
        tvPhone.setText(phNumber);
        tvType.setText(dir);
        tvDate.setText(callDayTime.toString());
    }
}
