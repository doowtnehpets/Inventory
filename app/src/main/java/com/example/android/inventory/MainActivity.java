package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventory.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Log tag
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Book loader ID
    private static final int BOOK_LOADER = 0;

    // BookCursorAdapter adapter for the ListView
    BookCursorAdapter bookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the action for the FloatingAction
        FloatingActionButton floatingActionButton = findViewById(R.id.activity_main_floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Start the editor activity once implemented
                Toast.makeText(MainActivity.this, " FAB Click!", Toast.LENGTH_SHORT).show();
            }
        });

        // Grab the ListView for the books
        ListView bookListView = findViewById(R.id.activity_main_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items
        View emptyView = findViewById(R.id.activity_main_empty_view);
        bookListView.setEmptyView(emptyView);

        // Set up the Cursor adapter
        bookCursorAdapter = new BookCursorAdapter(this, null, 0);
        bookListView.setAdapter(bookCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: implement click function to start editor activity
                Toast.makeText(MainActivity.this, "Item Click!", Toast.LENGTH_SHORT).show();
            }
        });

        // Start the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Add a book to the database using made up data
     */
    private void insertBook() {
        // Create a ContentValues object to insert a row into the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, getString(R.string.dummy_data_product_name));
        contentValues.put(BookEntry.COLUMN_BOOK_PRICE, getString(R.string.dummy_data_price));
        contentValues.put(BookEntry.COLUMN_BOOK_QUANTITY, getString(R.string.dummy_data_quantity));
        contentValues.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, getString(R.string.dummy_data_supplier_name));
        contentValues.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, getString(R.string.dummy_data_supplier_phone_number));

        // Insert a new row using the dummy data
        getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
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
            // "Insert dummy" menu item selected
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;

            // "Delete all entries" menu item selected
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to delete all books
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " rows deleted from database");
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns we want
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY};

        // Return a CursorLoader to query in the background
        return new android.content.CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // Update the cursor with the new data
        bookCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // Reset the cursor adapter with null
        bookCursorAdapter.swapCursor(null);
    }
}
