package clan.customeview.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;




public class DbHelper extends SQLiteOpenHelper {

	private static final String TAG = DbHelper.class.getSimpleName();
	public static final String DB_NAME = "friendDatabase.db";
	public static final int DB_VERSION = 1;

	/*
	 * friend Details Table : 
	 */
	public static final String TABLE1 = "friend_Table"; 
	public static final String _ID = "_id";
	public static final String firstName= "firstName";
	public static final String lastName= "lastName";
	public static final String email = "email";
	public static final String status = "status";
	
	public static final String latitude = "latitude";
	public static final String longitude = "longitude";
	public static final String place = "place";
	public static final String lastseen = "lastseen";
	
	/*
	 * to Do Item Table : 
	 */
	public static final String TABLE2 = "toDoTable"; 
	public static final String toDoItem = "toDoItem";
	public static final String itemDate = "itemDate";
	public static final String itemTime = "itemTime";
	
	public static final String friendTABLETODO = "friendTABLETODO"; 
	public static final String friendEmail = "friendEmail";
	
	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String friendsDetail = "CREATE TABLE " + TABLE1 + "(" + _ID+ " INTEGER PRIMARY KEY, " +firstName+" TEXT, "+ lastName + " TEXT, "+ email + " TEXT, " + status + " TEXT, "+latitude+" TEXT,"+longitude+" TEXT,"+place+" TEXT,"+lastseen+" TEXT"+");";
		String toDoItems = "CREATE TABLE " + TABLE2 + "(" + _ID+ " INTEGER PRIMARY KEY, " + friendEmail +" TEXT, "+ toDoItem +" TEXT, "+ itemDate+" TEXT, "+ itemTime + " TEXT);";
		String friendtoDoItems = "CREATE TABLE " + friendTABLETODO + "(" + _ID+ " INTEGER PRIMARY KEY, " + friendEmail +" TEXT, "+ toDoItem +" TEXT, "+ itemDate+" TEXT, "+ itemTime + " TEXT);";
		
		Log.d(TAG, "CREATING SQL friendDetail " + friendsDetail);
		db.execSQL(friendsDetail);
		Log.d(TAG, "CREATING toDoItems " + toDoItems);
		db.execSQL(toDoItems);
		Log.d(TAG, "CREATING friendtoDoItems " + friendtoDoItems);
		db.execSQL(friendtoDoItems);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		//i added this ...when the version will upgrade........................................
		oldVersion= newVersion;
		db.execSQL("PRAGMA user_version =" + newVersion);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE1);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE2);
		db.execSQL("DROP TABLE IF EXISTS " + friendTABLETODO);
        onCreate(db);
	}

}
