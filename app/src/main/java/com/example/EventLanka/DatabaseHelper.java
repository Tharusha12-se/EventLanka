package com.example.EventLanka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EventLanka.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CART = "cart";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "event_name";
    private static final String COLUMN_DATE = "event_date";
    private static final String COLUMN_TIME = "event_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CART + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // Insert Data
    public boolean addToCart(String eventName, String eventDate, String eventTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, eventName);
        values.put(COLUMN_DATE, eventDate);
        values.put(COLUMN_TIME, eventTime);

        Log.i("cartName", eventName);

        long result = db.insert(TABLE_CART, null, values);
        db.close();
        return result != -1;  // Returns true if insertion is successful
    }

    // Fetch Data
    public Cursor getCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CART, null);
    }

    // Clear All Cart Items
    public void clearCartItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CART); // Deletes all rows
        db.close();
    }
}
