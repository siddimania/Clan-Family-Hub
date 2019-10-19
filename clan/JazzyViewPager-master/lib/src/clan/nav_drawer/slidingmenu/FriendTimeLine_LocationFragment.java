package clan.nav_drawer.slidingmenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import clan.main.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FriendTimeLine_LocationFragment extends Fragment {

	private GoogleMap map;
	private ProgressDialog progress;
	Marker familyloc;
	String femail;
	String name;
	Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.friend_timeline_locationfragment, container, false);
		context = getActivity().getApplicationContext();
		
		Bundle args = getArguments();
		double latitude = args.getDouble("latitude");
		double longitude = args.getDouble("longitude");
		String lastseen = args.getString("lastseen");
		name = args.getString("name");
		femail = args.getString("femail");
		// Toast.makeText(getActivity(), ""+latitude+" "+longitude,
		// Toast.LENGTH_LONG).show();
		
		map = ((SupportMapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		map.setMyLocationEnabled(false);
		updateMap(latitude, longitude, name);

		// Marker kiel = map.addMarker(new MarkerOptions()
		// .position(KIEL)
		// .title("Kiel")
		// .snippet("Kiel is cool")
		// .icon(BitmapDescriptorFactory
		// .fromResource(R.drawable.ic_launcher)));

		Button track = (Button) rootView.findViewById(R.id.trackFamilyMember);
		track.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(context.CONNECTIVITY_SERVICE);
				
				boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
				boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

				if (is3g || isWifi){
					
					final SharedPreferences prefs = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					boolean ifLogged = prefs.getBoolean("ifLogged", false);
					if (ifLogged) {
						String email = prefs.getString("email", "");
						String password = prefs.getString("password", "");
						sendPostRequest(email, password, femail);

						progress = new ProgressDialog(getActivity());
						progress.setMessage("We are trying to work as fast as we can ");
						progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progress.setIndeterminate(true);
						progress.setCancelable(false);
						progress.show();

						Timer t = new Timer(false);
						t.schedule(new TimerTask() {
							@Override
							public void run() {
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										if (progress != null
												&& progress.isShowing()) {
											progress.dismiss();
											Toast.makeText(
													context,
													"Unable to fetch Location. We will try our best to update location asap",
													Toast.LENGTH_LONG).show();
										}
									}
								});

							}
						}, 1000*90);
					}
				}
				else{
					Toast.makeText(context, "Check Your Connection", Toast.LENGTH_SHORT).show();
				}
			}
		});

		return rootView;
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			// String message = arg1.getStringExtra("message");
			if (progress != null) {
				progress.cancel();
				double latitude = arg1.getDoubleExtra("latitude", 50.0d);
				double longitude = arg1.getDoubleExtra("longitude", 50.0d);
				updateMap(latitude, longitude, name);
				Toast.makeText(getActivity(), "Location Updated.", Toast.LENGTH_LONG).show();
			}
		}
	};

	private void updateMap(double latitude, double longitude, String name) {
		map.clear();
		LatLng floc = new LatLng(latitude, longitude);
		familyloc = map.addMarker(new MarkerOptions().position(floc)
				.title(name));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(floc, 15));
		map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mMessageReceiver, new IntentFilter("location_updated"));
	}

	@Override
	public void onPause() {
		// Unregister since the activity is not visible
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		Fragment f = (Fragment) getFragmentManager().findFragmentById(R.id.map);
		if (f != null) {
			getFragmentManager().beginTransaction().remove(f).commit();
		}
		super.onDestroyView();
	}

	private void sendPostRequest(String email, String password, String femail) {
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {

				String email = params[0];
				String password = params[1];
				String femail = params[2];

				HttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(
						"http://jiitsimplified.com/clan/server/getnewlocation.php");

				BasicNameValuePair fNameBasicNameValuePair = new BasicNameValuePair(
						"useremail", email);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair(
						"pass", password);
				BasicNameValuePair lNameBasicNameValuePAir = new BasicNameValuePair(
						"femail", femail);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(fNameBasicNameValuePair);
				nameValuePairList.add(lNameBasicNameValuePAir);
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
				Log.d("Signup", "On Post Execute");
				JSONObject getResult;
				try {
					getResult = new JSONObject(result);
					boolean registered = getResult.getInt("result") == 1 ? true
							: false;

					if (registered) {
						Toast.makeText(getActivity(),
								"New Location request sent", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(getActivity(),
								"New Location request sent:" + result,
								Toast.LENGTH_LONG).show();
						int errorCode = getResult.getInt("errorcode");
						switch (errorCode) {
						case 500:
							// Log out the user.
							// Toast.makeText(getApplicationContext(),
							// "UserName and Password Do not match",Toast.LENGTH_LONG).show();
							break;
						default:
							// Toast.makeText(getApplicationContext(),
							// "Not all values set",Toast.LENGTH_LONG).show();
							break;
						}
						// String errormsg = getResult.getString("errormsg");
						// Toast.makeText(getApplicationContext(),
						// "ErrorCode::"+errorCode, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		// Toast.makeText(getApplicationContext(),
		// "string sendpost request",Toast.LENGTH_LONG).show();
		sendPostReqAsyncTask.execute(email, password, femail);
	}

	public static FriendTimeLine_LocationFragment newInstance(String name,
			double lat, double longitude, String lastseen, String femail) {
		final FriendTimeLine_LocationFragment quoteFragment = new FriendTimeLine_LocationFragment();
		final Bundle args = new Bundle();
		args.putString("name", name);
		args.putDouble("latitude", lat);
		args.putDouble("longitude", longitude);
		args.putString("lastseen", lastseen);
		args.putString("femail", femail);
		quoteFragment.setArguments(args);
		return quoteFragment;
	}
}
