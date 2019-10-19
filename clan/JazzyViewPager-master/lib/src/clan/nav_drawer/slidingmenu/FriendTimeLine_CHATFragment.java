package clan.nav_drawer.slidingmenu;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.codebutler.android_websockets.WebSocketClient;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import clan.chat.MainActivity;
import clan.chat.Message;
import clan.chat.MessagesListAdapter;
import clan.chat.Utils;
import clan.chat.WsConfig;
import clan.customeview.database.DbHelper;
import clan.main.R;

public class FriendTimeLine_CHATFragment extends Fragment {
	private static final String TAG = FriendTimeLine_CHATFragment.class.getSimpleName();

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
	DbHelper dbHelper;
	SQLiteDatabase db;
	View rootView ;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.chat_activity_main, container, false);
        
        
        btnSend = (Button) rootView.findViewById(R.id.btnSend);
		inputMsg = (EditText) rootView.findViewById(R.id.inputMsg);
		listViewMessages = (ListView) rootView.findViewById(R.id.list_view_messages);

		utils = new Utils(getActivity().getApplicationContext());

		// Getting the person name from previous screen
		Bundle args = getArguments();
		name = args.getString("email");
		fname = args.getString("femail");
		
		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Sending message to web socket server
				sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText()
						.toString()));
				Log.d("this2", "" + name + fname);
				// Clearing the input filed once message was sent
				inputMsg.setText("");
			}
		});
		
		listMessages = new ArrayList<Message>();

		adapter = new MessagesListAdapter(getActivity(), listMessages);
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

			//	showToast(message);

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
        
        return rootView;
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

				showToast(name + message + ".");

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
    public void onDetach() {
        super.onDetach();
        if(client != null & client.isConnected()){
			client.disconnect();
		}
    }
	
	private void appendMessage(final Message m) {
		getActivity().runOnUiThread(new Runnable() {

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
			//	Toast.makeText(getActivity().getApplicationContext(), message,Toast.LENGTH_LONG).show();
	}

	public void playBeep() {

		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(),
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
	
	public static FriendTimeLine_CHATFragment newInstance(String email,String femail) {
        final  FriendTimeLine_CHATFragment quoteFragment = new FriendTimeLine_CHATFragment();
        final Bundle args = new Bundle();
        args.putString("email",email);
        args.putString("femail", femail);
        quoteFragment.setArguments(args);
        return quoteFragment;
    }
}

