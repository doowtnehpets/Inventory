package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Default constructor
     */
    public BookCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item using the layout list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Grab the TextViews for product name, price and quantity from the list_item
        TextView productNameTextView = view.findViewById(R.id.list_item_product_name);
        TextView priceTextView = view.findViewById(R.id.list_item_product_price);
        TextView quantityTextView = view.findViewById(R.id.list_item_quantity);
        Button saleButton = view.findViewById(R.id.list_item_sale_button);

        // Find the column index for the selected book attributes
        int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

        // Read the attributes from the cursor
        String productName = cursor.getString(productNameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);

        // If price is null or empty, then set to 0.00
        if (price == null || price.isEmpty()) price = "0.00";

        // Set the TextViews to the data pulled from the cursor
        productNameTextView.setText(productName);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);

        // Get the current Uri
        String currentId = cursor.getString(cursor.getColumnIndex(BookEntry._ID));
        final Uri contentUri = Uri.withAppendedPath(BookEntry.CONTENT_URI, currentId);

        // Set the click listener for the button
        View.OnClickListener saleButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityInteger = Integer.parseInt(quantity);
                if (quantityInteger > 0) {
                    // Update the quantity in the database
                    quantityInteger--;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BookEntry.COLUMN_BOOK_QUANTITY, Integer.toString(quantityInteger));
                    context.getContentResolver().update(contentUri, contentValues, null, null);
                } else
                    Toast.makeText(context, R.string.out_of_stock_message, Toast.LENGTH_SHORT).show();

            }
        };
        saleButton.setOnClickListener(saleButtonClickListener);
    }
}
