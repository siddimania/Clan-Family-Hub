package clan.chat;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import clan.main.R;

import com.codebutler.android_websockets.WebSocketClient;

public class MainActivity extends Activity {

	// LogCat tag
	private static final String TAG = MainActivity.class.getSimpleName();

	private Button btnSend;
	private EditText inputMsg;

	private WebSocketClient client;

	// Chat messages list adapter
	private MessagesListAdapter adapter;
	private List<Message> listMessages;
	private ListView listViewMessages;

	private Utils utils;

	// Client name
	private String name = null,fname=null;

	// JSON flags to identify the kind of JSON response
	private static final String TAG_SELF = "self", TAG_NEW = "new",
			TAG_MESSAGE = "message", TAG_EXIT = "exit";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity_main);

		btnSend = (Button) findViewById(R.id.btnSend);
		inputMsg = (EditText) findViewById(R.id.inputMsg);
		listViewMessages = (ListView) findViewById(R.id.list_view_messages);

		utils = new Utils(getApplicationContext());

		// Getting the person name from previous screen
		Intent i = getIntent();
		name = i.getStringExtra("name");
		fname = i.getStringExtra("fname");

		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Sending message to web socket server
				sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText()
						.toString()));
				Log.d("this", "" + name + fname);
				// Clearing the input filed once message was sent
				inputMsg.setText("");
			}
		});

		listMessages = new ArrayList<Message>();

		adapter = new MessagesListAdapter(this, listMessages);
		listViewMessages.setAdapter(adapter);

		
		
		client = new WebSocketClient(URI.create(WsConfig.URL_WEBSOCKET
				+ URLEncoder.encode(name)+"&receiver="+URLEncoder.encode(fname)), new WebSocketClient.Listener() {
			@Override
			public void onConnect() {

			}

			
			@Override
			public void onMessage(String message) {
				Log.d(TAG, String.format("Got string message! %s", message));

				parseMessage(message);

			}

			@Override
			public void onMessage(byte[] data) {
				Log.d(TAG, String.format("Got binary message! %s",
						bytesToHex(data)));

				// Message will be in JSON format
				parseMessage(bytesToHex(data));
			}

		
			@Override
			public void onDisconnect(int code, String reason) {

				String message = String.format(Locale.US,
						"Disconnected! Code: %d Reason: %s", code, reason);

				showToast(message);

				// clear the session id from shared preferences
				utils.storeSessionId(null);
			}

			@Override
			public void onError(Exception error) {
				Log.e(TAG, "Error! : " + error);

				showToast("Error! : " + error);
			}

		}, null);

		client.connect();
	}

	private void sendMessageToServer(String message) {
		if (client != null && client.isConnected()) {
			client.send(message);
		}
	}

	private void parseMessage(final String msg) {

		try {
			JSONObject jObj = new JSONObject(msg);

			// JSON node 'flag'
			String flag = jObj.getString("flag");

			// if flag is 'self', this JSON contains session id
			if (flag.equalsIgnoreCase(TAG_SELF)) {

				String sessionId = jObj.getString("sessionId");

				// Save the session id in shared preferences
				utils.storeSessionId(sessionId);

				Log.e(TAG, "Your session id: " + utils.getSessionId());

			} else if (flag.equalsIgnoreCase(TAG_NEW)) {
				// If the flag is 'new', new person joined the room
				String name = jObj.getString("name");
				String message = jObj.getString("message");

				// number of people online
				String onlineCount = jObj.getString("onlineCount");

				showToast(name + message + ". Currently " + onlineCount
						+ " people online!");

			} else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
				// if the flag is 'message', new message received
				String fromName = name;
				String message = jObj.getString("message");
				String sessionId = jObj.getString("sessionId");
				boolean isSelf = true;

				// Checking if the message was sent by you
				if (!sessionId.equals(utils.getSessionId())) {
					fromName = jObj.getString("name");
					isSelf = false;
				}

				Message m = new Message(fromName, message, isSelf);

				// Appending the message to chat list
				appendMessage(m);

			} else if (flag.equalsIgnoreCase(TAG_EXIT)) {
				// If the flag is 'exit', somebody left the conversation
				String name = jObj.getString("name");
				String message = jObj.getString("message");

				showToast(name + message);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(client != null & client.isConnected()){
			client.disconnect();
		}
	}

	private void appendMessage(final Message m) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				listMessages.add(m);
				adapter.notifyDataSetChanged();

				// Playing device's notification
				playBeep();
			}
		});
	}

	private void showToast(final String message) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_LONG).show();
			}
		});

	}

	public void playBeep() {

		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
			r.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
