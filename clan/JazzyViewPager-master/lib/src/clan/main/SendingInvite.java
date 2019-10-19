package clan.main;

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

import clan.login.SignUp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendingInvite extends Activity {
	EditText etEmail, etName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_a_friend);

		etEmail = (EditText) findViewById(R.id.invite_friend_email);
		etName = (EditText) findViewById(R.id.invite_friend_name);

		Button saveAddAnother = (Button) findViewById(R.id.invite_button_addAnother);
		saveAddAnother.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				boolean ifLogged = prefs.getBoolean("ifLogged", false);
				if (ifLogged && checkField()) {
					String email = prefs.getString("email", "");
					String password = prefs.getString("password", "");
					sendFriendInvite(email, password, etEmail.getText()
							.toString().trim(), etName.getText().toString()
							.trim(), 0);
				}
			}

		});

		Button doneClick = (Button) findViewById(R.id.invite_button_done);
		doneClick.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo ni = cm.getActiveNetworkInfo();
				if (ni != null) {
					final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					boolean ifLogged = prefs.getBoolean("ifLogged", false);
					if (ifLogged && checkField()) {
						String email = prefs.getString("email", "");
						String password = prefs.getString("password", "");
						
						sendFriendInvite(email, password, etEmail.getText()
								.toString().trim(), etName.getText().toString()
								.trim(), 1);
					}
				}
				else{
					Toast.makeText(getApplicationContext(), "Check Your Connection",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private boolean checkField() {
		String name = etName.getText().toString().trim();
		if (name.length() == 0) {
			etName.setError("Name can't be empty");
			return false;
		}

		String email = etEmail.getText().toString().trim();
		if (email.length() == 0) {
			etEmail.setError("Email can't be empty");
			return false;
		} else if (SignUp.isEmailValid(email) == false) {
			etEmail.setError("Email Not Valid");
			return false;
		}
		return true;
	}

	ProgressDialog dialog;

	private void sendFriendInvite(String email, String password, String femail,
			String fname, int toFinish) {

		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			String email;
			String password;

			boolean isfinish = false;

			@Override
			protected String doInBackground(String... params) {
				email = params[0];
				password = params[1];
				String femail = params[2];
				String fname = params[3];
				isfinish = Integer.parseInt(params[4]) == 1 ? true : false;

				/*
				 * dialog=new ProgressDialog(getApplicationContext());
				 * dialog.setMessage("Please Wait");
				 * //dialog.setCancelable(false);
				 * //dialog.setInverseBackgroundForced(true); dialog.show();
				 */
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						"http://jiitsimplified.com/clan/server/addfriend.php");

				BasicNameValuePair emailBasicNameValuePAir = new BasicNameValuePair(
						"useremail", email);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair(
						"pass", password);
				BasicNameValuePair fnameBasicNameValuePAir = new BasicNameValuePair(
						"fname", fname);
				BasicNameValuePair femailBasicNameValuePAir = new BasicNameValuePair(
						"femail", femail);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(emailBasicNameValuePAir);
				nameValuePairList.add(passwordBasicNameValuePAir);
				nameValuePairList.add(fnameBasicNameValuePAir);
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
				Log.d("Sending Invite_Send Friend Request", result);
				JSONObject getResult;
				try {
					getResult = new JSONObject(result);
					boolean isfriendAdded = getResult.getInt("result") == 1 ? true
							: false;

					if (isfriendAdded) {
						Toast.makeText(getApplicationContext(),
								"Friend Request Sent.", Toast.LENGTH_LONG)
								.show();
						if (isfinish)
							finish();

					} else {
						boolean isError = getResult.getInt("error") == 1 ? true
								: false;
						int errorCode = getResult.getInt("errorcode");
						switch (errorCode) {
						case 999:
							Toast.makeText(
									getApplicationContext(),
									"Your friend is not registered with us. Kindly, ask to signup.",
									Toast.LENGTH_LONG).show();
							break;
						default:
							Toast.makeText(
									getApplicationContext(),
									"Unable to Send Friend Request. Kindly,try again later",
									Toast.LENGTH_LONG).show();
							break;
						// String errormsg = getResult.getString("errormsg");
						// Toast.makeText(getApplicationContext(),
						// "ErrorCode::"+errorCode, Toast.LENGTH_LONG).show();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog = new ProgressDialog(SendingInvite.this);
				dialog.setMessage("Please Wait");
				dialog.show();
			}
		}
		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute(email, password, femail, fname, ""
				+ toFinish);
	}

}
