package clan.chathead;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static int width = 400;
	public static int height = 500;
	public static Bitmap mybitmap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// calculate height and width of the device screen
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;

		Toast.makeText(getApplicationContext(), "in chat head",
				Toast.LENGTH_SHORT).show();

		setBitmap();
		finish();
	}

	private void startingServices() {
		startService(new Intent(this, closeService.class));
		startService(new Intent(this, SOS_Service.class));
	}

	protected void gettingBitmap(Bitmap result) {
		ImageView chatHead = new ImageView(getApplicationContext());
		chatHead.setImageBitmap(result);
		mybitmap = result;
		startingServices();
	}

	private void setBitmap() {
		Bitmap bitmap = null;
		InputStream stream = null;
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;
		try {
			stream = getAssets().open("sos_red_icon.png");
			bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
			stream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		gettingBitmap(bitmap);
	}

}