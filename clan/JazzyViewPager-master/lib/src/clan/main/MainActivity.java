package clan.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import clan.login.SignUp;
import clan.main.JazzyViewPager.TransitionEffect;

public class MainActivity extends Activity implements OnClickListener {

	private JazzyViewPager mJazzy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupJazziness(TransitionEffect.ZoomIn);
		
		callingFunctions();
	
	}

	private void callingFunctions() {
		// TODO Auto-generated method stub
		checkIfLoggedIn();
		OnSigupClick();
		onLoginClick();
		onGPlusLogin();
	}
	
	public boolean checkConnection() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) 
                for (int i = 0; i < info.length; i++) 
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
	}
	
	private void checkIfLoggedIn() {
		
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean ifLogged = prefs.getBoolean("ifLogged", false);
		if(ifLogged){
			Intent i = new Intent(this,clan.nav_drawer.slidingmenu.MainActivity.class);
			startActivity(i);			
			finish();
		}
	}

	private void onGPlusLogin() {
		// TODO Auto-generated method stu
		ImageButton ib = (ImageButton) findViewById(R.id.gPlusLoginButton);
		ib.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(checkConnection()){
					Intent i = new Intent(MainActivity.this,clan.gPlusLogin.GPlus_Login.class);
					startActivity(i);
				}
				else{
					Toast.makeText(getApplicationContext(), "Check Your Connection", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void OnSigupClick() {

		Button signup = (Button) findViewById(R.id.button1);
		signup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), "SignUp",Toast.LENGTH_SHORT).show();
				if(checkConnection()){
					Intent i = new Intent(MainActivity.this,
							clan.login.SignUp.class);
					startActivity(i);
				}
				else{
					Toast.makeText(getApplicationContext(), "Check Your Connection", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Toggle Fade");
		String[] effects = this.getResources().getStringArray(
				R.array.jazzy_effects);
		for (String effect : effects)
			menu.add(effect);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equals("Toggle Fade")) {
			mJazzy.setFadeEnabled(!mJazzy.getFadeEnabled());
		} else {
			// TransitionEffect effect =
			// TransitionEffect.valueOf(item.getTitle().toString());
			// setupJazziness(effect);
		}
		return true;
	}

	private void setupJazziness(TransitionEffect effect) {

		List<Integer> list = new ArrayList<Integer>();
		list.add(R.drawable.my_desk);
		list.add(R.drawable.my_friends);
		list.add(R.drawable.connect_all);
		list.add(R.drawable.mobile_layers);

		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(effect);
		mJazzy.setAdapter(new MainAdapter(list));
		mJazzy.setPageMargin(30);
	}

	private class MainAdapter extends PagerAdapter {

		List<Integer> list;

		public MainAdapter(List<Integer> list) {
			this.list = list;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {

			ImageView imageView = new ImageView(MainActivity.this);
			imageView.setImageResource(list.get(position));
			// imageView.setPadding(30, 30, 30, 30);

			TextView text = new TextView(MainActivity.this);
			text.setGravity(Gravity.CENTER);
			text.setTextSize(30);
			text.setTextColor(Color.WHITE);
			text.setText("Page " + position);
			text.setPadding(30, 30, 30, 30);
			int bg = Color.rgb((int) Math.floor(Math.random() * 128) + 64,
					(int) Math.floor(Math.random() * 128) + 64,
					(int) Math.floor(Math.random() * 128) + 64);
			text.setBackgroundColor(bg);
			container.addView(imageView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			mJazzy.setObjectForPosition(imageView, position);

			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			container.removeView(mJazzy.findViewFromObject(position));
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof OutlineContainer) {
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}
	}

	

	// //Code of Login Activity (Dialog Box)
	private EditText etEmail, etPassword;
	Dialog d;

	private void onLoginClick() {

		Button signup = (Button) findViewById(R.id.button2);
		signup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(checkConnection()){
					//Toast.makeText(getApplicationContext(), "Login",Toast.LENGTH_SHORT).show();
					Intent i = new Intent(MainActivity.this, clan.login.Login.class);
					// startActivity(i);

					d = new Dialog(MainActivity.this);
					d.requestWindowFeature(Window.FEATURE_NO_TITLE);
					d.setContentView(R.layout.login_login_dialog_activity);

					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(d.getWindow().getAttributes());
					lp.width = WindowManager.LayoutParams.MATCH_PARENT;
					lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
					d.getWindow().setAttributes(lp);

					d.show();

					etEmail = (EditText) d.findViewById(R.id.login_login_et_email);
					etPassword = (EditText) d.findViewById(R.id.login_login_et_password);

					Button login = (Button) d.findViewById(R.id.login_login_button_login);
					login.setOnClickListener(MainActivity.this);
				}
				else{
					Toast.makeText(getApplicationContext(), "Check Your Connection", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.login_login_button_login:			
			String email = etEmail.getText().toString();
			if(email.length()==0){
				etEmail.setError("Email can't be empty");
				return;
			}
			else if(SignUp.isEmailValid(email)==false){
				etEmail.setError("Email Not Valid");
				return;
			}
			
			String password = etPassword.getText().toString();
			if(password.length()==0){
				etPassword.setError("Password can't be empty");
				return;
			}
			sendPostRequest(email,password);
			d.cancel();
			break;
		}
	}
	
	private void sendPostRequest(String email,String password) {

		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			String email;
			String password;
			@Override
			protected String doInBackground(String... params) {
				email = params[0];
				password = params[1];

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://jiitsimplified.com/clan/server/login.php");

				BasicNameValuePair emailBasicNameValuePAir = new BasicNameValuePair("email", email);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("pass", password);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(emailBasicNameValuePAir);
				nameValuePairList.add(passwordBasicNameValuePAir);

				try {
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
							nameValuePairList);
					httpPost.setEntity(urlEncodedFormEntity);

					try {
						// HttpResponse is an interface just like HttpPost.
						// Therefore we can't initialize them
						HttpResponse httpResponse = httpClient
								.execute(httpPost);

						// According to the JAVA API, InputStream constructor do
						// nothing.
						// So we can't initialize InputStream although it is not
						// an interface
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
				JSONObject getResult;
				try {
					getResult = new JSONObject(result);
					boolean validUser = getResult.getInt("result")==1?true:false;
					
					if(validUser){
						Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_LONG).show();
						
						final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						SharedPreferences.Editor editor = prefs.edit();
						editor.putBoolean("ifLogged", true);
						editor.putString("email",email);
						editor.putString("password",password);
						editor.commit();
						
						Intent i = new Intent(MainActivity.this,clan.nav_drawer.slidingmenu.MainActivity.class);
						startActivity(i);
						finish();
					}
					else{
						boolean isError = getResult.getInt("error")==1?true:false;
						int errorCode = getResult.getInt("errorcode");
						switch(errorCode){
						case 11:
							// The user is not registered. User is new. Show sign up page.
							//Intent i = new Intent(MainActivity.this,clan.login.SignUp.class);
							//startActivity(i);
							Toast.makeText(getApplicationContext(), "User and Password do not match. If not Registered, Register with us first.",Toast.LENGTH_LONG).show();
							break;
							
						default:
							Toast.makeText(getApplicationContext(), "Unable to Sign In. Kindly,try again later",Toast.LENGTH_LONG).show();
							break;
						}
						//String errormsg = getResult.getString("errormsg");
						//Toast.makeText(getApplicationContext(), "ErrorCode::"+errorCode, Toast.LENGTH_LONG).show();
					}	
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			ProgressDialog dialog;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog = new ProgressDialog(MainActivity.this);
				dialog.setMessage("Please Wait");
				dialog.show();
			}
		}
		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute(email, password);
	}
	
	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "Ring the phone",Toast.LENGTH_SHORT).show();
		Intent i = new Intent(MainActivity.this,clan.ring.phone.MainActivity.class);
		startActivity(i);
		finish();
	}
}
