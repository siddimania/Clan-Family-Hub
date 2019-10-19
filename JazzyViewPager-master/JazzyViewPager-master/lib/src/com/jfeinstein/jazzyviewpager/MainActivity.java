package com.jfeinstein.jazzyviewpager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

public class MainActivity extends Activity {

	private JazzyViewPager mJazzy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupJazziness(TransitionEffect.ZoomIn);
		OnSigupClick();
	}

	private void OnSigupClick() {
	
		Button signup = (Button) findViewById(R.id.button1);
		signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "SOS", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(MainActivity.this, SOSMain.class);
				startActivity(i);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Toggle Fade");
		String[] effects = this.getResources().getStringArray(R.array.jazzy_effects);
		for (String effect : effects)
			menu.add(effect);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equals("Toggle Fade")) {
			mJazzy.setFadeEnabled(!mJazzy.getFadeEnabled());
		} else {
			//TransitionEffect effect = TransitionEffect.valueOf(item.getTitle().toString());
			//setupJazziness(effect);
		}
		return true;
	}

	private void setupJazziness(TransitionEffect effect) {
		
		List<Integer> list = new ArrayList<Integer>();
		list.add(R.drawable.my_desk);
		list.add(R.drawable.my_friends);
		list.add(R.drawable.connect_all);
		list.add(R.drawable.mobile_layers);
		
		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(effect);
		mJazzy.setAdapter(new MainAdapter(list));
		mJazzy.setPageMargin(30);
	}

	private class MainAdapter extends PagerAdapter {
		
		List<Integer> list ;
		public MainAdapter(List<Integer> list) {
			this.list = list;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			
			ImageView imageView = new ImageView(MainActivity.this);
			imageView.setImageResource(list.get(position));
			//imageView.setPadding(30, 30, 30, 30);
			
			
			TextView text = new TextView(MainActivity.this);
			text.setGravity(Gravity.CENTER);
			text.setTextSize(30);
			text.setTextColor(Color.WHITE);
			text.setText("Page " + position);
			text.setPadding(30, 30, 30, 30);
			int bg = Color.rgb((int) Math.floor(Math.random()*128)+64, 
					(int) Math.floor(Math.random()*128)+64,
					(int) Math.floor(Math.random()*128)+64);
			text.setBackgroundColor(bg);
			container.addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mJazzy.setObjectForPosition(imageView, position);
			
			
			
			return imageView;
		}
		
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			container.removeView(mJazzy.findViewFromObject(position));
		}
		@Override
		public int getCount() {
			return list.size();
		}
		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof OutlineContainer) {
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}		
	}
	
	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "starting chat head",
				Toast.LENGTH_SHORT).show();
		Intent i = new Intent(MainActivity.this, com.jfeinstein.chathead.MainActivity.class);
		startActivity(i);
		finish();
	}
	
}
