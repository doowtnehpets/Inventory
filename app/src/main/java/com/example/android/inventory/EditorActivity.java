package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Variables -----------------------------------------------------------------------------------

    // Editor loader ID
    private static final int EDITOR_LOADER = 1;

    // EditText, Button and TextView fields to pull data from the layout
    private EditText productNameEditText;
    private EditText priceEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneEditText;
    private Button minusButton;
    private Button plusButton;
    private TextView quantityTextView;

    // Quantity amount
    private int bookQuantity = 0;

    // Boolean to see if the book has been edited
    private boolean bookHasChanged = false;

    // OnTouch Listener to check if the user has edited a field
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    // Book URI to store what was passed from the intent
    private Uri currentBookUri;

    // Activity Life Cycle Methods -----------------------------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Check the intent that was passed to this activity to see if a book was supplied
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        // If the intent doesn't have any data, change the title to "Add a Book"
        if (currentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_add_a_book));
            invalidateOptionsMenu();
        } else {
            getLoaderManager().initLoader(EDITOR_LOADER, null, this);
        }

        // Grab the views from the layout
        productNameEditText = findViewById(R.id.activity_editor_product_name_edit_text);
        priceEditText = findViewById(R.id.activity_editor_price_edit_text);
        supplierNameEditText = findViewById(R.id.activity_editor_supplier_name_edit_text);
        supplierPhoneEditText = findViewById(R.id.activity_editor_supplier_phone_edit_text);
        minusButton = findViewById(R.id.activity_editor_minus_button);
        plusButton = findViewById(R.id.activity_editor_plus_button);
        quantityTextView = findViewById(R.id.activity_editor_quantity_text_view);

        // Set up the OnTouchListener for the input fields
        productNameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneEditText.setOnTouchListener(touchListener);

        // Set up the minus button click listener
        View.OnClickListener minusOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If quantity is already zero, don't decrease it
                if (bookQuantity > 0) bookQuantity--;
                quantityTextView.setText(Integer.toString(bookQuantity));
                bookHasChanged = true;
            }
        };
        minusButton.setOnClickListener(minusOnClickListener);

        // Set up the plus button click listener
        View.OnClickListener plusOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookQuantity++;
                quantityTextView.setText(Integer.toString(bookQuantity));
                bookHasChanged = true;
            }
        };
        plusButton.setOnClickListener(plusOnClickListener);
    }

    // Options Menu --------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from the layout file
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // If the intent doesn't contain book data, then hide the delete option
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.menu_editor_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // "Save" selected from menu
            case R.id.menu_editor_save:
                saveBook();
                return true;
            // "Delete" selected from menu
            case R.id.menu_editor_delete:
                // TODO: Show delete confirmation dialog
                return true;
            // "Up" arrow selected
            case android.R.id.home:
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                // TODO: set up dialog for unsaved changes
        }

        return super.onOptionsItemSelected(item);
    }

    // Helper Methods ------------------------------------------------------------------------------

    // Save the book based on info supplied by the user
    private void saveBook() {
        // Read the data from the fields
        String productName = productNameEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String quantity = Integer.toString(bookQuantity);
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();

        // Check if the product name is null or empty, throw error message and return early if so
        if (productName == null || productName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format the price so it has 2 decimal places
        String priceFormatted = "0.00";
        if (price != null && !price.isEmpty())
            priceFormatted = String.format(java.util.Locale.getDefault(), "%.2f", Float.parseFloat(price));

        // Create the ContentValue object and put the information in it
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, productName);
        contentValues.put(BookEntry.COLUMN_BOOK_PRICE, priceFormatted);
        contentValues.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        contentValues.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierName);
        contentValues.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, supplierPhone);

        // Save the book data
        if (currentBookUri == null) {
            // New book, so insert into the database
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);

            // Show toast if successful or not
            if (newUri == null)
                Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT).show();
        } else {
            // Existing book, so save changes
            int rowsAffected = getContentResolver().update(currentBookUri, contentValues, null, null);

            // Show toast if successful or not
            if (rowsAffected == 0)
                Toast.makeText(this, getString(R.string.editor_update_book_failed), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.editor_update_book_successful), Toast.LENGTH_SHORT).show();

        }

        finish();
    }

    // Loader Methods ------------------------------------------------------------------------------
    @NonNull
    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // Projection for the book we want to load
        String[] projection = {
                BookEntry.COLUMN_BOOK_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER};

        return new android.content.CursorLoader(this,
                currentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // Load the data if any was returned
        if (cursor.moveToFirst()) {
            // Get the column indexes
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);

            // Pull the data from the columns
            String productName = cursor.getString(productNameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Set the data pulled onto the objects
            productNameEditText.setText(productName);
            priceEditText.setText(price);
            quantityTextView.setText(quantity);
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);

            // Set the global variable for the quantity of books
            bookQuantity = Integer.parseInt(quantity);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // Invalidate the data
        productNameEditText.setText("");
        priceEditText.setText("");
        quantityTextView.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }
}
