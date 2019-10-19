package clan.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_home_activity);

		((Button) findViewById(R.id.main_home_sos)).setOnClickListener(this);
		((Button) findViewById(R.id.main_home_map)).setOnClickListener(this);
		((Button) findViewById(R.id.main_home_chatTest))
				.setOnClickListener(this);
		((Button) findViewById(R.id.main_home_chatHead))
				.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent i ;
		switch (arg0.getId()) {
		case R.id.main_home_sos:
			i = new Intent(HomeActivity.this,clan.sos.SOSMain.class);
			startActivity(i);
			break;
		case R.id.main_home_map:
			i = new Intent(HomeActivity.this,clan.main.Location.class);
			startActivity(i);
			break;
		case R.id.main_home_chatTest:
			i = new Intent(HomeActivity.this,clan.chatinterface.HelloBubblesActivity.class);
			startActivity(i);
			break;
		case R.id.main_home_chatHead:
			i = new Intent(HomeActivity.this,clan.chathead.MainActivity.class);
			startActivity(i);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
