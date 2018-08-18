package com.example.android.inventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.inventory.data.BookContract.BookEntry;
import com.example.android.inventory.data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    // Log tag
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Database helper to access the database
    private BookDbHelper bookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookDbHelper = new BookDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseEntries();
    }

    /**
     * Display all the info from the database on the screen to verify SQL is working properly
     */
    private void displayDatabaseEntries() {
        // Grab a readable copy of the database
        SQLiteDatabase sqLiteDatabase = bookDbHelper.getReadableDatabase();

        // Projection specifying the columns we want from the database
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER};

        // Run a query on the database for the projection we defined and store it in a Cursor object
        Cursor cursor = sqLiteDatabase.query(BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        // Grab the TextView to display the database info
        TextView textView = findViewById(R.id.text_view_inventory);

        try {
            // Show the column names
            textView.setText(BookEntry._ID + " - " +
                    BookEntry.COLUMN_BOOK_PRODUCT_NAME + " - " +
                    BookEntry.COLUMN_BOOK_PRICE + " - " +
                    BookEntry.COLUMN_BOOK_QUANTITY + " - " +
                    BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER + "\n");

            // Grab the index for each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);

            // Run through the rows and append that to the TextView
            while (cursor.moveToNext()) {
                // Grab the values for each column on the specified row
                int currentId = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                // Add the values to the TextView so we can see the data in the database
                textView.append("\n" +
                        currentId + " - " +
                        currentProductName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhone);
            }

        } finally {
            // Make sure to close the cursor when finished
            cursor.close();
        }
    }

    /**
     * Add a pet to the database using made up data
     */
    private void insertBook() {
        // Get a writable copy of the database
        SQLiteDatabase sqLiteDatabase = bookDbHelper.getWritableDatabase();

        // Create a ContentValues object to insert a row into the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, getString(R.string.dummy_data_product_name));
        contentValues.put(BookEntry.COLUMN_BOOK_PRICE, getString(R.string.dummy_data_price));
        contentValues.put(BookEntry.COLUMN_BOOK_QUANTITY, 0);
        contentValues.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, getString(R.string.dummy_data_supplier_name));
        contentValues.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, getString(R.string.dummy_data_supplier_phone_number));

        // Insert a new row using the dummy data
        long rowId = sqLiteDatabase.insert(BookEntry.TABLE_NAME, null, contentValues);

        // Log the addition of the table row
        Log.v(LOG_TAG, "New row ID " + rowId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Execute action based on menu option selected
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                displayDatabaseEntries();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
