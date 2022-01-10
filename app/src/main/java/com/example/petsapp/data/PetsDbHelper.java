package com.example.petsapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.petsapp.data.PetsContract.PetsEntry;

public class PetsDbHelper extends SQLiteOpenHelper {
    //  Variable to set the database name
    public static final String DATABASE_NAME = "shelter.db";

    // Variable for the version of the database
    public static final int DATABASE_VERSION = 1;

    /**
     * Contructor to create instance of the class
     * @param context   From which activity is it is called
     */
    public PetsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is method is called when the table in the database is created for the 1st time
     * @param db The SQLite database in which the table is created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLES = "CREATE TABLE " + PetsEntry.TABLE_NAME
                + "("+ PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PetsEntry.COLUMN_PET_NAME + " TEXT NOT NULL,"
                + PetsEntry.COLUMN_PET_BREED + " TEXT,"
                + PetsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL,"
                + PetsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_TABLES);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
