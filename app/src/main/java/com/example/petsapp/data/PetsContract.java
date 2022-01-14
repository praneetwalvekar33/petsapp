package com.example.petsapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

public final class PetsContract {

    /** Content authority for content uri */
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    /** Content scheme for content uri */
    public static final String SCHEME = "content://";

    /** Base content uri */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PETS = "pets";
    private PetsContract() {};

    /**
     * Class containing the constants to be used
     */
    public static final class PetsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        // The MIME type for a list of pets
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_PETS;

        // The MIME type for a single pet
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final  String TABLE_NAME = "pets";

        public static final  String _ID = BaseColumns._ID;
        public static final  String COLUMN_PET_NAME = "name";
        public static final  String COLUMN_PET_BREED = "breed";
        public static final  String COLUMN_PET_GENDER = "gender";
        public static final  String COLUMN_PET_WEIGHT = "weight";

        public static final  int GENDER_UNKNOWN = 0;
        public static final  int GENDER_MALE = 1;
        public static final int GENDER_FEMALE= 2;

        /**
         * Method check whether passed value is selected from the mentioned choices
         * @param gender  Integer value
         * @return        boolean value
         */
        public static boolean isValidGender(Integer gender){
            if(gender == GENDER_MALE || gender == GENDER_FEMALE || gender == GENDER_UNKNOWN){
                return true;
            }
            return false;
        }
    }

}
