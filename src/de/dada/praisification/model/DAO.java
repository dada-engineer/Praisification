package de.dada.praisification.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DAO {

  // Database fields
  private SQLiteDatabase db;
  private database dbHelper;
  private String[] allColumns = { database.COLUMN_ID,
		  database.COLUMN_NAME, database.COLUMN_DRINKS, database.COLUMN_FOOD,
		  database.COLUMN_EXTRAS, database.COLUMN_ARRIVAL, database.COLUMN_DEPARTURE,
		  database.COLUMN_PICTURE};

  public DAO(Context context) {
    dbHelper = new database(context);
  }

  public void open() throws SQLException {
    db = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public void createProtocol(ProtocolContent protocol) {
    ContentValues values = new ContentValues();
    values.put(database.COLUMN_NAME, protocol.getName());
    values.put(database.COLUMN_DRINKS, protocol.getDrinks());
    values.put(database.COLUMN_FOOD, protocol.getFood());
    values.put(database.COLUMN_EXTRAS, protocol.getExtras());
    values.put(database.COLUMN_ARRIVAL, protocol.getArrivalTime());
    values.put(database.COLUMN_DEPARTURE, protocol.getDepatureTime());
    values.put(database.COLUMN_PICTURE, protocol.getPicturePath());
    
    long insertId = db.insert(database.TABLE_PROTOCOLLS, null,
        values);
    Cursor cursor = db.query(database.TABLE_PROTOCOLLS,
        allColumns, database.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.close();
  }

  public void deleteProtocol(ProtocolContent protocol) {
    String name = protocol.getName();
    System.out.println("Protocol deleted with id: " + name);
    db.delete(database.TABLE_PROTOCOLLS, database.COLUMN_ID
        + " = " + name, null);
  }

  public List<ProtocolContent> getAllProtocolls() {
    List<ProtocolContent> protocols = new ArrayList<ProtocolContent>();

    Cursor cursor = db.query(database.TABLE_PROTOCOLLS,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      ProtocolContent protocol = cursorToProtocol(cursor);
      protocols.add(protocol);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return protocols;
  }
  
  public ProtocolContent getProtocolByName(String name) {
	    ProtocolContent protocol = null;

	    Cursor cursor = db.rawQuery("select * from " + database.TABLE_PROTOCOLLS +
	    		" where " + database.COLUMN_NAME + "=" + name  , null);

	    if (cursor != null)
        {
         if (cursor.moveToFirst())
            {
		         long id = cursor.getInt(cursor.getColumnIndex(database.COLUMN_ID));
		         String drinks = cursor.getString(cursor.getColumnIndex(database.COLUMN_DRINKS));
		         String food = cursor.getString(cursor.getColumnIndex(database.COLUMN_FOOD));
		         String extras = cursor.getString(cursor.getColumnIndex(database.COLUMN_EXTRAS));
		         String arrival = cursor.getString(cursor.getColumnIndex(database.COLUMN_ARRIVAL));
		         String departure = cursor.getString(cursor.getColumnIndex(database.COLUMN_DEPARTURE));
		         String picture = cursor.getString(cursor.getColumnIndex(database.COLUMN_PICTURE));
		         
		         protocol = new ProtocolContent(name);
		         protocol.setId(id);
		         protocol.setDrinks(drinks);
		         protocol.setFood(food);
		         protocol.setExtras(extras);
		         protocol.setArrivalTime(arrival);
		         protocol.setDepatureTime(departure);
		         protocol.setPicturePath(picture);
		    }
		         cursor.close();
		 }
	    return protocol;
	  }
  
  public void updateProtocol(ProtocolContent protocol) {
	  ContentValues values = new ContentValues();
	  values.put(database.COLUMN_NAME, protocol.getName());
	  values.put(database.COLUMN_DRINKS, protocol.getDrinks());
	  values.put(database.COLUMN_FOOD, protocol.getFood());
	  values.put(database.COLUMN_EXTRAS, protocol.getExtras());
	  values.put(database.COLUMN_ARRIVAL, protocol.getArrivalTime());
	  values.put(database.COLUMN_DEPARTURE, protocol.getDepatureTime());
	  values.put(database.COLUMN_PICTURE, protocol.getPicturePath());

	  db.update(database.TABLE_PROTOCOLLS, values, database.COLUMN_NAME + 
			  " = " + protocol.getName(), null);
}

  private ProtocolContent cursorToProtocol(Cursor cursor) {
	  ProtocolContent protocol = new ProtocolContent("name");
    protocol.setId(cursor.getLong(0));
    protocol.setName(cursor.getString(1));
    return protocol;
  }
} 
