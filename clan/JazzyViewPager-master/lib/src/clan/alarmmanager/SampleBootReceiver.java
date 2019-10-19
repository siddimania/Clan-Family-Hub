package clan.alarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;


// BEGIN_INCLUDE(autostart)
public class SampleBootReceiver extends BroadcastReceiver {
	AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        alarm.SetAlarm(context);
        //Toast.makeText(context, "SampleBoot Receiver ", Toast.LENGTH_SHORT).show();
        //checkConnection(context);
    }
    
    
}
//END_INCLUDE(autostart)
