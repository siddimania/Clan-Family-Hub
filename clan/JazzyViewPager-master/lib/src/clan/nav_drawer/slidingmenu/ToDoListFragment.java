package clan.nav_drawer.slidingmenu;

import clan.customeview.database.DbHelper;
import clan.main.R;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ToDoListFragment extends Fragment {
	DbHelper dbHelper;
	SQLiteDatabase db;
	View rootView ;
	String femail;
	public ToDoListFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.to_do_list_fragment, container, false);
        Button addAItem = (Button) rootView.findViewById(R.id.addAItem);
        addAItem.setVisibility(View.GONE);
        Bundle args = getArguments();        
        gettingToDoListView();       
        
        return rootView;
    }

	SimpleCursorAdapter adapter;
	Cursor cursor = null;
	private int gettingToDoListView() {
		dbHelper = new DbHelper(getActivity().getApplicationContext());
		db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {

			final ListView tv = (ListView) rootView.findViewById(R.id.toDoList);
			cursor = db.query(DbHelper.TABLE2, new String[] {DbHelper._ID,DbHelper.friendEmail, DbHelper.toDoItem,DbHelper.itemDate,DbHelper.itemTime},
					null, null,null, null, DbHelper._ID+" DESC");

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



}
