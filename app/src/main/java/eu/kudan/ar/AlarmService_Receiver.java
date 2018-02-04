package eu.kudan.ar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;


public class AlarmService_Receiver extends BroadcastReceiver {
    final Calendar c = Calendar.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");

        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSecond =c.get(Calendar.SECOND);
        Log.d("msg","time");
        Toast.makeText(context, "현재 시간"+mHour+":"+mMinute+":"+mSecond, Toast.LENGTH_SHORT).show();
    }
}
