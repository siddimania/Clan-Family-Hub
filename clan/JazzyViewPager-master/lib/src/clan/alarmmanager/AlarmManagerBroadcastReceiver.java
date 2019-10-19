package clan.alarmmanager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;


public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
	final String contentString="2 new Events";
	final private int Time_delay=3*3600;
	final public static String ONE_TIME = "onetime";
	
	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent intent) {
		 PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "JYC_POWER_TAG");
         wl.acquire();  
       //  Toast.makeText(context, "onReceiver alamar manager ", Toast.LENGTH_SHORT).show();
         if(checkConnection(context)){
        	 try {
 				Intent service = new Intent(context,LocationUpdateOnServerService.class);
 		        context.startService(service);
 			} catch(Exception e){
 				e.printStackTrace();
 			}
         }
         wl.release();
	}
	public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent,PendingIntent.FLAG_NO_CREATE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * Time_delay , pi); 
       
    //    Toast.makeText(context, "Set Alarm  ", Toast.LENGTH_SHORT).show();
        ComponentName receiver = new ComponentName(context, clan.alarmmanager.SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);      
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        
        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the 
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    public void setOnetimeTimer(Context context){
    	AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
      //  Toast.makeText(context, "set on one time ", Toast.LENGTH_SHORT).show();
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
        //am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * Time_delay , pi); 
    }
    public void setRepeatingTimer(Context context){
    	AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        //Toast.makeText(context, "Repeating Alarm Set.", Toast.LENGTH_SHORT).show();
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * Time_delay , pi); 
    }
    
    public static boolean checkConnection(Context context) {
    	
    	boolean result = false;
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo ni = cm.getActiveNetworkInfo();
    	if (ni != null) {
			result =  true;
		} else {
			result = false;
		}
		return result;
	}
}
