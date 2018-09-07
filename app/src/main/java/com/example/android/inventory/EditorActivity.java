package com.example.android.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditorActivity extends AppCompatActivity {

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
    private int quantity = 0;
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

    // onCreate ------------------------------------------------------------------------------------
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
                if (quantity > 0) quantity--;
                quantityTextView.setText(Integer.toString(quantity));
                bookHasChanged = true;
            }
        };
        minusButton.setOnClickListener(minusOnClickListener);

        // Set up the plus button click listener
        View.OnClickListener plusOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                quantityTextView.setText(Integer.toString(quantity));
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
        // TODO: Implement menu selected tasks

        return super.onOptionsItemSelected(item);
    }
}
