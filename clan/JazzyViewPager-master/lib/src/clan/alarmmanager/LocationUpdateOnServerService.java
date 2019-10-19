package clan.alarmmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import clan.alarmmanager.MyLocation.LocationResult;

public class LocationUpdateOnServerService extends Service{

	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected Context context;
	String lat;
	String provider;
	protected double latitude,longitude; 
	protected boolean gps_enabled,network_enabled;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	MyLocation myLocation ;
	LocationResult locationResult;
	PowerManager.WakeLock wl;
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CLAN_POWERTAGS"); 
        wl.acquire();  
        
		locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location mLastLocation){
		        //Got the location!
		    	latitude = mLastLocation.getLatitude();
	            longitude = mLastLocation.getLongitude();
	            
	         //   Toast.makeText(getApplicationContext(), "mLastLocation "+latitude+longitude, Toast.LENGTH_SHORT).show();
	            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    		boolean ifLogged = prefs.getBoolean("ifLogged", false);
	    		if(ifLogged){
	    			String email = prefs.getString("email", "");
	    			String password = prefs.getString("password", "");
	    			
	    			Toast.makeText(getApplicationContext(), "lat and long "+ latitude+" "+longitude, Toast.LENGTH_SHORT).show();
	    			
	    			sendPostRequest(email,password,latitude,longitude); // last update latitude and longitude 
	    			sendPostRequestEveryTime(email,password,latitude,longitude); // storing all the latitude and logitude
	    		}
		    }
		};
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		wl.release();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
			myLocation = new MyLocation();
			myLocation.getLocation(this, locationResult);
		return Service.START_NOT_STICKY;
	}
	
	
	private void sendPostRequest(String email,String password, double latitude , double longitude) {
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {

				
				String email = params[0];
				String password = params[1];
				String latitude = params[2];
				String longitude = params[3];

				HttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost("http://jiitsimplified.com/clan/server/updatelocation.php");

				BasicNameValuePair fNameBasicNameValuePair = new BasicNameValuePair("useremail", email);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("pass", password);
				BasicNameValuePair lNameBasicNameValuePAir = new BasicNameValuePair("latitute", latitude);
				BasicNameValuePair emailBasicNameValuePAir = new BasicNameValuePair("longitude", longitude);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(fNameBasicNameValuePair);
				nameValuePairList.add(lNameBasicNameValuePAir);
				nameValuePairList.add(emailBasicNameValuePAir);
				nameValuePairList.add(passwordBasicNameValuePAir);
				
				try {
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

					httpPost.setEntity(urlEncodedFormEntity);

					try {

						HttpResponse httpResponse = httpClient.execute(httpPost);

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
				Log.d("Signup","On Post Execute");
				JSONObject getResult;
			//	Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
				try {
					getResult = new JSONObject(result);
					Log.d("HTTP MSG", getResult.toString());
					
					boolean registered = getResult.getInt("result")==1?true:false;
					
					if(registered){
						Toast.makeText(getApplicationContext(),"Location Update Successful", Toast.LENGTH_LONG).show();
					}
					else{
						int errorCode = getResult.getInt("errorcode");
						switch(errorCode){
							case 500:
								// The user is not registered. User is new. Show sign up page.
								Toast.makeText(getApplicationContext(), "UserName and Password Do not match",Toast.LENGTH_LONG).show();
								break;
							default:
								Toast.makeText(getApplicationContext(), "Not all values set",Toast.LENGTH_LONG).show();
								break;
						}
						//String errormsg = getResult.getString("errormsg");
						//Toast.makeText(getApplicationContext(), "ErrorCode::"+errorCode, Toast.LENGTH_LONG).show();
					}	
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		//Toast.makeText(getApplicationContext(), "string sendpost request",Toast.LENGTH_LONG).show();
		sendPostReqAsyncTask.execute(email, password, ""+latitude,""+longitude);
	}
	
	private void sendPostRequestEveryTime(String email,String password, double latitude , double longitude) {
		class SendPostReqAsyncTaskEveryTime extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {

				String email = params[0];
				String password = params[1];
				String latitude = params[2];
				String longitude = params[3];

				Log.d("Every Time", "this is everytime");
				
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://jiitsimplified.com/clan/server/updateLocationEveryTime.php");

				BasicNameValuePair fNameBasicNameValuePair = new BasicNameValuePair("useremail", email);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("pass", password);
				BasicNameValuePair lNameBasicNameValuePAir = new BasicNameValuePair("latitute", latitude);
				BasicNameValuePair emailBasicNameValuePAir = new BasicNameValuePair("longitude", longitude);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(fNameBasicNameValuePair);
				nameValuePairList.add(lNameBasicNameValuePAir);
				nameValuePairList.add(emailBasicNameValuePAir);
				nameValuePairList.add(passwordBasicNameValuePAir);
				
				try {
					Log.d("Every Time 2", "http request");
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
					httpPost.setEntity(urlEncodedFormEntity);
					try {
						HttpResponse httpResponse = httpClient.execute(httpPost);
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
				Log.d("Signup","On Post Execute"+result);
				JSONObject getResult;
				try {
					getResult = new JSONObject(result);
					boolean registered = getResult.getInt("result")==1?true:false;
					
					Toast.makeText(getApplicationContext(),"Location Update Successful EveryTime", Toast.LENGTH_LONG).show();
					
					if(registered){
						Toast.makeText(getApplicationContext(),"Location Update Successful EveryTime", Toast.LENGTH_LONG).show();
					}
					else{
						int errorCode = getResult.getInt("errorcode");
						switch(errorCode){
						case 500:
							Toast.makeText(getApplicationContext(), "Values not inteserted in table",Toast.LENGTH_LONG).show();
							break;
						default:
							Toast.makeText(getApplicationContext(), "Not all values set",Toast.LENGTH_LONG).show();
							break;
						}
					}	
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		SendPostReqAsyncTaskEveryTime sendPostReqAsyncTaskEveryTime = new SendPostReqAsyncTaskEveryTime();
		//Toast.makeText(getApplicationContext(), "string sendpost request",Toast.LENGTH_LONG).show();
		sendPostReqAsyncTaskEveryTime.execute(email, password, ""+latitude,""+longitude);
	}

}
