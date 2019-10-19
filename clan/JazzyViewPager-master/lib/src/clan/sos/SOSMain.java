package clan.sos;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import clan.main.R;

public class SOSMain extends Activity {

	private static final String TAG = "_tag:";
	
	DbHelper dbHelper = null;
	SQLiteDatabase db = null;
	
	int i = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sos_main);

		OnClickAddContactsIcon();
		OnClickViewContacts();
		OnClickAddContactsButton();
		OnClicksendSMSMessage();

	}

	public void OnClicksendSMSMessage() {
		Log.i("Send SMS", "");

		Button Sos = (Button) findViewById(R.id.buttonSOS);
		Sos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String phoneNo = null;
				int columnIndex= 0;
				String message = "Need Your Help";
				
				DbHelper dbHelper = new DbHelper(getApplicationContext());
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				
				Cursor c = db
						.query(DbHelper.TABLE, new String[] { DbHelper._ID,
								DbHelper.Noti_Name, DbHelper.phone_No },
								null, null, null, null, null);
				
				// Checking if there is any contacts or not
				if(c.getCount() == 0){
					Toast.makeText(getApplicationContext(),"No Contacts", Toast.LENGTH_SHORT).show();
				}
				else{
					while (c.moveToNext()) {
						columnIndex = c.getColumnIndex(dbHelper.phone_No);
						phoneNo = c.getString(columnIndex);
						
						try {
							Toast.makeText(getApplicationContext(), "Sending SOS..",Toast.LENGTH_SHORT).show();
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(phoneNo, null, message, null,null);
							Toast.makeText(getApplicationContext(), "SMS sent.",Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(),"SMS faild, please try again.", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	public void OnClickAddContactsButton() {
		// TODO Auto-generated method stub
		Button viewContact = (Button) findViewById(R.id.buttonAddContact);
		viewContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(SOSMain.this, AddContacts.class);
				startActivity(i);
			}
		});
	}

	public void OnClickViewContacts() {
		Button viewContact = (Button) findViewById(R.id.buttonViewContacts);
		viewContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(SOSMain.this, Contacts.class);
				startActivity(i);
			}
		});
	}

	public void OnClickAddContactsIcon() {

		ImageView im = (ImageView) findViewById(R.id.imageView2);
		im.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(SOSMain.this, AddContacts.class);
				startActivity(i);
			}
		});

	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed Called");
		finish();
	}
}
