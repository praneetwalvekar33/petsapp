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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.petsapp.data.PetsContract;
import com.example.petsapp.data.PetsContract.PetsEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    // Id for the Loader being used
    private static final int PET_LOADER = 0;

    // Global variable for Uri
    private Uri mcurrentPetUri;

    // Global variable to check if user has made any changes
    private boolean mPetHasChanged = false;

    // Global variable to check whether we have to update or insert pet information
    private boolean updateCheck = false;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Getting the intent used to call the EditorActivity
        Intent intent = getIntent();

        // Getting the extras present in the Intent
        mcurrentPetUri = intent.getData();

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        // Setting the spinner.
        setupSpinner();

        // Setting up OnTouchListener on each view
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        // Checking if the intent have a string which can parsed as  an Uri
        if(mcurrentPetUri != null){
            setTitle(R.string.editor_activity_title_edit_pet);
            // Initializing the loader.
            getLoaderManager().initLoader(PET_LOADER, null, this);
            // Setting the update check to true
            updateCheck = true;
        }
        else{
            setTitle(R.string.editor_activity_title_new_pet);
            // Invalidate the options menu, to hide the delete option
            invalidateOptionsMenu();

        }
    }

    // OnTouchListener listens for any user touches on the view
    // Implying that the view has been editted
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsContract.PetsEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsContract.PetsEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetsContract.PetsEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    /**
     * Method inflates the options menu
     * @param menu  Menu variable
     * @return      Inflated menu options
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Method is called before the onCreateOptionsMenu
     * @param menu   Menu variable
     * @return       Boolean Value
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        // If a new pet is inserted then hide the delete option
        if(mcurrentPetUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showUnsaveChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        // Creating AlertDialog.Builder set message and clicklistener
        // for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            // User clicked the keep editting button
            // so close the dialog and continue editting the pet
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onBackPressed(){
        // If the user has not added any values continue with normal action of back button
        if(!mPetHasChanged){
            super.onBackPressed();
            return;
        }

        // If the user has made changes then a dialog to warn the user
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user clicked the discard button close the activity
                        finish();
                    }
                };

        // Show unsaved changes
        showUnsaveChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // adding the user provided information into database
                savePetInformation();
                // Exit the activity after saving the data
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the user has not made any changes
                // navigate back to parent activity (CatalogActivity)
                if(!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                }

                // If user has made changes then create a dialog to warn the users
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //  The user has clicked discard changes then return back
                                finish();
                            }
                        };

                // Show a dialog box to the user showing that they have unsaved changes
                showUnsaveChangesDialog(discardButtonClickListener);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePetInformation(){
        // Storing values from user into variables
        String petName = mNameEditText.getText().toString().trim();
        String petBreed = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();

        // creating Content value to add tuple into database
        ContentValues values = new ContentValues();
        values.put(PetsEntry.COLUMN_PET_NAME, petName);
        values.put(PetsEntry.COLUMN_PET_BREED, petBreed);
        values.put(PetsEntry.COLUMN_PET_GENDER, mGender);


        // If the weight is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int weight = 0;
        if (!TextUtils.isEmpty(weightString)) {
            weight = Integer.parseInt(weightString);
        }
        values.put(PetsEntry.COLUMN_PET_WEIGHT, weight);

        if(updateCheck){
            String selection = PetsEntry._ID + "=?";
            String[] selectionArg = new String[]{String.valueOf(ContentUris.parseId(mcurrentPetUri))};
            int newRowUri = getContentResolver().update(mcurrentPetUri, values, selection, selectionArg);

            // If the Uri value is null show in toast message that insertion failed
            if(newRowUri == 0){
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,getString(R.string.editor_insert_pet_successful) + newRowUri, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // Uri to check if the pet has been inserted or not
            Uri newRowUri = null;

            // If the user has not inserted any values
            if (mcurrentPetUri == null &&
                    TextUtils.isEmpty(petName) && TextUtils.isEmpty(petBreed) &&
                    TextUtils.isEmpty(weightString) && mGender == PetsEntry.GENDER_UNKNOWN){
                Toast.makeText(this, getString(R.string.editor_insert_pet_empty_values),
                        Toast.LENGTH_SHORT).show();
                return;
            }


            // Adding the tuple into the database
            newRowUri = getContentResolver().insert(mcurrentPetUri, values);

            // If the Uri value is null show in toast message that insertion failed
            if(newRowUri == null){
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,getString(R.string.editor_insert_pet_successful) + newRowUri, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method is called to create a loader
     * @param i         Id of the loader
     * @param bundle    Values provided
     * @return          Loader instances
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        // The attributes we want to use in the edit pet activity
        String[] projection = {PetsEntry._ID,
                    PetsEntry.COLUMN_PET_NAME,
                    PetsEntry.COLUMN_PET_BREED,
                    PetsEntry.COLUMN_PET_GENDER,
                    PetsEntry.COLUMN_PET_WEIGHT};

        String selection = PetsEntry._ID + "=?";
        String[] selectionArg = new String[]{String.valueOf(ContentUris.parseId(mcurrentPetUri))};

        return new CursorLoader(this, mcurrentPetUri,
                projection,
                selection,
                selectionArg,
                null);
    }

    /**
     * Method is called after onCreateLoader() fills the view in the editor activity
     * @param loader    CursorLoader containing the cursor
     * @param cursor    Variable containing the tuples
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        // Moving the cursor to zero position
        cursor.moveToNext();

        // Column id for each attribute
        int petNameColumnId = cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_PET_NAME);
        int petBreedColumnId = cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_PET_BREED);
        int petGenderColumnId = cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_PET_GENDER);
        int petWeightColumnId = cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_PET_WEIGHT);

        // Values for each attribute present in cursor
        String petNameString = cursor.getString(petNameColumnId);
        String petBreedString = cursor.getString(petBreedColumnId);
        int petGenderInteger = cursor.getInt(petGenderColumnId);
        int petWeightInteger = cursor.getInt(petWeightColumnId);

        // Setting the values for each view in the editor activity
        mNameEditText.setText(petNameString);
        mBreedEditText.setText(petBreedString);
        mWeightEditText.setText(Integer.toString(petWeightInteger));
        mGenderSpinner.setSelection(petGenderInteger);
    }

    /**
     * Method resets the view in the editor activity
     * @param loader    CursorLoader containing the cursors
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mBreedEditText.setText(null);
        mWeightEditText.setText(null);
        mGenderSpinner.setSelection(PetsEntry.GENDER_UNKNOWN);
    }
}