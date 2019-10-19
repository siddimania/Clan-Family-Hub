package clan.login;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import clan.main.R;
import clan.main.SendingInvite;

public class SignUp extends Activity implements OnClickListener {
	private String SHAHash;
	public static int NO_OPTIONS = 0;
	private static final int SELECT_PICTURE_GALLERY = 1;
	private static final int SELECT_PICTURE_CAMERA = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_signup_activity);

		((Button) findViewById(R.id.login_signup_button_save)).setOnClickListener(this);
		((ImageView) findViewById(R.id.login_signup_imageview_addphoto)).setOnClickListener(this);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if (extras != null) {
		    if (extras.getString("firstName") != null) {
		    	String firstName = extras.getString("firstName");
		    	EditText editTextFirstName = (EditText)findViewById(R.id.login_signup_et_fname);
		    	editTextFirstName.setText(firstName);
		    	
		    }
		    if (extras.getString("lastName") != null) {
		    	String lastName = extras.getString("lastName");
		    	EditText editTextLastName = (EditText)findViewById(R.id.login_signup_et_lname);
		    	editTextLastName.setText(lastName);
		    }
		    if (extras.getString("email") != null) {
		    	String email = extras.getString("email");
		    	EditText editTextEmail = (EditText)findViewById(R.id.login_signup_et_email);
		    	editTextEmail.setText(email);
		    }
		}
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.login_signup_button_save:
			registerme();
			break;
		// case R.id.login_signup_imagebutton_addphoto:
		case R.id.login_signup_imageview_addphoto:
			showPhotoDialog();
			break;
		case R.id.login_signup_dialog_photo_button_gallery: {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"),
					SELECT_PICTURE_GALLERY);
		}
			break;
		case R.id.login_signup_dialog_photo_button_pictake:
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent,SELECT_PICTURE_CAMERA);
			break;

		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE_GALLERY) {
				Uri selectedImageUri = data.getData();
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), selectedImageUri);
					setBitmap(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if (requestCode == SELECT_PICTURE_CAMERA) {
				Bitmap bp = (Bitmap) data.getExtras().get("data");
				setBitmap(bp);
			}
			d.cancel();
		}
	}
	Bitmap bp;
	private void setBitmap(Bitmap bitmap){
		bp=bitmap;
		ImageView imView = (ImageView) findViewById(R.id.login_signup_imageview_addphoto);
		imView.setImageBitmap(bitmap);
	}
	Dialog d;
	private void showPhotoDialog() {
		 d = new Dialog(this,
				android.R.style.Theme_Black_NoTitleBar_Fullscreen);

		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.login_signup_dialog_photo);

		ColorDrawable dialogColor = new ColorDrawable(Color.WHITE);
		dialogColor.setAlpha(60);
		d.getWindow().setBackgroundDrawable(dialogColor);
		d.show();

		((Button) d.findViewById(R.id.login_signup_dialog_photo_button_gallery))
				.setOnClickListener(this);
		((Button) d.findViewById(R.id.login_signup_dialog_photo_button_pictake))
				.setOnClickListener(this);
	}

	private void registerme() {
		
		// checkconnectivity();
		
		EditText etFName = (EditText) findViewById(R.id.login_signup_et_fname);
		String fname = etFName.getText().toString();
		if(fname.length()==0){
			etFName.setError("First Name cannot  be empty");
		return ;	
		}
		
		EditText etLName = (EditText) findViewById(R.id.login_signup_et_lname);
		String lname = etLName.getText().toString();
		if(lname.length()==0){
			etLName.setError("Last Name cannot  be empty");
		return ;	
		}
		
		EditText etEmail = (EditText) findViewById(R.id.login_signup_et_email);
		String email = etEmail.getText().toString();
		if(email.length()==0){
			etEmail.setError("Email cannot  be empty");
		return ;	
		}
		else if(isEmailValid(email)==false){
			etEmail.setError("Kindly enter valid email address");
			return;
		}
		
		EditText etPass = (EditText) findViewById(R.id.login_signup_et_password);
		String password = etPass.getText().toString();
		if(password.length()<6){
			etPass.setError("Password should be atleast 6 characters long");
		return ;	
		}else if(password.length()>30){
			etPass.setError("Password cannot be more than 30 characters long");
		return ;	
		}
		
		if(bp==null){
			Toast.makeText(this, "Please upload your photo too", Toast.LENGTH_LONG).show();
			return;
		}
		
			String pic="";
			
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bp.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
		        byte [] byte_arr = stream.toByteArray();
		         pic = Base64.encodeToString(byte_arr, Base64.DEFAULT);
		        //Toast.makeText(getApplicationContext(), "IMage:"+pic, Toast.LENGTH_LONG).show();
        	sendPostRequest(fname, lname, email, password,pic);
	}
	
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;
	 
	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;
	 
	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    } 
	    return isValid;
	} 

	private void sendPostRequest(String fname, String lname, String email,String password,String pic) {
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {

				String fName = params[0];
				String lName = params[1];
				String email = params[2];
				String password = params[3];
				String picture = params[4];

				HttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost("http://jiitsimplified.com/clan/server/signup.php");

				BasicNameValuePair fNameBasicNameValuePair = new BasicNameValuePair("fname", fName);
				BasicNameValuePair lNameBasicNameValuePAir = new BasicNameValuePair("lname", lName);
				BasicNameValuePair emailBasicNameValuePAir = new BasicNameValuePair("email", email);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("pass", password);
				BasicNameValuePair pictureNameValuePAir = new BasicNameValuePair("pic", picture);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(fNameBasicNameValuePair);
				nameValuePairList.add(lNameBasicNameValuePAir);
				nameValuePairList.add(emailBasicNameValuePAir);
				nameValuePairList.add(passwordBasicNameValuePAir);
				nameValuePairList.add(pictureNameValuePAir);
				Log.d("Signup","Basic Name Value Pair");
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
				dialog.hide();
				Log.d("Signup","On Post Execute");
				JSONObject getResult;
				try {
					getResult = new JSONObject(result);
					boolean registered = getResult.getInt("result")==1?true:false;
					
					if(registered){
						Toast.makeText(getApplicationContext(),"Registration Successful, You may  Login Now", Toast.LENGTH_LONG).show();
						finish();
					}
					else{
						boolean isError = getResult.getInt("error")==1?true:false;
						int errorCode = getResult.getInt("errorcode");
						switch(errorCode){
						case 11:
							// The user is not registered. User is new. Show sign up page.
							finish();
							Toast.makeText(getApplicationContext(), "User is already registered. Please Login",Toast.LENGTH_LONG).show();
							break;
						default:
							Toast.makeText(getApplicationContext(), "Unable to Sign Up. Kindly,try again later",Toast.LENGTH_LONG).show();
							break;
						}
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
				dialog = new ProgressDialog(SignUp.this);
				dialog.setMessage("Please Wait");
				dialog.show();
			}
		}
		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute(fname, lname, email, password,pic);
	}
	
}
