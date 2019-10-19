package clan.nav_drawer.slidingmenu;

import clan.main.R;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class HomeFragment extends Fragment{
	
	public HomeFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.nav_drawer_fragment_home, container, false);
        
        ImageButton addAFriend = (ImageButton) rootView.findViewById(R.id.addAFriend);
        addAFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			
				Intent intent = new Intent(getActivity(), clan.main.SendingInvite.class);
				startActivity(intent);
			}
		});
        return rootView;
    }
}
