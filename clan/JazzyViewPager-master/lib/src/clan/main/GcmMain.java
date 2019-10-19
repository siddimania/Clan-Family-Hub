package clan.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMain {
    private Context context;
    GoogleCloudMessaging gcm;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_USER_KEY = "PROPERTY_USER_KEY";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "480218778586";
    static final String TAG = "GCMDemo";
    String regid;

    public GcmMain(Context context){
        this.context = context;

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);

            Toast.makeText(context, "RegId: "+regid, Toast.LENGTH_SHORT).show();
            if (regid.isEmpty()) {
                registerInBackground();
            }
        }
        else{
            // Google Play Service not found. Need a fallback Mechanism.
            Toast.makeText(context, "Problem with Google Play Services.", Toast.LENGTH_LONG).show();
        }
    }
/*   Registers to the server ////*/
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean ifLogged = prefs.getBoolean("ifLogged", false);
		String uEmail="",uPassword="";
		if(ifLogged){
			uEmail = prefs.getString("email", "");
			uPassword = prefs.getString("password", "");
		}
        updateRegistrationId(uEmail,uPassword,getAppVersion(context),regid);
    }

    public void updateRegistrationId(String useremail,String pass,int appVersion,String regKey) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();

        /* login.php returns true if username and password is equal to saranga */
        HttpPost httppost = new HttpPost("http://jiitsimplified.com/clan/server/updategcmRegID.php");
        try {
            // Add user name and password
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("useremail", ""+useremail));
            nameValuePairs.add(new BasicNameValuePair("pass", ""+pass));
            nameValuePairs.add(new BasicNameValuePair("appversion", ""+appVersion));
            nameValuePairs.add(new BasicNameValuePair("regID", regKey));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            Log.w("SENCIDE", "Execute HTTP Post Request");
            HttpResponse response = httpclient.execute(httppost);

            String str = inputStreamToString(response.getEntity().getContent()).toString();
            JSONObject jsonObject = new JSONObject(str);

            boolean goodResponse = jsonObject.getInt("result")==1?true:false;
            if(goodResponse){
                storeRegistrationId(context, regid);
            }
            else{
            	int errorcode = jsonObject.getInt("errorcode");
            	switch(errorcode){
            	case 500:
            		//Log out the user here.
            		
            		break;
            	default:
            		
            		break;
            	}
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        // Read response until the end
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return full string
        return total;
    }

    void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // Persist the regID - no need to register again.
                    //The registration id is persisted after sending the data to server and fetching the response, if successful, stores the data in preference.
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, we will try again on next app session.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
               // Toast.makeText(context,"Device Registered with "+msg,Toast.LENGTH_LONG).show();
                Log.d("GCM", msg);
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
      //  editor.putString(PROPERTY_USER_KEY, userKey);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Must clear registration ID on app update as otherwise, it is not guaranteed to work always.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            /*
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            */ //Our app don't rely on Notification support, so we can go for fallback Mechanism simply.
            return false;
        }
        return true;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private static String getAppName(Context context) {
            Resources appR = context.getResources();
            CharSequence appName = appR.getText(appR.getIdentifier("app_name",
                    "string", context.getPackageName()));
            return appName.toString();
    }



    private SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(GcmMain.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
}
