package com.example.petsapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.petsapp.data.PetsContract.PetsEntry;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /** Code for accessing the entire table */
    private static final int PETS = 100;

    /** Code for accessing a particular tuple from the table */
    private static final int PET_ID = 101;

    /** UriMatcher variable */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        // Maps the Uri to check if the entire table has to be accessed
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS, PETS);

        // Maps the Uri to check if a particular tuple from the table has to be accessed
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PATH_PETS + "/#", PET_ID);
    }

    /** Database Helper variable */
    private static PetsDbHelper mDbHelper;
    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Database helper variable which can be used by other methods
        mDbHelper = new PetsDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                cursor = db.query(PetsContract.PetsEntry.TABLE_NAME, projection,selection, selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query illegal Uri: " + uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}