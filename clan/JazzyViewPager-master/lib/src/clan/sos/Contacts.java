/*
@author: siddhartha dimania
 */
package clan.sos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import clan.main.R;

public class Contacts extends Activity {

	private static final String TAG = "_tag:";
	DbHelper dbHelper;
	SQLiteDatabase db;

	public static String ipaddress = null;
	public static String name = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sos_contacts);

		gettingListView();
		OnClickAddContacts();
	}

	private void OnClickAddContacts() {

		ImageView im = (ImageView) findViewById(R.id.imageView2);
		im.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(getApplicationContext(), "Add a new Contact",
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent(Contacts.this, AddContacts.class);
				startActivity(i);
			}
		});

	}

	private void deleteListItem(int position) {

		ListView tv = (ListView) findViewById(R.id.ContactsList);
		int columnIndex = 0;
		int dbsno = 0;
		
		Cursor c = db.query(DbHelper.TABLE, new String[] { DbHelper._ID,
				DbHelper.Noti_Name, DbHelper.phone_No }, null, null,
				null, null, null);
		
		while (position >= 0) {
			c.moveToNext();
			columnIndex = c.getColumnIndex(dbHelper._ID);
			dbsno = c.getInt(columnIndex);
			Log.d("while running", "starting..." + dbsno);
			position--;

		}
		db.delete(dbHelper.TABLE, dbHelper._ID + "=" + dbsno, null);

		Cursor cursor = db.query(DbHelper.TABLE, new String[] { DbHelper._ID,
				DbHelper.Noti_Name, DbHelper.phone_No, }, null, null,
				null, null, null);

		startManagingCursor(cursor);
		SimpleCursorAdapter adapter;

		String[] from = { dbHelper.Noti_Name, dbHelper.phone_No };
		int[] to = { R.id.heading, R.id.description };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.sos_list_view, cursor, from, to, 0);
		tv.setAdapter(adapter);
		Toast.makeText(getApplicationContext(), "Contact deleted",Toast.LENGTH_SHORT).show();

	}

	public int gettingListView() {

		dbHelper = new DbHelper(getApplicationContext());
		db = dbHelper.getReadableDatabase();
		
		Cursor cursor = null;
		try {

			ListView tv = (ListView) findViewById(R.id.ContactsList);
			cursor = db.query(DbHelper.TABLE, new String[] { DbHelper._ID,
					DbHelper.Noti_Name, DbHelper.phone_No }, null, null,
					null, null, null);

			startManagingCursor(cursor);
			SimpleCursorAdapter adapter;

			String[] from = { dbHelper.Noti_Name, dbHelper.phone_No };
			int[] to = { R.id.heading, R.id.description };

			adapter = new SimpleCursorAdapter(getApplicationContext(),R.layout.sos_list_view, cursor, from, to, 0);

			View empty = findViewById(R.id.empty);
			tv.setEmptyView(empty);
			tv.setAdapter(adapter);
			tv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// Cursor c;
					final int positionGlobal = position;
					Log.d("Globla positon::", ""+positionGlobal);
					new AlertDialog.Builder(Contacts.this,
							AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
							.setTitle("Link")
							.setIcon(R.drawable.ic_launcher)
							.setMessage("Delete Contact: ")

							.setNeutralButton("Ok",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											deleteListItem(positionGlobal);
										}
									})
							.create().show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor != null)
			return cursor.getCount();
		else
			return 0;
	}

	@Override
	public void onBackPressed() {

		Log.d(TAG, "onBackPressed Called");
		dbHelper.close();
		db.close();
		finish();
	}
}
