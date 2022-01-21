package com.example.petsapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.petsapp.data.PetsContract.PetsEntry;
import com.example.petsapp.data.PetsDbHelper;



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    PetsDbHelper mDbHelper = new PetsDbHelper(this);

    // Constant used as an id for the loader being used
    // We onlu have one loader so it is set as constant value
    private static final int PET_LOADER = 0;

    // Global variable for CursorLoader
    PetsCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find ListView Which will be populated with pets data
        ListView petListView = findViewById(R.id.list_view);

        // Find the empty view used for ListView when no data is present
        View petEmptyView = findViewById(R.id.empty_list_view);

        // Setting an empty view on the ListView
        petListView.setEmptyView(petEmptyView);

        // Initializing the CursorAdapter
        mCursorAdapter = new PetsCursorAdapter(this, null);

        // Setting an CursorAdapter for ListView
        petListView.setAdapter(mCursorAdapter);

        // Setting a ClickListener on each item in the  ListView
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Creating Uri for the item clicked to retrive the data from that item
                Uri petDataUri = ContentUris.withAppendedId(PetsEntry.CONTENT_URI,id);

                // Intent to call EditorActivity from this activity
                Intent editPetDataIntent =  new Intent(MainActivity.this, EditorActivity.class);

                // Adding Uri of the clicked item in the intent
                editPetDataIntent.setData(petDataUri);

                // Starting the activity by passing the intent
                startActivity(editPetDataIntent);
            }
        });

        // Starting the loader
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPetInformation();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Confirmation dialog box is shown
                showDeleteConfirmationDialogBox();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void insertPetInformation(){
        // Getting data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Used to store the values of the attribute for the tuple
        ContentValues values = new ContentValues();
        values.put(PetsEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetsEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetsEntry.COLUMN_PET_GENDER, PetsEntry.GENDER_MALE);
        values.put(PetsEntry.COLUMN_PET_WEIGHT, 7);

        // Inserting the tuples in the database
        Uri newRowUri = getContentResolver().insert(PetsEntry.CONTENT_URI, values);

        Log.v("MainActivity", "New row added" + newRowUri);
    }

    /**
     * Method shows the confirmation dialog box before deleting the data
     */
    private void showDeleteConfirmationDialogBox(){
        // Setting AlertDialog.Builder messages and ClickListener
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clicked on delete button so delete the pet
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clicked on cancel button so close the dialog box and continue editing the pet
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Method used to delete the data
     */
    private void deletePet(){
        getContentResolver().delete(PetsEntry.CONTENT_URI, null, null);
        Toast.makeText(this, getString(R.string.editor_delete_pet_successful), Toast.LENGTH_SHORT);
    }

    /**
     * Creates a new CursorLoader
     * @param i         Id for the CursorLoader to be used
     * @param bundle    null values
     * @return          CursorLoader
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        // Projection that specifies the column of the table we want to query
        String[] projection = {PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED};

        //  Loader will execute the contentprovider's query method on a background thread
        return new CursorLoader(this, PetsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    /**
     * The CursorLoader created in onCreateLoader is passed to this method to set the cursor
     * @param loader    CursorLoader created in onCreateLoader
     * @param cursor    Varible containing the tuple from database
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Swap the CursorAdapter with new cursor
        mCursorAdapter.swapCursor(cursor);
    }

    /**
     * Method resets the CursorLoader
     * @param loader    CursorLoader to be reseted
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Swap the CursorAdapter's cursor with null value
        mCursorAdapter.swapCursor(null);
    }
}