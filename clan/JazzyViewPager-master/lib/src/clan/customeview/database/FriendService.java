package clan.customeview.database;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import clan.main.MainActivity;

public class FriendService extends Service {

	DbHelper dbHelper;
	SQLiteDatabase db;

	@Override
	public IBinder onBind(Intent arg0) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		checkConnection();
		return super.onStartCommand(intent, flags, startId);
	}

	private void checkConnection() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();

		if (is3g || isWifi) {
			try {
				accessWebService();
			} finally {
				onDestroy();
			}
		} else {
			onDestroy();
		}

	}

	private void writingIntoDatabase(final int i, final String firstName,
			final String lastName, final String email, final int status,
			String latitude, String longitude, String place, String lastseen) {

		dbHelper = new DbHelper(getApplicationContext());
		db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(DbHelper._ID, i);
		values.put(DbHelper.firstName, firstName);
		values.put(DbHelper.lastName, lastName);
		values.put(DbHelper.email, email);
		values.put(DbHelper.status, status);
		values.put(DbHelper.latitude, latitude);
		values.put(DbHelper.longitude, longitude);
		values.put(DbHelper.lastseen, lastseen);
		values.put(DbHelper.place, place);
		db.insertWithOnConflict(DbHelper.TABLE1, null, values,
				SQLiteDatabase.CONFLICT_REPLACE);
		// Toast.makeText(getApplicationContext(),
		// "finished writing into database", Toast.LENGTH_SHORT).show();
		Log.d("writing into database ", "finished writing");
	}

	public void accessWebService() {

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String email = prefs.getString("email", "");
		String password = prefs.getString("password", "");
		sendPostRequest(email, password);
	}

	private void sendPostRequest(String email, String password) {

		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			String email;
			String password;

			@Override
			protected String doInBackground(String... params) {

				email = params[0];
				password = params[1];

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						"http://jiitsimplified.com/clan/server/getfriends.php");

				BasicNameValuePair emailBasicNameValuePAir = new BasicNameValuePair(
						"useremail", email);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair(
						"pass", password);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(emailBasicNameValuePAir);
				nameValuePairList.add(passwordBasicNameValuePAir);

				try {
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
							nameValuePairList);

					httpPost.setEntity(urlEncodedFormEntity);

					try {
						HttpResponse httpResponse = httpClient
								.execute(httpPost);

						InputStream inputStream = httpResponse.getEntity()
								.getContent();
						InputStreamReader inputStreamReader = new InputStreamReader(
								inputStream);
						BufferedReader bufferedReader = new BufferedReader(
								inputStreamReader);
						StringBuilder stringBuilder = new StringBuilder();
						String bufferedStrChunk = null;

						while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
							stringBuilder.append(bufferedStrChunk);
						}

						return stringBuilder.toString();

					} catch (ClientProtocolException cpe) {
						System.out.println("Firstption caz of HttpResponese :"
								+ cpe);
						cpe.printStackTrace();
					} catch (IOException ioe) {
						System.out.println("Secondption caz of HttpResponse :"
								+ ioe);
						ioe.printStackTrace();
					}

				} catch (UnsupportedEncodingException uee) {
					System.out
							.println("Anption given because of UrlEncodedFormEntity argument :"
									+ uee);
					uee.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Toast.makeText(getApplicationContext(), "Family List Updated",Toast.LENGTH_SHORT).show();
				ListDrwaer(result);
			}
		}

		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute(email, password);
	}

	public void ListDrwaer(String jsonResult) {
		// getLastUpdatedSno();

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("response");

			dbHelper = new DbHelper(getApplicationContext());
			db = dbHelper.getReadableDatabase();

			JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

			for (int i = 0; i < jsonMainNode.length(); i++) {

				jsonChildNode = jsonMainNode.getJSONObject(i);
				String firstName = jsonChildNode.optString("firstname");
				String lastName = jsonChildNode.optString("lastname");
				String email = jsonChildNode.optString("email");
				int status = Integer
						.parseInt(jsonChildNode.optString("status"));
				String latitude = jsonChildNode.optString("latitute");
				String longitude = jsonChildNode.optString("longitude");
				String lastseen = jsonChildNode.optString("lastseen");
				String place = jsonChildNode.optString("place");
				writingIntoDatabase(i, firstName, lastName, email, status,
						latitude, longitude,place, lastseen);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
