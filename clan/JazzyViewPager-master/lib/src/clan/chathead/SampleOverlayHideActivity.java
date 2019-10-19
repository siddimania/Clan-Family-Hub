package clan.chathead;

import android.app.Activity;

import android.os.Bundle;

public class SampleOverlayHideActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		SampleOverlayService.stop();
			
		finish();
		
	}
    
}
