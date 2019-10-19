package clan.nav_drawer.slidingmenu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import clan.customeview.database.DbHelper;
import clan.main.R;

public class FriendTimeLine_TODOFragment extends Fragment {

	DbHelper dbHelper;
	SQLiteDatabase db;
	View rootView ;
	String femail;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.to_do_list_fragment, container, false);
        Bundle args = getArguments();
		femail = args.getString("femail");      
        
        gettingToDoListView();
        addAToDoItem();
        return rootView;
    }
	
	private void addAToDoItem() {
		Button addAItem = (Button) rootView.findViewById(R.id.addAItem);
		addAItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), clan.nav_drawer.slidingmenu.AddItemInToDoList.class);
				intent.putExtra("femail",femail);
				//Log.d("TODOFragment",femail);
				startActivityForResult(intent, 1);
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 1) {
	        if(resultCode == getActivity().RESULT_OK){
	        	gettingToDoListView();
	        	//cursor.requery();
	        	//adapter.notifyDataSetChanged();
	        	Log.d("Resutl","TODO Fragment");
	            //String result=data.getStringExtra("result");
	        } 
	        if (resultCode == getActivity().RESULT_CANCELED) {
	            //Write your code if there's no result 
	        } 
	    } 
	}
	SimpleCursorAdapter adapter;
	Cursor cursor = null;
	private int gettingToDoListView() {
		dbHelper = new DbHelper(getActivity().getApplicationContext());
		db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {

			final ListView tv = (ListView) rootView.findViewById(R.id.toDoList);
			cursor = db.query(DbHelper.friendTABLETODO, new String[] {DbHelper._ID,DbHelper.friendEmail, DbHelper.toDoItem,DbHelper.itemDate,DbHelper.itemTime},
					DbHelper.friendEmail+" = '"+femail+"'", null,null, null, DbHelper._ID+" DESC");

			getActivity().startManagingCursor(cursor);
					
			
			String[] from = { dbHelper.toDoItem};
			int[] to = { R.id.toDoItem};
			
			adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),R.layout.to_do_list_fragment_custom_listview, cursor, from, to, 0);
			adapter.notifyDataSetChanged();
			tv.setAdapter(adapter);
			
			
		//	Toast.makeText(getActivity(),"Getting To Do List View ", Toast.LENGTH_SHORT).show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor != null)
			return cursor.getCount();
		else
			return 0;
	}
	
	public static FriendTimeLine_TODOFragment newInstance(String femail) {
        final  FriendTimeLine_TODOFragment quoteFragment = new FriendTimeLine_TODOFragment();
        final Bundle args = new Bundle();
        args.putString("femail", femail);
        quoteFragment.setArguments(args);
        return quoteFragment;
    }
}
