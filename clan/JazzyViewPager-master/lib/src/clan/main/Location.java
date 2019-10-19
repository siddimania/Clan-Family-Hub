package clan.main;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class Location extends Activity {

	// Google Map
	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_home);

		try {
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			double longi = 28.450317;
			double latitu = 77.075284;
			googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(longi, latitu)).title("My location")
					.snippet("Location : " + longi + "," + latitu)
					.draggable(true));

			Polygon polygon = googleMap.addPolygon(new PolygonOptions()
					.add(new LatLng(0, 0), new LatLng(0, 5), new LatLng(3, 5),
							new LatLng(0, 0)).strokeColor(Color.RED)
					.fillColor(Color.BLUE));

			CircleOptions circleOptions = new CircleOptions()
					.center(new LatLng(longi, latitu)).radius(10000)
					.strokeColor(Color.BLUE); // In meters

			Circle circle = googleMap.addCircle(circleOptions);

			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
				// placePoint();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
		googleMap.setMyLocationEnabled(true);
	}

}