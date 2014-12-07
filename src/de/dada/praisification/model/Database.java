package de.dada.praisification.model;

import android.content.Context;
import android.database.sqlite.*;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	public static final String TABLE_PROTOCOLLS = "protocolls";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DRINKS = "drinks";
    public static final String COLUMN_FOOD = "food";
    public static final String COLUMN_EXTRAS = "extras";
    public static final String COLUMN_ARRIVAL = "arrival";
    public static final String COLUMN_DEPARTURE = "departure";
    public static final String COLUMN_PICTURE = "picture";
    public static final String COLUMN_RATING = "rating";

    private static final String DATABASE_NAME = "protocolls.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
        + TABLE_PROTOCOLLS + "(" + COLUMN_ID
        + " integer primary key autoincrement, " + COLUMN_NAME
        + " text not null," + COLUMN_DRINKS + " text not null," + COLUMN_FOOD
        + " text not null," + COLUMN_EXTRAS + " text not null," + COLUMN_ARRIVAL
        + " text not null," + COLUMN_DEPARTURE + " text not null," + COLUMN_PICTURE
        + " text not null," + COLUMN_RATING + " real not null" + ");";
  
	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Database.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROTOCOLLS);
		    onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Database.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROTOCOLLS);
		    onCreate(db);
	}

}
