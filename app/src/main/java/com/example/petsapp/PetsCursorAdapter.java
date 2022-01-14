package com.example.petsapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.petsapp.data.PetsContract.PetsEntry;

public class PetsCursorAdapter extends CursorAdapter {

    /**
     * Constructor used to call the contructor of parent class
     * @param context   The activity from which the class is called
     * @param cursor    Variable containing the list of tuples
     */
    public PetsCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    /**
     * Method creates a empty view for the bindView method to fill
     * @param context   The activity from which a new instance of class is created.
     * @param cursor    Variable containing the list of tuples
     * @param parent    parent class
     * @return          Empty view of layput pet_item
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.pet_item, parent, false);
    }

    /**
     * Method populate the empty view or recycled view with data
     * @param view      View to be populated
     * @param context   Activity from which a new instance is created
     * @param cursor    Variable containing the tuple data
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TextViews to be inflated by the data
        TextView petName = (TextView) view.findViewById(R.id.name_of_pet);
        TextView petBreed = (TextView) view.findViewById(R.id.breed_of_pet);

        // Extracting the name and breed of the pet from the cursor
        String petNameString = cursor.getString(cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_PET_NAME));
        String petBreedString = cursor.getString(cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_PET_BREED));

        // Populating the TextView with the extracted data
        petName.setText(petNameString);
        petBreed.setText(petBreedString);
    }
}
