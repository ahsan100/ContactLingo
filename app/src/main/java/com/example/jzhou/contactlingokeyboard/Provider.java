package com.example.jzhou.contactlingokeyboard;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.aware.utils.DatabaseHelper;

import java.io.File;
import java.util.HashMap;

/**
 * Created by ahsanmanzoor on 13/02/2016.
 */
public class Provider extends ContentProvider {

    public static String AUTHORITY = "com.contactlingo.provider";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "message.db";

    private static final int MESSAGE = 1;
    private static final int MESSAGE_ID = 2;



    @Override
    public boolean onCreate() {
        System.out.println("INSIDE create");
        AUTHORITY = getContext().getPackageName() + ".provider.message";
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], MESSAGE);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", MESSAGE_ID);

        tableMap = new HashMap<String, String>();
        tableMap.put(BasicData._ID, BasicData._ID);
        tableMap.put(BasicData.CONTACT, BasicData.CONTACT);
        tableMap.put(BasicData.FIRST_LANG, BasicData.FIRST_LANG);
        tableMap.put(BasicData.SECOND_LANG, BasicData.SECOND_LANG);
        return true;
    }


    public static final class BasicData implements BaseColumns {
        private BasicData(){};
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/message");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.contactlingo.message";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.contactlingo.message";
        public static final String _ID = "_ID";
        public static final String CONTACT = "contact";
        public static final String FIRST_LANG = "first_lang";
        public static final String SECOND_LANG = "second_lang";

    }

    public static final String[] DATABASE_TABLES = { "message" };
    public static final String[] TABLES_FIELDS = {
            BasicData._ID + " integer primary key autoincrement,"
                    + BasicData.CONTACT + " text default '',"
                    + BasicData.FIRST_LANG + " text default '',"
                    + BasicData.SECOND_LANG + " text default ''," + "UNIQUE("
                    + BasicData.CONTACT + ")" };

    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> tableMap = null;
    private static DatabaseHelper databaseHelper = null;
    private static SQLiteDatabase database = null;

    private boolean initializeDB() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper( getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS );
        }
        if( databaseHelper != null && ( database == null || ! database.isOpen() )) {
            database = databaseHelper.getWritableDatabase();
        }
        return( database != null && databaseHelper != null);
    }

    public static void resetDB(Context c ) {

        File db = new File(DATABASE_NAME);
        db.delete();
        databaseHelper = new DatabaseHelper( c, DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
        if( databaseHelper != null ) {
            database = databaseHelper.getWritableDatabase();
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return null;
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                qb.setTables(DATABASE_TABLES[0]);
                qb.setProjectionMap(tableMap);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {
            Log.e("ERRoR", e.getMessage());
            return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                return BasicData.CONTENT_TYPE;
            case MESSAGE_ID:
                return BasicData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues new_values) {
        if (!initializeDB()) {
            Log.w(AUTHORITY, "Database unavailable...");
            return null;
        }

        ContentValues values = (new_values != null) ? new ContentValues(new_values) : new ContentValues();

        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                long _id = database.insert(DATABASE_TABLES[0], BasicData.CONTACT, values);
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(BasicData.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY, "Database unavailable...");
            return 0;
        }
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                count = database.delete(DATABASE_TABLES[0], selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
                break;
            default:
                database.close();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
