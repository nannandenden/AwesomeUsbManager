package com.example.nanden.awesomeusbmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Created by nanden on 11/15/17.
 */

public class LockerDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = LockerDatabaseHelper.class.getSimpleName();

    // for making the SQliteOpenHelper class singleton class
    private static LockerDatabaseHelper sInstance;
    // database info
    private static final String DATABASE_NAME = "lockerDatabase";
    private static final int DATABASE_VERSION = 1;

    // table name
    private static final String TABLE_LOCKER = "locker";

    // locker table columns
    private static final String KEY_LOCKER_ID = "id";
    private static final String KEY_LOCKER_CODE = "code";
    private static final String KEY_LOCKER_NUMBER = "number";

    public static synchronized LockerDatabaseHelper getsInstance(Context context) {
        if (sInstance == null) {
            // use application context to ensure that not accidentally leak the activity's context
            return new LockerDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private LockerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // called when the database is created for the first time
    // If a database already exist on disk with the same DATABASE_NAME, onCreate method will not be called
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_LOCKER +
                "(" +
                KEY_LOCKER_ID + " INTEGER PRIMARY KEY," + // primary key
                KEY_LOCKER_CODE + " TEXT," +
                KEY_LOCKER_NUMBER + " TEXT" +
                ")");
    }

    // called when database need to be upgraded
    // This method will be only called if a database already exists on disk with the same DATABASE_NAME
    // but the DATABASE_VERSION is different than the version of the database that exist on disk
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // drop the old database and re-create the new one
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCKER);
            onCreate(sqLiteDatabase);
        }
    }

    public void addListOfLocker(List<Locker> lockerList) {
        for (Locker locker : lockerList) {
            addLocker(locker);
        }
    }

    public void addLocker(Locker locker) {
        // create/open a database for writing
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            // for store a set of values
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_LOCKER_CODE, locker.code);
            contentValues.put(KEY_LOCKER_NUMBER, locker.lockerNumber);
            // insert row into a database
            // nullColumnHack:  If not set to null, the nullColumnHack parameter provides the name of nullable column name to explicitly insert a NULL into in the case where your values is empty.
            sqLiteDatabase.insertOrThrow(TABLE_LOCKER, null, contentValues);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, "Fail to insert into the table: " + e.getMessage());
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public Locker getLockerNumber(String code) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Locker locker = null;
        String QUERY = String.format("SELECT * FROM %S WHERE %S = %S", TABLE_LOCKER, KEY_LOCKER_CODE, code);
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(QUERY, null);
            cursor.moveToFirst();
            locker = new Locker(cursor.getString(cursor.getColumnIndex(KEY_LOCKER_CODE)), cursor.getString(cursor.getColumnIndex(KEY_LOCKER_NUMBER)));
            Log.d(LOG_TAG, "locker.code: " + locker.code + "\tlocker.lockerNumber: " + locker.lockerNumber);
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, "Fail to get the locker number: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return locker;
    }
}
