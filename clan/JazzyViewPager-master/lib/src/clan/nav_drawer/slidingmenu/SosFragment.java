package clan.nav_drawer.slidingmenu;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import clan.main.R;
import clan.sos.AddContacts;
import clan.sos.Contacts;
import clan.sos.DbHelper;

public class SosFragment extends Fragment {
	
	private static final String TAG = "_tag:";
	
	DbHelper dbHelper = null;
	SQLiteDatabase db = null;
	
	Context context = null;
	Contacts myobject = null;
	View rootView = null;
	
	public SosFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.sos_main, container, false);
        context = getActivity();
        
        callingFunctions();
        return rootView;
    }
	
	
	private void callingFunctions() {
		
		OnClickStartChatHead(); // Start Chat Head
		OnClickViewContacts();  // View Contacts
		OnClickAddContactsButton(); // Add Contacts to the list 
		OnClicksendSMSMessage(); // Send SMS to All Contacts
	
	}

	private void OnClickStartChatHead() {
		// TODO Auto-generated method stub
		Button startChadHed = (Button) rootView.findViewById(R.id.startSosChatHead);
		startChadHed.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(),clan.chathead.MainActivity.class);
				startActivity(i);	
			}
		});
	}

	public void OnClicksendSMSMessage() {
		Log.i("Send SMS", " Sending SMS ");

		Button Sos = (Button) rootView.findViewById(R.id.buttonSOS);
		Sos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String phoneNo = null;
				int columnIndex = 0;
				String message = "SOS Emergency";
	
				DbHelper dbHelper = new DbHelper(context);
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				
				Cursor c = db.query(DbHelper.TABLE, new String[] { DbHelper._ID,
								DbHelper.Noti_Name, DbHelper.phone_No },
								null, null, null, null, null);
				
				// Checking if there is any contacts or not
				if(c.getCount() == 0){
					Toast.makeText(context,"No Contacts", Toast.LENGTH_SHORT).show();
				}
				else{
					while (c.moveToNext()) {
						columnIndex = c.getColumnIndex(dbHelper.phone_No);
						phoneNo = c.getString(columnIndex);
						
						try {
							Toast.makeText(context, "Sending SOS..",Toast.LENGTH_SHORT).show();
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(phoneNo, null, message, null,null);
							Toast.makeText(context, "SMS sent.",Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							Toast.makeText(context,"SMS faild, please try again.", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				}
			}
		});

	}

	public void OnClickAddContactsButton() {
		// TODO Auto-generated method stub
		Button viewContact = (Button) rootView.findViewById(R.id.buttonAddContact);
		viewContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Add Contact",Toast.LENGTH_SHORT).show();
				Intent i = new Intent(context, AddContacts.class);
				startActivity(i);
			}
		});
	}

	public void OnClickViewContacts() {
		Button viewContact = (Button) rootView.findViewById(R.id.buttonViewContacts);
		viewContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(context, "All Contacts",Toast.LENGTH_SHORT).show();
				Intent i = new Intent(context, Contacts.class);
				startActivity(i);
			}
		});
	}
}
