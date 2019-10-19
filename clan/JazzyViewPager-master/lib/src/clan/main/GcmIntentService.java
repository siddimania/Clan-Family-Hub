package clan.main;

import java.util.Calendar;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import clan.alarmmanager.AlarmManagerBroadcastReceiver;
import clan.alarmmanager.LocationUpdateOnServerService;
import clan.customeview.database.DbHelper;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String TAG="GCM";
 
    public GcmIntentService() { 
        super("GcmIntentService"); 
    } 
 
    @Override 
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received 
        // in your BroadcastReceiver. 
        String messageType = gcm.getMessageType(intent);
 
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /* 
             * Filter messages based on message type. Since it is likely that GCM 
             * will be extended in the future with new message types, just ignore 
             * any message types you're not interested in, or that you don't 
             * recognize. 
             */ 
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + 
                        extras.toString());
            // If it's a regular GCM message, do some work. 
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work. 
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try { 
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    } 
                } 
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                Log.i(TAG, "Received GCM: " + extras.toString());
                // Post notification of received message. 
                switch(extras.getString("type")){
	                case "location_request":
	                	sendNotification("Location Request " + extras.toString());
	                	if(AlarmManagerBroadcastReceiver.checkConnection(getApplicationContext())){
	                   	 try {
	            				Intent service = new Intent(getApplicationContext(),LocationUpdateOnServerService.class);
	            				getApplicationContext().startService(service);
	            			} catch(Exception e){
	            				e.printStackTrace();
	            			}
	                    }
	                	break;
	                case "location_updated":
	                	double latitude = Double.parseDouble(extras.getString("latitude"));
	                	double longitude = Double.parseDouble(extras.getString("longitude"));
	                	sendNotification("Location Request " + extras.toString());
	                	sendMessage(latitude,longitude);                	
	                	break;
	                case "todoitemadd":
	                	String task = extras.getString("todo_task");
	                	String date = extras.getString("todo_task_date");
	                	String time = extras.getString("todo_task_time");
	                	String fromEmail = extras.getString("from_email");
	                	String fromName =  extras.getString("from_name");
	                	
	                	sendNotification("A new task from "+fromName);
	                	addTODOToDatabase(fromEmail,task,date,time);
	                	break;
	                case "ring_phone":
	                	Log.i(TAG, "Ring Phone: " + extras.toString());
	                	sendNotification("Ring Phone: " + extras.toString());
	                	Intent intentRingPhone = new Intent(getApplicationContext(), clan.ring.phone.PhoneRing.class);
	                	intentRingPhone.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    				startActivity(intentRingPhone);
	                	break;
	                default:
	                	sendNotification("Received: " + extras.toString());
	                	break;
                }
                Log.i(TAG, "Received: " + extras.toString());
            } 
        } 
        // Release the wake lock provided by the WakefulBroadcastReceiver. 
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    } 
    
    private void addTODOToDatabase(String fromEmail, String task, String date,
			String time) {
		// TODO Auto-generated method stub
    	DbHelper dbHelper;
		SQLiteDatabase db;
		
		dbHelper = new DbHelper(getApplicationContext());
		db = dbHelper.getWritableDatabase();

		final Calendar c = Calendar.getInstance();
		int id = c.get(Calendar.MILLISECOND);
		
		
		ContentValues values = new ContentValues();

		values.put(DbHelper._ID,id);
		values.put(DbHelper.friendEmail, fromEmail);
		values.put(DbHelper.toDoItem, task);
		values.put(DbHelper.itemDate, date);
		values.put(DbHelper.itemTime, time);
		db.insertWithOnConflict(DbHelper.TABLE2, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		
	}

	private void sendMessage(double latitude,double longitude) { 
    	  Intent intent = new Intent("location_updated");
    	  intent.putExtra("latitude", latitude);
    	  intent.putExtra("longitude", longitude);
    	  //Toast.makeText(getApplicationContext(), "Lat: "+latitude+" "+longitude, Toast.LENGTH_LONG).show();
    	  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    	} 
 
    // Put the message into a notification and post it. 
    // This is just one simple example of what you might choose to do with 
    // a GCM message. 
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
 
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
 
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("GCM Notification") 
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);
 
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    } 
} 