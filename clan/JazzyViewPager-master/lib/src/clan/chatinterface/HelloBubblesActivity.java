package clan.chatinterface;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ListView;
import clan.main.R;
import de.svenjacobs.loremipsum.LoremIpsum;

public class HelloBubblesActivity extends Activity {
	private clan.chatinterface.DiscussArrayAdapter adapter;
	private ListView lv;
	private LoremIpsum ipsum;
	private EditText editText1;
	private static Random random;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity_discuss);
		random = new Random();
		ipsum = new LoremIpsum();

		lv = (ListView) findViewById(R.id.listView1);

		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.chat_listitem_discuss);

		lv.setAdapter(adapter);

		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					adapter.add(new OneComment(getRandomBoolean(), editText1.getText().toString()));
					editText1.setText("");
					return true;
				}
				return false;
			}
		});

		addItems();
	}
	
	public static boolean getRandomBoolean() {
	       return Math.random() < 0.5;
	       //I tried another approaches here, still the same result
	   }

	private void addItems() {
		adapter.add(new OneComment(true, "Hello bubbles!"));

		for (int i = 0; i < 4; i++) {
			boolean left = getRandomInteger(0, 1) == 0 ? true : false;
			int word = getRandomInteger(1, 10);
			int start = getRandomInteger(1, 40);
			String words = ipsum.getWords(word, start);

			adapter.add(new OneComment(left, words));
		}
	}

	private static int getRandomInteger(int aStart, int aEnd) {
		if (aStart > aEnd) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}
		long range = (long) aEnd - (long) aStart + 1;
		long fraction = (long) (range * random.nextDouble());
		int randomNumber = (int) (fraction + aStart);
		return randomNumber;
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