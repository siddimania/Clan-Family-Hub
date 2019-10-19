package clan.chathead;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import clan.main.R;
@SuppressWarnings("deprecation")
public class SOS_Service extends Service {
	
	private static final int TRAY_HIDDEN_FRACTION 			= 6; 	// Controls fraction of the tray hidden when open
	private static final int TRAY_MOVEMENT_REGION_FRACTION 	= 7;	// Controls fraction of y-axis on screen within which the tray stays.
	private static final int ANIMATION_FRAME_RATE 			= 30;	// Animation frame rate per second.
	private static final int TRAY_DIM_X_DP 					= 55;	// Width of the tray in dps
	private static final int TRAY_DIM_Y_DP 					= 55; 	// Height of the tray in dps
	
	private WindowManager 				mWindowManager;				// Reference to the window
	private WindowManager.LayoutParams 	mRootLayoutParams;			// Parameters of the root layout
	private RelativeLayout 				mRootLayout;				// Root layout
	private RelativeLayout 				mContentContainerLayout;	// Contains everything other than buttons and song info
	private RelativeLayout 				mLogoLayout;				// Contains image
	private RelativeLayout 				mCoverLayout;				// Contains cover
	private RelativeLayout 				mCoverHelperLayout;			// Contains cover of the previous song. This helps with fade animations.
	private RelativeLayout 				mNotification;
	
	// Variables that control drag
	private int mStartDragX;
	private int mPrevDragX;
	private int mPrevDragY;
	
	private boolean mIsTrayOpen = true;
	private static int checkClicks=0;
	
	// Controls for animations
	private Timer 					mTrayAnimationTimer;
	private TrayAnimationTimerTask 	mTrayTimerTask;
	private Handler 				mAnimationHandler = new Handler();
	
	public static View myView = null;
	
