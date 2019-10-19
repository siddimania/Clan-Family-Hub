package clan.chathead;

import java.net.URL;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import clan.main.R;
import clan.sos.DbHelper;

public class SampleOverlayView extends OverlayView {

	private TextView info;
	SOS_Service showNotification = new SOS_Service();
	
	public SampleOverlayView(OverlayService service) {
		super(service, R.layout.chathead_overlay_sos, 1);
	}

	public int getGravity() {
		return Gravity.TOP + Gravity.CENTER;
	}
	
	@Override
	public void onInflateView() {
		info = (TextView) this.findViewById(R.id.textview_info);
		Button buttonNotify = (Button)findViewById(R.id.windowManagerSOS);
		buttonNotify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//showNotification.checknotification();
				Log.d("button click", "on click listener");
				sendSmsMessage();
			}

			private void sendSmsMessage() {
				// TODO Auto-generated method stub
				String phoneNo = null;
				int columnIndex= 0;
				String message = "Need Your Help";
				
				DbHelper dbHelper = new DbHelper(getContext());
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				
				Cursor c = db
						.query(DbHelper.TABLE, new String[] { DbHelper._ID,
								DbHelper.Noti_Name, DbHelper.phone_No },
								null, null, null, null, null);
				
				// Checking if there is any contacts or not
				if(c.getCount() == 0)
					Toast.makeText(getContext(),"No Contacts", Toast.LENGTH_SHORT).show();
				else{
					while (c.moveToNext()) {
						columnIndex = c.getColumnIndex(dbHelper.phone_No);
						phoneNo = c.getString(columnIndex);
						
						try {
							Toast.makeText(getContext(), "Sending SOS..",Toast.LENGTH_SHORT).show();
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(phoneNo, null, message, null,null);
							Toast.makeText(getContext(), "SMS sent.",Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							Toast.makeText(getContext(),"SMS faild, please try again.", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				}
			}
		});
	}
	
	@Override
	protected void refreshViews() {
		info.setText("SOS Wall");
	}

	@Override
	protected void onTouchEvent_Up(MotionEvent event) {
		info.setText("UP\nPOINTERS: " + event.getPointerCount());
	}

	@Override
	protected void onTouchEvent_Move(MotionEvent event) {
		info.setText("MOVE\nPOINTERS: " + event.getPointerCount());
	}

	@Override
	protected void onTouchEvent_Press(MotionEvent event) {
		info.setText("DOWN\nPOINTERS: " + event.getPointerCount());
	}

	@Override
	public boolean onTouchEvent_LongPress() {
		info.setText("LONG\nPRESS");
		return true;
	}
}
