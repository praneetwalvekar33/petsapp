package com.example.petsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.petsapp.data.PetsDbHelper;
import com.example.petsapp.data.PetsContract.PetsEntry;

//import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    PetsDbHelper mDbHelper = new PetsDbHelper(this);

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

        displayDatabaseInfo();
    }

    /**
     * This function is mostly called when user return from another activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
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
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        //PetsDbHelper mDbHelper = new PetsDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {PetsEntry._ID,
                            PetsEntry.COLUMN_PET_NAME,
                            PetsEntry.COLUMN_PET_BREED,
                            PetsEntry.COLUMN_PET_GENDER,
                            PetsEntry.COLUMN_PET_WEIGHT};

        // Cursor containing the list of tuples from querying the database
        Cursor cursor = getContentResolver().query(PetsEntry.CONTENT_URI,projection, null, null, null);

        // ListView which will be populated with the pets data
        ListView listView = (ListView) findViewById(R.id.list_view);

        // View which will be displayed on ListView when there is no data present in cursor adapter
        View emptyView = findViewById(R.id.empty_list_view);

        // Setting up a empty view for the ListView
        listView.setEmptyView(emptyView);

        // Setup of CurosrAdapter for creating a view for each pets from cursor data
        PetsCursorAdapter petsAdapter = new PetsCursorAdapter(this, cursor);

        // Attaching adapter to the listview
        listView.setAdapter(petsAdapter);
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
}