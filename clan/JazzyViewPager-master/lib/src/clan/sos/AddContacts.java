
package clan.sos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import clan.main.R;


public class AddContacts extends Activity {

	private static final String TAG = "_tag:";
	DbHelper dbHelper = null;
	SQLiteDatabase db = null;
	
	int i = 0;
	
	Contacts myobject;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sos_add_contact);
		
		OnClickOk_add_contacts();
		OnClickCancel();
	}

	private void OnClickOk_add_contacts() {
		
		Button b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dbHelper = new DbHelper(getApplicationContext());
				db = dbHelper.getWritableDatabase();
				
				ContentValues values = new ContentValues();
				
				String UserName,ipaddress;
				
				EditText Name = (EditText) findViewById(R.id.editTextName);
				UserName=Name.getText().toString();
				
				EditText Ip = (EditText) findViewById(R.id.editTextIp);
				ipaddress=Ip.getText().toString();
				
				long time= System.currentTimeMillis();
				i = (int)time;
				values.put(DbHelper._ID,i);
				values.put(DbHelper.Noti_Name, UserName);
				values.put(DbHelper.phone_No, ipaddress);
				db.insertWithOnConflict(DbHelper.TABLE, null, values,
				SQLiteDatabase.CONFLICT_REPLACE);
				
				Toast.makeText(getApplicationContext(), "Contact Added", Toast.LENGTH_SHORT).show();
				
				Intent i = new Intent(AddContacts.this, Contacts.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("flag", "modify");
				startActivity(i);
				finish();
			}
		});
				
	}

	private void OnClickCancel() {
		Button b1 = (Button) findViewById(R.id.button2);
		b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed Called");
		finish();
	}
}
