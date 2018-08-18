package com.example.android.inventory.data;

import android.provider.BaseColumns;

public class BookContract {

    // Empty constructor
    private BookContract() {
    }

    public static final class BookEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "books";

        // ID for the books
        public static final String _ID = BaseColumns._ID;

        // Columns
        public static final String COLUMN_BOOK_PRODUCT_NAME = "product_name";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_BOOK_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}
