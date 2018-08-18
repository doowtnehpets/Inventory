package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    // Log tag
    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    // Database version and name
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    /**
     * Constructor
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is first created
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // SQL statement used to create the table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookEntry.COLUMN_BOOK_PRODUCT_NAME + " TEXT NOT NULL, " +
                BookEntry.COLUMN_BOOK_PRICE + " TEXT NOT NULL DEFAULT '0.00', " +
                BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT NOT NULL DEFAULT 'Unknown', " +
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL DEFAULT '00000000000');";

        // Create the database
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * Called when database is upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Currently no upgrade implementation
    }
}
