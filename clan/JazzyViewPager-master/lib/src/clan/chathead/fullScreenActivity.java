package clan.chathead;

//import sid.dimania.example.MainActivity.GetBitmap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import clan.main.R;

public class fullScreenActivity extends Activity{
	
	SOS_Service obj = new SOS_Service();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  setContentView(R.layout.chathead_fullscreenlayout);
		  
		
		  RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.mainlayout);
		  rlayout.setOnClickListener(new OnClickListener() {
		     @Override
		    public void onClick(View v) {
		    	 
		    	 closeAllService();
		    	 
		    }

			private void closeAllService() {
				
				stopService(new Intent(fullScreenActivity.this,SampleOverlayService.class));
				obj.updateCheckClicks();
				finishActivity();
			
			}
			
		  });
	}
	
	private void finishActivity() {
		finish();
	}
	
	@Override
	public void onBackPressed() {
		
		stopService(new Intent(fullScreenActivity.this,SampleOverlayService.class));
		obj.updateCheckClicks();
		finishActivity();
	
	}
}