	//object closeService
	closeService obj; 
	MainActivity getHeight = new MainActivity();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		obj = new closeService();
		
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		mRootLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.chat_head, null);
		mContentContainerLayout = (RelativeLayout) mRootLayout.findViewById(R.id.content_container);
		mContentContainerLayout.setOnTouchListener(new TrayTouchListener());
		
		mLogoLayout = (RelativeLayout) mRootLayout.findViewById(R.id.logo_layout);
		mCoverLayout = (RelativeLayout) mRootLayout.findViewById(R.id.cover_layout);
		mCoverHelperLayout = (RelativeLayout) mRootLayout.findViewById(R.id.cover_helper_layout);
		mNotification = (RelativeLayout) mRootLayout.findViewById(R.id.notificationmy);
		
		mRootLayoutParams = new WindowManager.LayoutParams(
				Utils.dpToPixels(TRAY_DIM_X_DP, getResources()),
				Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE, 
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, 
				PixelFormat.TRANSLUCENT);

		mRootLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowManager.addView(mRootLayout, mRootLayoutParams);
		
		mRootLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				RelativeLayout.LayoutParams params;
				Bitmap bmap;
				int containerNewWidth = 50;
								
				bmap = Utils.loadMaskedBitmap(mCoverLayout.getHeight(), containerNewWidth);
				params = (RelativeLayout.LayoutParams) mCoverLayout.getLayoutParams();
				params.width = (bmap.getWidth() * mCoverLayout.getHeight()) / bmap.getHeight();
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
				mCoverLayout.setLayoutParams(params);
				mCoverLayout.requestLayout();
				mCoverHelperLayout.setLayoutParams(params);
				mCoverHelperLayout.requestLayout();
				mCoverLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), bmap));	
				
				params = new RelativeLayout.LayoutParams(50,30);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				params.leftMargin = mRootLayout.getWidth()-33;
				//mNotification.setLayoutParams(params);
				
				// Setup the root layout
				mRootLayoutParams.x = -mLogoLayout.getLayoutParams().width;
				mRootLayoutParams.y = (getApplicationContext().getResources().getDisplayMetrics().heightPixels-mRootLayout.getHeight()) / 6;
				mWindowManager.updateViewLayout(mRootLayout, mRootLayoutParams);
				
				// Make everything visible
				mRootLayout.setVisibility(View.VISIBLE);
				
				// Animate the Tray
				mTrayTimerTask = new TrayAnimationTimerTask();
				mTrayAnimationTimer = new Timer();
				mTrayAnimationTimer.schedule(mTrayTimerTask, 0, ANIMATION_FRAME_RATE);
			}
		}, ANIMATION_FRAME_RATE);
		
	}

	
	public void checknotification(){
		View view= mContentContainerLayout.findViewById(R.id.notificationmy);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
		if (mRootLayout != null)
			mWindowManager.removeView(mRootLayout);
	}

	
	private final int MAX_CLICK_DURATION = 140;
    private long startClickTime=0;
	
	private void dragTray(int action, int x, int y,View v){
		myView = v;
		switch (action){
		
		case MotionEvent.ACTION_DOWN:
	
			if (mTrayTimerTask!=null){
				mTrayTimerTask.cancel();
				mTrayAnimationTimer.cancel();
			}
			// Store the start points
			mStartDragX = x;
			//mStartDragY = y;
			mPrevDragX = x;
			mPrevDragY = y;
			startClickTime = Calendar.getInstance().getTimeInMillis();
			break;
			
		case MotionEvent.ACTION_OUTSIDE:
			Toast.makeText(getApplicationContext(), "this", Toast.LENGTH_LONG).show();
			stopService(new Intent(this,SampleOverlayService.class));
    		checkClicks=0;
			break;    	
			
		case MotionEvent.ACTION_MOVE:
			obj.show();
			float deltaX = x-mPrevDragX;
			float deltaY = y-mPrevDragY;
			mRootLayoutParams.x += deltaX;
			mRootLayoutParams.y += deltaY;
			mPrevDragX = x;
			mPrevDragY = y;
			mWindowManager.updateViewLayout(mRootLayout, mRootLayoutParams);
			break;
			
		case MotionEvent.ACTION_UP:
			if( x > (getHeight.width)/2-50 && x < (getHeight.width)/2 + 60 && y > getHeight.height-100 && y < getHeight.height ){
				closeAllServices();
				break;
			}
			long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
			if(mRootLayoutParams.gravity== Gravity.BOTTOM){
				Toast.makeText(getApplicationContext(), "this is bottom", Toast.LENGTH_SHORT).show();
			}
            if(clickDuration < MAX_CLICK_DURATION ) {
            	if(checkClicks == 0){
            		mContentContainerLayout.removeView(mNotification);
            		startService(new Intent(this, SampleOverlayService.class));
            		Intent intent = new Intent(SOS_Service.this,fullScreenActivity.class);
            		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		startActivity(intent);
            		checkClicks = 1;
            	}
            	else {
            		stopSampleOverlayService();
            	}            	
			}
            obj.hide();
            
		case MotionEvent.ACTION_CANCEL:
			mTrayTimerTask = new TrayAnimationTimerTask();
			mTrayAnimationTimer = new Timer();
			mTrayAnimationTimer.schedule(mTrayTimerTask, 0, ANIMATION_FRAME_RATE);
			break;
		
		}
	}
	
	public void stopSampleOverlayService() {
		
		stopService(new Intent(SOS_Service.this,SampleOverlayService.class));
		updateCheckClicks();
	
	}

	public void updateCheckClicks() {
		checkClicks=0;
	}

	private void closeAllServices(){
		stopService(new Intent(SOS_Service.this,SampleOverlayService.class));
		stopService(new Intent(SOS_Service.this,SOS_Service.class));
		
		obj.hide();
		
		stopService(new Intent(SOS_Service.this,closeService.class));
		Vibrator vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    vibs.vibrate(150);    
	
	}
	
	public class TrayTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getActionMasked();
			switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_UP:
					dragTray(action, (int)event.getRawX(), (int)event.getRawY(),v);
				case MotionEvent.ACTION_CANCEL:
					break;
				default:
					return false;
			}
			return true;
		}	
	}
	
	private class TrayAnimationTimerTask extends TimerTask{
		
		// Ultimate destination coordinates toward which the tray will move
		int mDestX;
		int mDestY;
		
		public TrayAnimationTimerTask(){
			
			// Setup destination coordinates based on the tray state. 
			super();
			if (!mIsTrayOpen){
				mDestX = -mLogoLayout.getWidth();
			}else{
				mDestX = -mRootLayout.getWidth()/TRAY_HIDDEN_FRACTION;
			}
			
			// Keep upper edge of the widget within the upper limit of screen
			int screenHeight = getResources().getDisplayMetrics().heightPixels;
			mDestY = Math.max(screenHeight/TRAY_MOVEMENT_REGION_FRACTION, mRootLayoutParams.y);
			
			// Keep lower edge of the widget within the lower limit of screen
			mDestY = Math.min(((TRAY_MOVEMENT_REGION_FRACTION-1)*screenHeight)/TRAY_MOVEMENT_REGION_FRACTION - mRootLayout.getWidth(), mDestY);
		}
		
		// This function is called after every frame.
		@Override
		public void run() {
			// handler is used to run the function on main UI thread in order to
			// access the layouts and UI elements.
			mAnimationHandler.post(new Runnable() {
				@Override
				public void run() {
					// Update coordinates of the tray
					mRootLayoutParams.x = (2*(mRootLayoutParams.x-mDestX))/3 + mDestX;
					mRootLayoutParams.y = (2*(mRootLayoutParams.y-mDestY))/3 + mDestY;
					mWindowManager.updateViewLayout(mRootLayout, mRootLayoutParams);
			
					// Cancel animation when the destination is reached
					if (Math.abs(mRootLayoutParams.x-mDestX)<2 && Math.abs(mRootLayoutParams.y-mDestY)<2){
						TrayAnimationTimerTask.this.cancel();
						mTrayAnimationTimer.cancel();
					}
				}
			});
		}
	}

}
