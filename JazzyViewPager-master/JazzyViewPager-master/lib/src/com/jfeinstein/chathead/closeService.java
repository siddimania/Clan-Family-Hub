package com.jfeinstein.chathead;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader.TileMode;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.jfeinstein.jazzyviewpager.*;

public class closeService extends Service {

	public WindowManager windowManager;
	static public ImageView chatHead=null;
	final int MAX_CLICK_DURATION = 150;
	long startClickTime = 0;

    MainActivity getParams = new MainActivity();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		chatHead = new ImageView(this);
		chatHead.setImageResource(R.drawable.cross_white);

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				Utils.dpToPixels(65, getResources()),
				Utils.dpToPixels(65, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSPARENT);

		params.gravity = Gravity.BOTTOM | Gravity.CENTER;
		Log.d("coordinates me ", getParams.width+" "+ getParams.height);
		params.x = 0;
		params.y = 10;
		params.windowAnimations = android.R.style.Animation_InputMethod;
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.cross_white);
		Bitmap th = getCircularBitmapWithWhiteBorder(bmp, 0);
		chatHead.setImageBitmap(th);

	    chatHead.setVisibility(View.GONE);
		windowManager.addView(chatHead, params);
		
	}
	
	public void show(){
        chatHead.setVisibility(View.VISIBLE);
    }
    public void hide() {
        chatHead.setVisibility(View.GONE);
    }
    
	public static Bitmap getCircularBitmapWithWhiteBorder(Bitmap bitmap,int borderWidth) {
		if (bitmap == null || bitmap.isRecycled()) {
			return null;
		}
		final int width = bitmap.getWidth() + borderWidth;
		final int height = bitmap.getHeight() + borderWidth;

		Bitmap canvasBitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(shader);
		
		Canvas canvas = new Canvas(canvasBitmap);
		float radius = width > height ? ((float) height) / 2f: ((float) width) / 2f;
		canvas.drawCircle(width / 2, height / 2, radius, paint);
		paint.setShader(null);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.TRANSPARENT);
		canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2,paint);
		return canvasBitmap;
	}

	public void destoryMe() {
		stopService(new Intent(closeService.this, closeService.class));
	}

	@Override
	public void onDestroy() {
		stopSelf();
		super.onDestroy();
		if (chatHead != null)
			windowManager.removeView(chatHead);
	}
}