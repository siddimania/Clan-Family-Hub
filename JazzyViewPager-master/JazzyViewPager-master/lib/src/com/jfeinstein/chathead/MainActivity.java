package com.jfeinstein.chathead;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static int width = 300;
	public static int height = 500;
	public static Bitmap mybitmap=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//calculate height and width of the device screen
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;
		
		Toast.makeText(getApplicationContext(),"in chat head", Toast.LENGTH_SHORT).show();
		
		//final String URL = "http://d2z9qv80fklwtv.cloudfront.net/data/user_profile_pictures/ee8/b8b5041371d16cf2e6989b73254c2ee8_100_thumb.jpg";
		final String URL = "http://m.c.lnkd.licdn.com/mpr/pub/image-d62u-1_SRDbowCIEheYodL43P9JEDEPOmV21CxOSPMefarg4d621TFISPj-fYRoX_kpn/siddhartha-dimania.jpg";
		
		GetBitmap task = new GetBitmap();
		task.execute(URL);

		finish();
	}
	
    private void functionToStartServices() {
    	
	    startService(new Intent(this, closeService.class));
	    startService(new Intent(this, PlayerService.class));
	
    }
	
	private class GetBitmap extends AsyncTask<String, Void, Bitmap> {
	       @Override
	       protected Bitmap doInBackground(String... urls) {
	           Bitmap map = null;
	           map = downloadImage(urls[0]);
       	       return map;
	       }

	       @Override
	       protected void onPostExecute(Bitmap result) {
	    	   ImageView chatHead = new ImageView(getApplicationContext());
	    	   chatHead.setImageBitmap(result);
	    	   mybitmap = result;
	    	   functionToStartServices();
	       }

	       private Bitmap downloadImage(String url) {
	           Bitmap bitmap = null;
	           InputStream stream = null;
	           BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	           bmOptions.inSampleSize = 1;
	           try {
	               stream = getHttpConnection(url);
	               bitmap = BitmapFactory.decodeStream(new PatchInputStream(stream), null, bmOptions);
	               stream.close();
	           } catch (IOException e1) {
	               e1.printStackTrace();
	           }
	           return bitmap;
	       }
	       
	       private InputStream getHttpConnection(String urlString)throws IOException {
	           InputStream stream = null;
	           URL url = new URL(urlString);
	           URLConnection connection = url.openConnection();

	           try {
	               HttpURLConnection httpConnection = (HttpURLConnection) connection;
	               httpConnection.setRequestMethod("GET");
	               httpConnection.connect();

	               if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                   stream = httpConnection.getInputStream();
	               }
	           } catch (Exception ex) {
	               ex.printStackTrace();
	           }
	           return stream;
	       }
	       
	       public class PatchInputStream extends FilterInputStream {
	    	   public PatchInputStream(InputStream in) {
	    	     super(in);
	    	   }
	    	   public long skip(long n) throws IOException {
	    	     long m = 0L;
	    	     while (m < n) {
	    	       long _m = in.skip(n-m);
	    	       if (_m == 0L) break;
	    	       m += _m;
	    	     }
	    	     return m;
	    	   }
	       }
	       
	   }
}