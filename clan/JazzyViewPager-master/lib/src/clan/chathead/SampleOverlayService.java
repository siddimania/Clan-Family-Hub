package clan.chathead;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import clan.main.*;

public class SampleOverlayService extends OverlayService {

	public static SampleOverlayService instance;
	private SampleOverlayView overlayView;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		overlayView = new SampleOverlayView(this);	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (overlayView != null) {
			overlayView.destory();
		}
	}
	
	static public void stop() {
		
		if (instance != null) 
			instance.stopSelf();
	}
	
	@Override
	protected Notification foregroundNotification(int notificationId) {
		return null;
	}


	private PendingIntent notificationIntent() {
		
		Intent intent = new Intent(this, SampleOverlayHideActivity.class);
		PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pending;
	}
}
