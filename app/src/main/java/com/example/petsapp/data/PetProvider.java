package com.example.petsapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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
        // Checking for the code the given Uri has
        int match = sUriMatcher.match(uri);
        Uri insertPetRowNoUri;

        // Checking for the code the match variable has and performing appropriate function
        switch(match){
            // If the match variable has PETS code then insert the value in table
            case PETS:
                 insertPetRowNoUri = insertPetInformation(uri, contentValues);
                break;
            //  If it does not return PETS code then throw a illegal message
            default:
                throw new IllegalArgumentException("Cannot insert value of illega; Uri: " + uri);
        }
        return insertPetRowNoUri;
    }

    /**
     * Helper method that inserts rows into the table
     * @param uri              Uri variable contains the table name
     * @param contentValues    Values to be inserted in the table
     * @return                 Row at which insertion occured
     */
    private Uri insertPetInformation(Uri uri, ContentValues contentValues){
        // Gives a writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Sanity check whether the provided name string is valid or not
        String name = contentValues.getAsString(PetsEntry.COLUMN_PET_NAME);
        if(name == null){
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Checking whether the provided gender value is valid or not
        Integer gender = contentValues.getAsInteger(PetsEntry.COLUMN_PET_GENDER);
        if(gender == null || !PetsEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Choice a valid gender");
        }

        // Checking whether the provided weight value is valid or not
        Integer weight = contentValues.getAsInteger(PetsEntry.COLUMN_PET_WEIGHT);
        if(weight != null && weight < 0){
            throw new IllegalArgumentException("Pet weight cannot be a negative be a value");
        }

        // New row id after inserting a new row into the table
        long newRowId = db.insert(PetsEntry.TABLE_NAME, null, contentValues);

        // If the newRowId is null return a null value
        if(newRowId == -1){
            Log.e(LOG_TAG, "Row is not inserted into the table for uri: " + uri);
            return null;
        }

        // Returns the Uri by adding the row at which insertion occured
        return ContentUris.withAppendedId(PetsEntry.CONTENT_URI,newRowId);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return updatePetsInformation(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePetsInformation(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot update value for a illegal uri: " + uri);
        }
    }


    /**
     * Helper method which updates pet information in the table
     * @param uri               Uri variable contains the table to be effected
     * @param contentValues     Contains the columns to be effected
     * @param selection         Contains columns which should be checked for conditions
     * @param selectionArgs     Value against which the selection has to be compared
     * @return                  The no. pf rows effected by the update query
     */
    private int updatePetsInformation(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){

        // Checks if the contentValues size is 0 return 0 as edited rows
        if(contentValues.size() == 0){
            return 0;
        }
        // Gives a writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Check if the contentValues contains the name key
        if(contentValues.containsKey(PetsEntry.COLUMN_PET_NAME)){
            // Sanity check whether the provided name string is valid or not
            String name = contentValues.getAsString(PetsEntry.COLUMN_PET_NAME);
            if(name == null){
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // Checks if the contentValues contain the gender key
        if(contentValues.containsKey(PetsEntry.COLUMN_PET_GENDER)){
            // Checking whether the provided gender value is valid or not
            Integer gender = contentValues.getAsInteger(PetsEntry.COLUMN_PET_GENDER);
            if(gender == null || !PetsEntry.isValidGender(gender)){
                throw new IllegalArgumentException("Choice a valid gender");
            }
        }

        // Checks if the contentValues contain the weight key
        if(contentValues.containsKey(PetsEntry.COLUMN_PET_WEIGHT)){
            // Checking whether the provided weight value is valid or not
            Integer weight = contentValues.getAsInteger(PetsEntry.COLUMN_PET_WEIGHT);
            if(weight != null && weight < 0){
                throw new IllegalArgumentException("Pet weight cannot be a negative be a value");
            }
        }

        // Updates the value in the table and returns the no. of rows effected
        return db.update(PetsEntry.TABLE_NAME, contentValues, selection, selectionArgs);

    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                // Delete all the values from the table
                return db.delete(PetsEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Deletes the rows that satisfy the condition
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(PetsEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion cannot be done for illegal uri: " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case PETS:
                return PetsEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri + "with match" + match);
        }
    }
}