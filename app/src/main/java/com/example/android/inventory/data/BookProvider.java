package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventory.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    // Log Tag
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    // URI matcher for books table
    private static final int BOOKS = 100;

    // URI matcher for single book from books table
    private static final int BOOK_ID = 101;

    // UriMatcher object to match URI to code
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer to add the URI matches
    static {
        // Add the patterns for the URI matcher
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    // Database helper object
    private BookDbHelper bookDbHelper;

    @Override
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase sqLiteDatabase = bookDbHelper.getReadableDatabase();

        // Cursor object to return the data from the query
        Cursor cursor;

        // Find the corresponding code for the query and execute the query based on the match
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // for BOOKS code query the entire database
                cursor = sqLiteDatabase.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                // for BOOK_ID find the ID from the URI and query for the 1 book
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                return null;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                return null;
        }
    }

    /**
     * Insert a book into the database
     */
    @Nullable
    private Uri insertBook(Uri uri, ContentValues contentValues) {
        // Check the product name is valid
        String productName = contentValues.getAsString(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
        if (productName == null) return null;

        // No need to check price, quantity, supplier name or phone number as there's defaults

        // Get a writable database
        SQLiteDatabase sqLiteDatabase = bookDbHelper.getWritableDatabase();

        // Insert a book into the database
        long id = sqLiteDatabase.insert(BookEntry.TABLE_NAME, null, contentValues);

        // Return early if there's an issue
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify listeners of the change
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the id appended to the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                return 0;
        }
    }

    /**
     * Update books in the database from the values provided
     */
    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Check if product name is in the values and verify it's not null
        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_PRODUCT_NAME)) {
            String productName = contentValues.getAsString(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            if (productName == null) return 0;
        }

        // No need check the other values as there's defaults

        // If there are no values to update, then return early
        if (contentValues.size() == 0) return 0;

        // Get writable database to update values
        SQLiteDatabase sqLiteDatabase = bookDbHelper.getWritableDatabase();

        // Execute the update on the database
        int rowsUpdated = sqLiteDatabase.update(BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // If the change was successful, notify the listeners
        if (rowsUpdated != 0) getContext().getContentResolver().notifyChange(uri, null);

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase sqLiteDatabase = bookDbHelper.getWritableDatabase();

        // Track the number of rows deleted
        int rowsDeleted;

        // Delete row or table data based on match
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = sqLiteDatabase.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = sqLiteDatabase.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                rowsDeleted = 0;
        }

        // If 1 or more rows were updated, notify the listeners
        if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);

        // Return the number of rows deleted
        return rowsDeleted;
    }

}
