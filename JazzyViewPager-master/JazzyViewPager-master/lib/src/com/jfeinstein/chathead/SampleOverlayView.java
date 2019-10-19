package com.jfeinstein.chathead;

import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jfeinstein.jazzyviewpager.*;
import com.jfeinstein.jazzyviewpager.MainActivity;


public class SampleOverlayView extends OverlayView {

	private TextView info;
	PlayerService showNotification = new PlayerService();
	
	public SampleOverlayView(OverlayService service) {
		super(service, R.layout.chathead_overlay_sos, 1);
	}

	public int getGravity() {
		return Gravity.TOP + Gravity.CENTER;
	}
	
	@Override
	public void onInflateView() {
		info = (TextView) this.findViewById(R.id.textview_info);
		Button buttonNotify = (Button)findViewById(R.id.button1);
		buttonNotify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//showNotification.checknotification();
				
				com.jfeinstein.jazzyviewpager.SOSMain obj = new com.jfeinstein.jazzyviewpager.SOSMain();
				Log.d("button click", "on click listener");
				Toast.makeText(getContext(),"SOS Sending..", Toast.LENGTH_SHORT).show();
				obj.OnClicksendSMSMessage();
				
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
