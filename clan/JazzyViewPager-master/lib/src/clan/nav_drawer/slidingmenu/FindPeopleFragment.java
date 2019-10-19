package clan.nav_drawer.slidingmenu;

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

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import clan.alarmmanager.AlarmManagerBroadcastReceiver;
import clan.customeview.database.DbHelper;
import clan.main.R;

public class FindPeopleFragment extends Fragment {

	public FindPeopleFragment() {
	}
	CustomAdapter adapter;
	DbHelper dbHelper;
	SQLiteDatabase db;
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.nav_drawer_fragment_find_people,
				container, false);
		
		return rootView;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		gettingListView();
	}

	private void gettingListView() {

		dbHelper = new DbHelper(getActivity().getApplicationContext());
		db = dbHelper.getReadableDatabase();
		try {
			final ListView tv = (ListView) rootView
					.findViewById(R.id.friendList);
		
			adapter = new CustomAdapter(this.getActivity());
			tv.setAdapter(adapter);
			tv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					DbHelper dbHelper = new DbHelper(getActivity().getApplicationContext());
					SQLiteDatabase db = dbHelper.getReadableDatabase();
					Cursor cursor;
					try {
						String sql = "SELECT * FROM " + DbHelper.TABLE1 + "";

						Cursor mCur = db.rawQuery(sql, null);
						if (mCur != null) {
							mCur.moveToPosition(position);
						}
						cursor = mCur;
					} catch (SQLException mSQLException) {
						Log.e("Family Friend", "getTestData >>" + mSQLException.toString());
						throw mSQLException;
					}
					
					String firstName = cursor.getString(1);
					String lastName = cursor.getString(2);
					String email = cursor.getString(3);
					String latitude = cursor.getString(5);
					String longitude = cursor.getString(6);
					String lastseen = cursor.getString(8);
					String place = cursor.getString(7);
					
					Intent intent = new Intent(getActivity(),
							FriendTimeLine.class);
					Bundle extras = new Bundle();
					extras.putString("firstName", firstName);
					extras.putString("lastName", lastName);
					extras.putString("email", email);
					extras.putDouble("latitude", Double.parseDouble(latitude));
					extras.putDouble("longitude", Double.parseDouble(longitude));
					extras.putString("lastseen", lastseen);
					intent.putExtras(extras);
					startActivity(intent);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void updateAdapter(){
		adapter.notifyDataSetChanged();
	}
	
}

class CustomAdapter extends BaseAdapter implements OnClickListener {
	private Activity activity;
	private Cursor cursor;

	public CustomAdapter(Activity a) {
		activity = a;
		DbHelper dbHelper = new DbHelper(a.getApplicationContext());
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		try {
			String sql = "SELECT * FROM " + DbHelper.TABLE1 + "";

			Cursor mCur = db.rawQuery(sql, null);
			if (mCur != null) {
				mCur.moveToNext();
			}
			cursor = mCur;
		} catch (SQLException mSQLException) {
			Log.e("Family Friend", "getTestData >>" + mSQLException.toString());
			throw mSQLException;
		}
	}

	public int getCount() {
		return cursor.getCount();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = parent.inflate(activity.getApplicationContext(),R.layout.custome_list_view_friends, null);

		TextView tvName = (TextView) vi.findViewById(R.id.name);
		TextView tvEmail = (TextView) vi.findViewById(R.id.email);

		String fname, lname, email;

		// status is at 4
		fname = cursor.getString(1);
		lname = cursor.getString(2);
		email = cursor.getString(3);
		tvName.setText(fname + lname);
		tvEmail.setText(email);

		int status = Integer.parseInt(cursor.getString(4));
		if (status == 1) {
			View v = vi.findViewById(R.id.family_list_approval_decision);
			v.setVisibility(View.GONE);

		} else {

			Button approve = (Button) vi.findViewById(R.id.family_list_accept);
			Button reject = (Button) vi.findViewById(R.id.family_list_reject);

			approve.setTag(email);
			reject.setTag(email);
			
			approve.setOnClickListener(this);
			reject.setOnClickListener(this);
		}
		return vi;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.family_list_accept:
	
		String email = (String) v.getTag();
				//Toast.makeText(activity, "Approval Process"+email, Toast.LENGTH_LONG).show();
				acceptFriendRequest(email);
			break;
		case R.id.family_list_reject:
		//	Toast.makeText(activity, "Rejection Process", Toast.LENGTH_LONG).show();
			break;
		}
	}
	
	void acceptFriendRequest(String femail) {
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			String email;
			String password;
			String femail;
			@Override
			protected String doInBackground(String... params) {
				email = params[0];
				password = params[1];
				femail = params[2];

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						"http://jiitsimplified.com/clan/server/acceptfriend.php");

				BasicNameValuePair emailBasicNameValuePAir = new BasicNameValuePair(
						"useremail", email);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair(
						"pass", password);
				BasicNameValuePair femailBasicNameValuePAir = new BasicNameValuePair(
						"femail", femail);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(emailBasicNameValuePAir);
				nameValuePairList.add(passwordBasicNameValuePAir);
				nameValuePairList.add(femailBasicNameValuePAir);

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
				dialog.hide();
				Log.d("Sending Invite_Send Friend Request", ""+result);
				JSONObject getResult;
				try {
					getResult = new JSONObject(result);
					boolean isfriendAdded = getResult.getInt("result") == 1 ? true
							: false;

					if (isfriendAdded) {
						Toast.makeText(activity.getApplicationContext(),
								"Friend Added", Toast.LENGTH_LONG)
								.show();
						
						DbHelper dbHelper = new DbHelper(activity.getApplicationContext());
						SQLiteDatabase db = dbHelper.getReadableDatabase();
						Cursor cursor;
						try {
							String sql = "SELECT * FROM " + DbHelper.TABLE1 + " where "+DbHelper.email+"='"+femail+"'";

							Cursor mCur = db.rawQuery(sql, null);
							if (mCur != null) {
								mCur.moveToFirst();
							}
							cursor = mCur;
						} catch (SQLException mSQLException) {
							Log.e("Family Friend", "getTestData >>" + mSQLException.toString());
							throw mSQLException;
						}
						 AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
							alarm.setRepeatingTimer(activity);

						String firstName = cursor.getString(1);
						String lastName = cursor.getString(2);
						String email = cursor.getString(3);
						String latitude = cursor.getString(5);
						String longitude = cursor.getString(6);
						String lastseen = cursor.getString(8);
						String place = cursor.getString(7);
						
						Intent intent = new Intent(activity,
								FriendTimeLine.class);
						Bundle extras = new Bundle();
						extras.putString("firstName", firstName);
						extras.putString("lastName", lastName);
						extras.putString("email", email);
						extras.putDouble("latitude", Double.parseDouble(latitude));
						extras.putDouble("longitude", Double.parseDouble(longitude));
						extras.putString("lastseen", lastseen);
						intent.putExtras(extras);
						activity.startActivity(intent);								
						
					} else {
						boolean isError = getResult.getInt("error") == 1 ? true
								: false;
						int errorCode = getResult.getInt("errorcode");
						switch (errorCode) {
						case 43:
							Toast.makeText(
									activity.getApplicationContext(),
									"Friend Request Accepted. Already a friend.",
									Toast.LENGTH_LONG).show();
							//v.setVisibility(View.GONE);
							break;
						default:
							Toast.makeText(
									activity.getApplicationContext(),
									"Unable to Accept Friend Request. Kindly,try again later",
									Toast.LENGTH_LONG).show();
							break;
						}
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
				dialog = new ProgressDialog(activity);
				dialog.setMessage("Please Wait");
				dialog.show();
			}
		}
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(activity.getApplicationContext());
		boolean ifLogged = prefs.getBoolean("ifLogged", false);
			String email = prefs.getString("email", "");
			String password = prefs.getString("password", "");
		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute(email, password, femail);
	}

}
