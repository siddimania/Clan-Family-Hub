package clan.ring.phone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import clan.main.R;

public class PhoneRing extends Activity {
	Ringtone r1;
	int onoff = 0;
	int ringerMode = 3;
	AlarmManager am;
	Vibrator vibrator;
	PowerManager.WakeLock wl;
	AudioManager audioManager;

	MediaPlayer mPlayer;
	
	protected void onCreate(Bundle savedInstanceState) {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		audioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
		wl.acquire();

		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.clan_ring_phone_layout);

		try {

			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_ALARM);
			r1 = RingtoneManager.getRingtone(getApplicationContext(),
					notification);

			mPlayer= new MediaPlayer();
			mPlayer.setDataSource(this,notification);
			
			System.out.println("this is ringer mode "+ audioManager.getRingerMode());
			
			int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, 0);

			if (audioManager.getRingerMode() == 1) {
				ringerMode = 1;
			}
			
			mPlayer.setLooping(true);
			mPlayer.prepare();
			mPlayer.start();
			
			if (onoff == 1)
				vibrator.vibrate(10 * 60 * 1000);

				//r1.play();

		} catch (Exception e) {
			System.out.println("YOU GET AN ERROR");

		}

		onClickListenerDismiss();

	}

	private void onClickListenerDismiss() {

		ImageView img = (ImageView) findViewById(R.id.imageView2);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(10);
				
				mNotificationManager.cancel(9);

				//r1.stop();
				mPlayer.stop();
				vibrator.cancel();
				if (ringerMode == 1) {
					audioManager
							.setRingerMode(audioManager.RINGER_MODE_VIBRATE);
				}
				System.out.println("This is after ringermode "
						+ audioManager.getRingerMode());
				finish();
				wl.release();

			}
		});
	}

	@Override
	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called");
		
	}
	
	@Override
	public void onAttachedToWindow() {
		
	}

}
