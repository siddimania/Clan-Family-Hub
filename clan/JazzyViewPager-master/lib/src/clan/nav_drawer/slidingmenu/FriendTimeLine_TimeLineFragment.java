package clan.nav_drawer.slidingmenu;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import clan.main.R;

public class FriendTimeLine_TimeLineFragment extends Fragment {
	String femail;
	String name;
	Context context;
	double latitude, longitude;
	String lastseen;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.friend_time_line, container,
				false);
		Bundle args = getArguments();
		latitude = args.getDouble("latitude");
		longitude = args.getDouble("longitude");
		lastseen = args.getString("lastseen");
		name = args.getString("name");
		femail = args.getString("femail");
		
		context = getActivity().getApplicationContext();
		
		TextView tvName = (TextView) rootView.findViewById(R.id.friend_time_line_name);
		TextView tvEmail = (TextView) rootView.findViewById(R.id.friend_time_line_email);
		//TextView tvName = (TextView) rootView.findViewById(R.id.friend_time_line_city);
		tvName.setText(name);
		tvEmail.setText(femail);

		Button trackLocation = (Button) rootView.findViewById(R.id.trackLocation);
		trackLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(context.CONNECTIVITY_SERVICE);
				
				boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
				boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

				if (is3g || isWifi){
					Intent i = new Intent(getActivity().getApplicationContext(),
							ViewLocation.class);
					i.putExtra("latitude", latitude);
					i.putExtra("longitude", longitude);
					i.putExtra("name", name);
					i.putExtra("femail", femail);
					startActivity(i);
				}
				else{
					Toast.makeText(context, "Check Your Connection", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return rootView;
	}

	public static FriendTimeLine_TimeLineFragment newInstance(String name,
			double lat, double longitude, String lastseen, String femail) {
		final FriendTimeLine_TimeLineFragment quoteFragment = new FriendTimeLine_TimeLineFragment();
		final Bundle args = new Bundle();
		args.putString("name", name);
		args.putDouble("latitude", lat);
		args.putDouble("longitude", longitude);
		args.putString("lastseen", lastseen);
		args.putString("femail", femail);
		quoteFragment.setArguments(args);
		return quoteFragment;
	}
}
