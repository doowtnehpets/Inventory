package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Grab the TextViews for product name, price and quantity from the list_item
        TextView productNameTextView = view.findViewById(R.id.list_item_product_name);
        TextView priceTextView = view.findViewById(R.id.list_item_product_price);
        TextView quantityTextView = view.findViewById(R.id.list_item_quantity);

        // Find the column index for the selected book attributes
        int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

        // Read the attributes from the cursor
        String productName = cursor.getString(productNameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        // Set the TextViews to the data pulled from the cursor
        productNameTextView.setText(productName);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);
    }
}
