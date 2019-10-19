package clan.nav_drawer.slidingmenu;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import clan.main.R;

public class FriendTimeLine extends FragmentActivity implements ActionBar.TabListener{

	ActionBar actionBar;
	ViewPager viewPager ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_timeline_mainxml);
		
		//getIntentExtras();
		formingMytabsInFriendTime();
	}

	private void formingMytabsInFriendTime() {
		
		ArrayList<String> myTabs;
		myTabs = new ArrayList<String>();
		
		myTabs.add("TimeLine");
		myTabs.add("TODO");
		myTabs.add("Chat");
		
		actionBar= getActionBar();
		TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.myTabsPager);
		viewPager.setAdapter(tabsAdapter);
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for(String tabString : myTabs){
			actionBar.addTab(actionBar.newTab().setText(tabString).setTabListener(this));
		}
		
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
		
	}

	private void getIntentExtras() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if (extras != null) {
		    if (extras.getString("firstName") != null && extras.getString("lastName") != null) {
		    	String firstName = extras.getString("firstName");
		    	String lastName = extras.getString("lastName");
		    	TextView textViewFirstName = (TextView)findViewById(R.id.friend_time_line_name);
		    	textViewFirstName.setText(firstName+" "+lastName);
		    	
		    }
		    if (extras.getString("email") != null) {
		    	String email = extras.getString("email");
		    	TextView textViewEmail = (TextView)findViewById(R.id.friend_time_line_city);
		    	textViewEmail.setText(email);
		    }
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		 viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	private class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
            	case 0:{
            		Intent i = getIntent();
            		Double latitude = i.getDoubleExtra("latitude", 0.0d);
            		Double longitude = i.getDoubleExtra("longitude", 0.0d);
            		String lastseen = i.getStringExtra("lastseen");
            		String name = i.getStringExtra("firstName")+"  "+i.getStringExtra("lastName");
            		String femail =i.getStringExtra("email");
            		return ((Fragment)FriendTimeLine_TimeLineFragment.newInstance(name,latitude,longitude,lastseen,femail));
            	}
            	case 1:{
            		Intent i = getIntent();
            		String femail =i.getStringExtra("email");
            		return ((Fragment)FriendTimeLine_TODOFragment.newInstance(femail));
            	}
            	case 2:{
            		
            		Intent i = getIntent();
            		
            		final SharedPreferences prefs = PreferenceManager
    						.getDefaultSharedPreferences(getApplicationContext());
    				String email = prefs.getString("email", "");
            		String femail =i.getStringExtra("email");
            		return ((Fragment)FriendTimeLine_CHATFragment.newInstance(email,femail));
            	}
            	default:
                    return null;
            }
            //   return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 3;
        }
    }
	
}
