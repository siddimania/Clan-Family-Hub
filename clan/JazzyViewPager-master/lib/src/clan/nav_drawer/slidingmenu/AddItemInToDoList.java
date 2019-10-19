package clan.nav_drawer.slidingmenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import clan.customeview.database.DbHelper;
import clan.main.MainActivity;
import clan.main.R;
import clan.main.SendingInvite;

public class AddItemInToDoList extends Activity{
	

    final static int RQS_1 = 1;
    final static int i = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_add_to_do_list);
		Button done = (Button) findViewById(R.id.invite_button_done);
		done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DateTimeDiff();
				//finish();
			}
		});
	}
	
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
	    switch(view.getId()) {
	        case R.id.checkbox_addTimer:
	            if (checked){
	            	//Toast.makeText(getApplicationContext(), "checbox clicked", Toast.LENGTH_SHORT).show();
	            	Intent intent = new Intent(getApplicationContext(), clan.nav_drawer.slidingmenu.AddDateTimeToDoList.class);
					startActivity(intent);
	            }
	            else
	                
	            break;
	    }
	}
	
	private void DateTimeDiff() {
		Calendar calNow = Calendar.getInstance();
		Calendar calSet = (Calendar) calNow.clone();
/*
		calSet.set(Calendar.HOUR_OF_DAY, AddDateTimeToDoList.gHour);
		calSet.set(Calendar.MINUTE, AddDateTimeToDoList.gMinute);
		calSet.set(Calendar.YEAR, AddDateTimeToDoList.gYear);
		calSet.set(Calendar.DAY_OF_MONTH, AddDateTimeToDoList.gDay);
		calSet.set(Calendar.MONTH, AddDateTimeToDoList.gMonth);
		*/
		
		calSet.set(Calendar.HOUR_OF_DAY, 9);
		calSet.set(Calendar.MINUTE, 40);
		calSet.set(Calendar.YEAR, 2014);
		calSet.set(Calendar.DAY_OF_MONTH, 31);
		calSet.set(Calendar.MONTH, 12);

		long ourTime = calSet.getTimeInMillis();
		long currentTime = calNow.getTimeInMillis();

		long timeDiff = ourTime - currentTime;

		setAlarm(timeDiff);

	}

	
	private void setAlarm(long timeDiff) {
/*
		int notificationId = 1;
		Toast.makeText(getApplicationContext(), "Added Item to do list",
				Toast.LENGTH_SHORT).show();

		
		Intent intentAlarm= new Intent(this, AlarmReceiver.class);
		PendingIntent p = PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, timeDiff, p );


		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.clan_icon_3d)
				.setContentTitle("Event Set").setContentText("This is Event")
				.setContentIntent(p);

		// Get an instance of the NotificationManager service
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

		// Build the notification and issues it with notification manager.
		notificationManager.notify(notificationId, notificationBuilder.build());
		*/
		Intent i = getIntent();
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String email = prefs.getString("email", "");
		String password = prefs.getString("password", "");
		String femail = i.getStringExtra("femail");
		
		EditText itemName = (EditText)findViewById(R.id.item_name);
		String myitemName = itemName.getText().toString();
		
		String date = AddDateTimeToDoList.gDay+" "+AddDateTimeToDoList.gMonth+" "+AddDateTimeToDoList.gYear;
		String time = AddDateTimeToDoList.gHour+" "+ AddDateTimeToDoList.gMinute;
		
		uploadtoServer(email,password,femail,myitemName,date,time);
	}
	
	private void uploadtoServer(String email,String password,String femail,String task,String date,String time) {

		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			String email;
			String password;
			String femail,task,date,time;
			@Override
			protected String doInBackground(String... params) {
				email = params[0];
				password = params[1];
				femail = params[2];
				task = params[3];
				date = params[4];
				time = params[5];
				Log.d("IN Background",""+email+" "+password+" "+femail+" "+task+" "+date+" "+time);
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://jiitsimplified.com/clan/server/addtodo.php");

				BasicNameValuePair emailBasicNameValuePAir = new BasicNameValuePair("useremail", email);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("pass", password);
				BasicNameValuePair femailBasicNameValuePAir = new BasicNameValuePair("femail", femail);
				BasicNameValuePair taskBasicNameValuePAir = new BasicNameValuePair("task", task);
				BasicNameValuePair dateBasicNameValuePAir = new BasicNameValuePair("date", date);
				BasicNameValuePair timeBasicNameValuePAir = new BasicNameValuePair("time", time);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(emailBasicNameValuePAir);
				nameValuePairList.add(passwordBasicNameValuePAir);
				nameValuePairList.add(femailBasicNameValuePAir);
				nameValuePairList.add(taskBasicNameValuePAir);
				nameValuePairList.add(dateBasicNameValuePAir);
				nameValuePairList.add(timeBasicNameValuePAir);
				Log.d("IN Background","in background");
				try {
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
							nameValuePairList);
					httpPost.setEntity(urlEncodedFormEntity);

					try {
						HttpResponse httpResponse = httpClient
								.execute(httpPost);
						InputStream inputStream = httpResponse.getEntity().getContent();
						InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						StringBuilder stringBuilder = new StringBuilder();

						String bufferedStrChunk = null;

						while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
							stringBuilder.append(bufferedStrChunk);
						}

						return stringBuilder.toString();

					} catch (ClientProtocolException cpe) {
						System.out.println("Firstption caz of HttpResponese :"+ cpe);
						cpe.printStackTrace();
					} catch (IOException ioe) {
						System.out.println("Secondption caz of HttpResponse :"+ ioe);
						ioe.printStackTrace();
					}

				} catch (UnsupportedEncodingException uee) {
					System.out.println("Anption given because of UrlEncodedFormEntity argument :"+ uee);
					uee.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				dialog.hide();
				//Log.d("IN Background","POST EXECUTE");
				Log.d("Add TODO ITEM JSON",result);
				JSONObject getResult;
				try {
					getResult = new JSONObject(result);
					boolean validUser = getResult.getInt("result")==1?true:false;
					
					if(validUser){
						Toast.makeText(getApplicationContext(),"Task Uploaded to Server Successfully.", Toast.LENGTH_LONG).show();
						storeItIntoDatabase(femail,task,date,time);
					}
					else{
						Toast.makeText(getApplicationContext(), "Error in uploading task to server", Toast.LENGTH_LONG).show();
						//String errormsg = getResult.getString("errormsg");
						//Toast.makeText(getApplicationContext(), "ErrorCode::"+errorCode, Toast.LENGTH_LONG).show();
					}	
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ProgressDialog dialog;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog = new ProgressDialog(AddItemInToDoList.this);
				dialog.setMessage("Please Wait");
				dialog.show();
			}
		}
		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute(email,password,femail,task,date,time);
	}

	public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

	private void storeItIntoDatabase(String femail,String task,String date,String time) {
		// TODO Auto-generated method stub
		DbHelper dbHelper;
		SQLiteDatabase db;
		
		dbHelper = new DbHelper(getApplicationContext());
		db = dbHelper.getWritableDatabase();

		final Calendar c = Calendar.getInstance();
		int id = c.get(Calendar.MILLISECOND);
		
		
		ContentValues values = new ContentValues();

		values.put(DbHelper._ID,id);
		values.put(DbHelper.friendEmail, femail);
		values.put(DbHelper.toDoItem, task);
		values.put(DbHelper.itemDate, date);
		values.put(DbHelper.itemTime, time);
		db.insertWithOnConflict(DbHelper.friendTABLETODO, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		
		donewithActivity();
	}
	
	private void donewithActivity(){
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result","done"); 
		setResult(RESULT_OK,returnIntent);
		finish(); 
	}

}
