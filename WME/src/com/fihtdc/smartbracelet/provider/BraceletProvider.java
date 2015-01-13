package com.fihtdc.smartbracelet.provider;


import java.util.HashMap;

import com.fihtdc.smartbracelet.util.Constants;
import com.fihtdc.smartbracelet.util.Utility;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class BraceletProvider extends ContentProvider {

    public static final String TAG = "BraceletProvider";
    private static final UriMatcher sUriMatcher ;
    private BraceletSQLiteHelper mSQLHelper = null;
    
    /**
     * A projection map used to select columns from the database
     */
    private static HashMap<String, String> sProfileProjectionMap;
    private static HashMap<String,String> sMeasureProjectionMap;
    private static HashMap<String,String> sCoachingProjectionMap;
    private static HashMap<String,String> sDayCoachingProjectionMap;
    
    private static final int BRACELET_PROFILE = 1;
    private static final int BRACELET_PROFILE_ID = 2;
    private static final int BRACELET_MEASURE = 3;
    private static final int BRACELET_MEASURE_ID = 4;
    private static final int BRACELET_COACHING = 5;
    private static final int BRACELET_COACHING_ID = 6;
    
    private static final int BRACELET_DAYCOACHING = 7;
    private static final int BRACELET_DAYCOACHING_ID = 8;
    
    private ContentResolver mContentResolver;
    //Creates and initializes the URI matcher
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        
        sProfileProjectionMap  = new HashMap<String, String>();
        sMeasureProjectionMap = new HashMap<String, String>();
        sCoachingProjectionMap = new HashMap<String, String>();
        sDayCoachingProjectionMap = new HashMap<String,String>();
        
        sUriMatcher.addURI(BraceletInfo.AUTHORITY, "profile", BRACELET_PROFILE);
        sUriMatcher.addURI(BraceletInfo.AUTHORITY, "profile/#", BRACELET_PROFILE_ID);
        
        sProfileProjectionMap.put(BraceletInfo.Profile._ID, BraceletInfo.Profile._ID);
        sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_PROFILE_NAME, BraceletInfo.Profile.COLUMN_NAME_PROFILE_NAME);
        sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_PROFILE_GENDER, BraceletInfo.Profile.COLUMN_NAME_PROFILE_GENDER);
        sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_BIRTHDAY, BraceletInfo.Profile.COLUMN_NAME_BIRTHDAY);
        sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_HEIGHT, BraceletInfo.Profile.COLUMN_NAME_HEIGHT);
        sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_WEIGHT, BraceletInfo.Profile.COLUMN_NAME_WEIGHT);
        
        sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_WEIGHT_UNIT, BraceletInfo.Profile.COLUMN_NAME_WEIGHT_UNIT);
        sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_HEIGHT_UNIT, BraceletInfo.Profile.COLUMN_NAME_HEIGHT_UNIT);
        
        //sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_DATA1, BraceletInfo.Profile.COLUMN_NAME_DATA1);
        //sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_DATA2, BraceletInfo.Profile.COLUMN_NAME_DATA2);
        //sProfileProjectionMap.put(BraceletInfo.Profile.COLUMN_NAME_DATA3, BraceletInfo.Profile.COLUMN_NAME_DATA3);
        
        sUriMatcher.addURI(BraceletInfo.AUTHORITY, "measure", BRACELET_MEASURE);
        sUriMatcher.addURI(BraceletInfo.AUTHORITY, "measure/#", BRACELET_MEASURE_ID);
        
        sMeasureProjectionMap.put(BraceletInfo.Measure._ID,BraceletInfo.Measure._ID);
        sMeasureProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_PROFILE_NAME, BraceletInfo.Measure.COLUMN_NAME_PROFILE_NAME);
        sMeasureProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_ANS_AGE, BraceletInfo.Measure.COLUMN_NAME_ANS_AGE);
        sMeasureProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_AGILITY, BraceletInfo.Measure.COLUMN_NAME_AGILITY);
        sMeasureProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_BPM, BraceletInfo.Measure.COLUMN_NAME_BPM);
        sMeasureProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_PERSONALITY_TYPE, BraceletInfo.Measure.COLUMN_NAME_PERSONALITY_TYPE);
        sMeasureProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_TEST_TIME, BraceletInfo.Measure.COLUMN_NAME_TEST_TIME);
        sMeasureProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_EMOTION_STATUS, BraceletInfo.Measure.COLUMN_NAME_EMOTION_STATUS);
        sMeasureProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_AGE_HF, BraceletInfo.Measure.COLUMN_NAME_AGE_HF);
        sMeasureProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_AGE_LF, BraceletInfo.Measure.COLUMN_NAME_AGE_LF);

        
        sUriMatcher.addURI(BraceletInfo.AUTHORITY, "coaching", BRACELET_COACHING);
        sUriMatcher.addURI(BraceletInfo.AUTHORITY, "coaching/#", BRACELET_COACHING_ID);
        
        sCoachingProjectionMap.put(BraceletInfo.Coaching._ID, BraceletInfo.Coaching._ID);
        sCoachingProjectionMap.put(BraceletInfo.Coaching.COLUMN_NAME_PROFILE_NAME, BraceletInfo.Coaching.COLUMN_NAME_PROFILE_NAME);
        sCoachingProjectionMap.put(BraceletInfo.Coaching.COLUMN_NAME_AGILITY, BraceletInfo.Coaching.COLUMN_NAME_AGILITY);
        sCoachingProjectionMap.put(BraceletInfo.Coaching.COLUMN_NAME_ANS_AGE, BraceletInfo.Coaching.COLUMN_NAME_ANS_AGE);
        sCoachingProjectionMap.put(BraceletInfo.Coaching.COLUMN_NAME_BPM, BraceletInfo.Coaching.COLUMN_NAME_BPM);
        sCoachingProjectionMap.put(BraceletInfo.Coaching.COLUMN_NAME_CYCLE, BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
        sCoachingProjectionMap.put(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM, BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
        sCoachingProjectionMap.put(BraceletInfo.Coaching.COLUMN_NAME_LEVEL, BraceletInfo.Coaching.COLUMN_NAME_LEVEL);
        sCoachingProjectionMap.put(BraceletInfo.Coaching.COLUMN_NAME_PERSONALITY_TYPE, BraceletInfo.Coaching.COLUMN_NAME_PERSONALITY_TYPE);
        sCoachingProjectionMap.put(BraceletInfo.Coaching.COLUMN_NAME_TEST_TIME, BraceletInfo.Coaching.COLUMN_NAME_TEST_TIME);
        sCoachingProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_AGE_HF, BraceletInfo.Measure.COLUMN_NAME_AGE_HF);
        sCoachingProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_AGE_LF, BraceletInfo.Measure.COLUMN_NAME_AGE_LF);
        sCoachingProjectionMap.put(BraceletInfo.Measure.COLUMN_NAME_EMOTION_STATUS, BraceletInfo.Measure.COLUMN_NAME_EMOTION_STATUS);
        
        sUriMatcher.addURI(BraceletInfo.AUTHORITY, "daycoaching", BRACELET_DAYCOACHING);
        sUriMatcher.addURI(BraceletInfo.AUTHORITY, "daycoaching/#", BRACELET_DAYCOACHING_ID);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching._ID, BraceletInfo.DayCoaching._ID);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_DAYCOACHING_DATE, BraceletInfo.DayCoaching.COLUMN_NAME_DAYCOACHING_DATE);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_TEST_TIME,BraceletInfo.DayCoaching.COLUMN_NAME_TEST_TIME);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE1, BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE1);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT1, BraceletInfo.DayCoaching.COLUMN_NAME_HIT1);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE2, BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE2);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT2, BraceletInfo.DayCoaching.COLUMN_NAME_HIT2);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE3, BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE3);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT3, BraceletInfo.DayCoaching.COLUMN_NAME_HIT3);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE4, BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE4);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT4, BraceletInfo.DayCoaching.COLUMN_NAME_HIT4);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE5, BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE5);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT5, BraceletInfo.DayCoaching.COLUMN_NAME_HIT5);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLEM, BraceletInfo.DayCoaching.COLUMN_NAME_CYCLEM);
        sDayCoachingProjectionMap.put(BraceletInfo.DayCoaching.COLUMN_NAME_HITM, BraceletInfo.DayCoaching.COLUMN_NAME_HITM);
        
    }
    
    public static final String[] COACHING_PROJ = new String[]{
        BraceletInfo.Coaching._ID,
        BraceletInfo.Coaching.COLUMN_NAME_LEVEL,
        BraceletInfo.Coaching.COLUMN_NAME_CYCLE,
        BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM,
        BraceletInfo.Coaching.COLUMN_NAME_TEST_TIME
    };
    Context mContext;
    @Override
    public boolean onCreate() {
        mContext = getContext();
        mSQLHelper = BraceletSQLiteHelper.getInstance(mContext);
        mContentResolver = mContext.getContentResolver();
        return true;
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String whereStr = null;
        
        //Choose the projection and adjust the "where" clause based on URI pattern-matching.
        switch (sUriMatcher.match(uri)) {
            case BRACELET_PROFILE:
                setQueryCondition(false, qb, BraceletInfo.Profile.TABLE_NAME, 
                        sProfileProjectionMap, null);
                break;
            case BRACELET_PROFILE_ID:
                whereStr = BraceletInfo.Profile._ID +
                "=" +uri.getPathSegments().get(BraceletInfo.Profile.PROFILE_ID_PATH_POSITION);
                setQueryCondition(true, qb, BraceletInfo.Profile.TABLE_NAME, 
                        sProfileProjectionMap, whereStr);
                break;
            case BRACELET_MEASURE:
                setQueryCondition(false, qb, BraceletInfo.Measure.TABLE_NAME, 
                        sMeasureProjectionMap, null);
                break;
            case BRACELET_MEASURE_ID:
                whereStr = BraceletInfo.Measure._ID +
                "=" +uri.getPathSegments().get(BraceletInfo.Measure.MEASURE_ID_PATH_POSITION);
                setQueryCondition(true, qb, BraceletInfo.Measure.TABLE_NAME, 
                        sMeasureProjectionMap, whereStr);
                break;
            case BRACELET_COACHING:
                setQueryCondition(false, qb, BraceletInfo.Coaching.TABLE_NAME, 
                        sCoachingProjectionMap, null);
                break;
            case BRACELET_COACHING_ID:
                whereStr = BraceletInfo.Coaching._ID +
                "=" +uri.getPathSegments().get(BraceletInfo.Coaching.COACHING_ID_PATH_POSITION);
                setQueryCondition(true, qb, BraceletInfo.Coaching.TABLE_NAME, 
                        sCoachingProjectionMap, whereStr);
                break;
            case BRACELET_DAYCOACHING:
                setQueryCondition(false, qb, BraceletInfo.DayCoaching.TABLE_NAME, 
                        sDayCoachingProjectionMap, null);
                break;
            case BRACELET_DAYCOACHING_ID:
                whereStr = BraceletInfo.DayCoaching._ID +
                "=" +uri.getPathSegments().get(BraceletInfo.DayCoaching.DAYCOACHING_ID_PATH_POSITION);
                setQueryCondition(true, qb, BraceletInfo.DayCoaching.TABLE_NAME, 
                        sDayCoachingProjectionMap, whereStr);
                break;
            default:
                // If the URI doesn't match any of the known patterns, throw an exception.
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Opens the database object in "read" mode, since no writes need to be done.
        SQLiteDatabase db = mSQLHelper.getReadableDatabase();

        /*
         * Performs the query. If no problems occur trying to read the database, then a Cursor
         * object is returned; otherwise, the cursor variable contains null. If no records were
         * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
         */
        Cursor c = qb.query(
            db,            // The database to query
            projection,    // The columns to return from the query
            selection,     // The columns for the where clause
            selectionArgs, // The values for the where clause
            null,          // don't group the rows
            null,          // don't filter by row groups
            sortOrder        // The sort order
        );

        // Tells the Cursor what URI to watch, so it knows when its source data changes
        c.setNotificationUri(mContentResolver, uri);
        return c;
    }

    private void setQueryCondition(boolean type,SQLiteQueryBuilder sqb,String tableName,HashMap<String, String> map,String whereStr){
        sqb.setTables(tableName);
        sqb.setProjectionMap(map);
        if(type){
            sqb.appendWhere(whereStr);
        } 
    }
    
    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.paradise.bracelet";
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        int matcher = sUriMatcher.match(uri);
        // Validates the incoming URI. Only the full provider URI is allowed for inserts.
        switch (matcher) {
            case BRACELET_PROFILE :
                return insertProfile(uri , initialValues);
            case BRACELET_MEASURE :
                return insertMeasure(uri, initialValues);
            case BRACELET_COACHING:
                return insertCoaching(uri, initialValues);
            case BRACELET_DAYCOACHING:
                return insertDayCoaching(uri,initialValues);
            default :
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    
    private Uri insertDayCoaching(Uri uri, ContentValues initialValues){
        //A map to hold the new record's values.
        ContentValues values;
        // If the incoming values map is not null, uses it for the new values.
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            // Otherwise, create a new value map
            values = new ContentValues();
        }
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        // Performs the insert and returns the ID of the new note.
        long rowId =0;
        try {
            rowId = db.insert(
                    BraceletInfo.DayCoaching.TABLE_NAME,        // The table to insert into.
                null,  // A hack, SQLite sets this column value to null
                                                 // if values is empty.
                values                           // A map of column names, and the values to insert
                                                 // into the columns.
            );
        } catch (Exception  e) {
           Log.d(TAG, "insert the same data exception");
        }

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(BraceletInfo.DayCoaching.CONTENT_ID_URI_BASE, rowId);
            //Notifies observers registered against this provider that the data changed.
            mContentResolver.notifyChange(noteUri, null);
            return noteUri;
        }
        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
    }
    private Uri insertMeasure(Uri uri, ContentValues initialValues){
        
        //A map to hold the new record's values.
        ContentValues values;
        // If the incoming values map is not null, uses it for the new values.
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            // Otherwise, create a new value map
            values = new ContentValues();
        }
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        // Performs the insert and returns the ID of the new note.
        long rowId =0;
        try {
            rowId = db.insert(
                    BraceletInfo.Measure.TABLE_NAME,        // The table to insert into.
                null/*Weather.Cities.COLUMN_NAME_CITY_NAME*/,  // A hack, SQLite sets this column value to null
                                                 // if values is empty.
                values                           // A map of column names, and the values to insert
                                                 // into the columns.
            );
        } catch (Exception  e) {
           Log.d(TAG, "insert the same data exception");
        }

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(BraceletInfo.Measure.CONTENT_ID_URI_BASE, rowId);
            //Notifies observers registered against this provider that the data changed.
            mContentResolver.notifyChange(noteUri, null);
            return noteUri;
        }
        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
    }
    
    private Uri insertCoaching(Uri uri, ContentValues initialValues){
        
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        // Performs the insert and returns the ID of the new note.
        long rowId =0;
        try {
            rowId = db.insert(
                    BraceletInfo.Coaching.TABLE_NAME,        // The table to insert into.
                null,  // A hack, SQLite sets this column value to null
                                                 // if values is empty.
                values                           // A map of column names, and the values to insert
                                                 // into the columns.
            );
        } catch (Exception  e) {
           Log.d(TAG, "insert the same data exception");
        }

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            //Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(BraceletInfo.Coaching.CONTENT_ID_URI_BASE, rowId);
            //Notifies observers registered against this provider that the data changed.
            mContentResolver.notifyChange(noteUri, null);
            doInsertDayCoaching(initialValues);
            return noteUri;
        }
        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
    }
    
    private void doInsertDayCoaching(ContentValues values){
        long time = values.getAsLong(BraceletInfo.Coaching.COLUMN_NAME_TEST_TIME);
        ContentValues endValues = new ContentValues();
        if(time == 0){
            throw new IllegalArgumentException("time can not be zero");
        }
       String level = values.getAsString(BraceletInfo.Coaching.COLUMN_NAME_LEVEL);
       if(level == null){
           throw new IllegalArgumentException("Level can not be null");
       }
       String dateStr = Utility.getDisplayDate(time, "yyyy-MM-dd");
       String where = BraceletInfo.DayCoaching.COLUMN_NAME_DAYCOACHING_DATE + " = "+"'"+dateStr+"'";
       Cursor cursor = query(BraceletInfo.DayCoaching.CONTENT_URI, Constants.DAYCOACHING_PROJ, where, null, null);
       
       if(cursor != null && cursor.moveToFirst()){//the date has existed
           if(level.equals("1")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               int cycle = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE1));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE1, i+cycle);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               int hit = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT1));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT1, i+hit);
           }else if(level.equals("2")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               int cycle = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE2));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE2, i+cycle);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               int hit = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT2));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT2, i+hit);
           }else if(level.equals("3")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               int cycle = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE3));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE3, i+cycle);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               int hit = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT3));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT3, i+hit);
           }else if(level.equals("4")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               int cycle = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE4));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE4, i+cycle);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               int hit = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT4));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT4, i+hit);
           }else if(level.equals("5")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               int cycle = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE5));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE5, i+cycle);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               int hit = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT5));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT5, i+hit);
           }else if(level.equalsIgnoreCase("M")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               int cycle = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLEM));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLEM, i+cycle);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               int hit = cursor.getInt(cursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HITM));
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HITM, i+hit);
           }
           update(BraceletInfo.DayCoaching.CONTENT_URI, endValues, where, null);
       }else{// put data to contentValues,and insert
           if(level.equals("1")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE1, i);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT1, i);
           }else if(level.equals("2")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE2, i);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT2, i);
           }else if(level.equals("3")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE3, i);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT3, i);
           }else if(level.equals("4")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE4, i);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT4, i);
           }else if(level.equals("5")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE5, i);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT5, i);
           }else if(level.equalsIgnoreCase("M")){
               int i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_CYCLE);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLEM, i);
               i = values.getAsInteger(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM);
               endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_HITM, i);
           }
           endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_DAYCOACHING_DATE, dateStr);
           endValues.put(BraceletInfo.DayCoaching.COLUMN_NAME_TEST_TIME, time);
           insertDayCoaching(BraceletInfo.DayCoaching.CONTENT_URI, endValues);
       }
       
       if(null != cursor){
           cursor.close();
           cursor = null;
       }
    }
    
    private Uri insertProfile(Uri uri, ContentValues initialValues){
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        // Performs the insert and returns the ID of the new note.
        long rowId =0;
        try {
            rowId = db.insert(
                    BraceletInfo.Profile.TABLE_NAME,        // The table to insert into.
                null,  // A hack, SQLite sets this column value to null
                                                 // if values is empty.
                values                           // A map of column names, and the values to insert
                                                 // into the columns.
            );
        } catch (Exception  e) {
           Log.d(TAG, "insert the same data exception");
        }

        // If the insert succeeded, the row ID exists.
        if (rowId >= 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(BraceletInfo.Profile.CONTENT_ID_URI_BASE, rowId);
            //Notifies observers registered against this provider that the data changed.
            mContentResolver.notifyChange(noteUri, null);
            return noteUri;
        }
        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);

    }

    private void doDeleteDayCoaching(String where){
        Log.d("Pei", "where=="+where);
        Cursor cursor = query(BraceletInfo.Coaching.CONTENT_URI, COACHING_PROJ, where, null, null);
        Cursor lCursor = null;
        String selection = null;
        if((null != cursor) && cursor.moveToFirst()){
            Log.d("Pei", "CURSOR.size=="+cursor.getCount());
            long time = cursor.getLong(cursor.getColumnIndex(BraceletInfo.Coaching.COLUMN_NAME_TEST_TIME));
            int cycle = cursor.getInt(cursor.getColumnIndex(BraceletInfo.Coaching.COLUMN_NAME_CYCLE));
            String level = cursor.getString(cursor.getColumnIndex(BraceletInfo.Coaching.COLUMN_NAME_LEVEL));
            int hit = cursor.getInt(cursor.getColumnIndex(BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM));
            String dateStr = Utility.getDisplayDate(time, "yyyy-MM-dd");
            selection = BraceletInfo.DayCoaching.COLUMN_NAME_DAYCOACHING_DATE + " = "+"'"+dateStr+"'";
            lCursor = query(BraceletInfo.DayCoaching.CONTENT_URI, Constants.DAYCOACHING_PROJ, selection, null, null);
            if(null != lCursor && lCursor.moveToFirst()){
                int cycle1 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE1));
                int cycle2 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE2));
                int cycle3 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE3));
                int cycle4 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE4));
                int cycle5 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE5));
                int cyclem = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLEM));
                int hit1 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT1));
                int hit2 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT2));
                int hit3 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT3));
                int hit4 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT4));
                int hit5 = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HIT5));
                int hitm = lCursor.getInt(lCursor.getColumnIndex(BraceletInfo.DayCoaching.COLUMN_NAME_HITM));
                ContentValues values = new ContentValues();
                if(level.equals("1")){
                    cycle1 -=cycle;
                    hit1 -= hit;
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE1, cycle1);
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT1, hit1);
                }else if(level.equals("2")){
                    cycle2 -= cycle;
                    hit2 -= hit;
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE2, cycle2);
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT2, hit2);
                }else if(level.equals("3")){
                    cycle3 -= cycle;
                    hit3 -= hit;
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE3, cycle3);
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT3, hit3);
                }else if(level.equals("4")){
                    cycle4 -= cycle;
                    hit4 -= hit;
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE4, cycle4);
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT4, hit4);
                }else if(level.equals("5")){
                    cycle5 -= cycle;
                    hit5 -= hit;
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE5, cycle5);
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_HIT5, hit5);
                }else if(level.equalsIgnoreCase("M")){
                    cyclem -= cycle;
                    hitm -= hit;
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_CYCLEM, cyclem);
                    values.put(BraceletInfo.DayCoaching.COLUMN_NAME_HITM, hitm);
                }
                int total = cycle1 + cycle2 + cycle3+ cycle4+ cycle5 +cyclem;
                if(total <= 0){//delete this item in the dayCoaching table
                    delete(BraceletInfo.DayCoaching.CONTENT_URI, selection, null);
                }else{//update this item in the dayCoaching table
                    update(BraceletInfo.DayCoaching.CONTENT_URI, values, selection, null);
                }
                
            }
        }
        if(null != cursor){
            cursor.close();
            cursor = null;
        }
        if(null != lCursor){
            lCursor.close();
            lCursor = null;
        }
    }
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        String finalWhere;
        int count;
        // Does the delete based on the incoming URI pattern.
        switch (sUriMatcher.match(uri)) {
            case BRACELET_PROFILE:
                count = deleteTable(db,
                    BraceletInfo.Profile.TABLE_NAME,  
                    where,                     
                    whereArgs                  
                );
                break;
            case BRACELET_PROFILE_ID:
                finalWhere = setConditionWhereClause(BraceletInfo.Profile._ID, 
                        uri, BraceletInfo.Profile.PROFILE_ID_PATH_POSITION, where);
                count = deleteTable(db, BraceletInfo.Profile.TABLE_NAME, finalWhere, whereArgs);
                break;
            case BRACELET_MEASURE:
                count = deleteTable(db,
                    BraceletInfo.Measure.TABLE_NAME,  
                    where,                     
                    whereArgs                  
                );
                break;
            case BRACELET_MEASURE_ID:
                doDeleteDayCoaching(where);
                finalWhere = setConditionWhereClause(BraceletInfo.Measure._ID, 
                        uri, BraceletInfo.Measure.MEASURE_ID_PATH_POSITION, where);
                count = deleteTable(db, BraceletInfo.Measure.TABLE_NAME, finalWhere, whereArgs);
                break;
            case BRACELET_COACHING:
                doDeleteDayCoaching(where);
                count = deleteTable(db,
                    BraceletInfo.Coaching.TABLE_NAME,  
                    where,                     
                    whereArgs                  
                );
                break;
            case BRACELET_COACHING_ID:
                finalWhere = setConditionWhereClause(BraceletInfo.Coaching._ID, 
                        uri, BraceletInfo.Coaching.COACHING_ID_PATH_POSITION, where);
                count = deleteTable(db, BraceletInfo.Coaching.TABLE_NAME, finalWhere, whereArgs);
                break;
            case BRACELET_DAYCOACHING:
                count = deleteTable(db,
                    BraceletInfo.DayCoaching.TABLE_NAME,  
                    where,                     
                    whereArgs                  
                );
                break;
            case BRACELET_DAYCOACHING_ID:
                finalWhere = setConditionWhereClause(BraceletInfo.DayCoaching._ID, 
                        uri, BraceletInfo.DayCoaching.DAYCOACHING_ID_PATH_POSITION, where);
                count = deleteTable(db, BraceletInfo.DayCoaching.TABLE_NAME, finalWhere, whereArgs);
                break;
            //If the incoming pattern is invalid, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        mContentResolver.notifyChange(uri, null);

        // Returns the number of rows deleted.
        return count;
    }
        
    private int deleteTable(SQLiteDatabase db,String tableName,String where,String[] whereArgs){
        return db.delete(tableName, where, whereArgs);
    }

    private String setConditionWhereClause(String str1,Uri uri,int position,String where){
        String finalWhere =
                str1 +                              // The ID column name
                " = " +                                          // test for equality
                uri.getPathSegments().                           // the incoming note ID
                    get(position);
                // If there were additional selection criteria, append them to the final
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
        return finalWhere;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        int count;
        String finalWhere;
        // Does the update based on the incoming URI pattern
        switch (sUriMatcher.match(uri)) {
            case BRACELET_PROFILE:
                count = updateTable(db, BraceletInfo.Profile.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BRACELET_PROFILE_ID:
                finalWhere = setConditionWhereClause(BraceletInfo.Profile._ID, uri, 
                        BraceletInfo.Profile.PROFILE_ID_PATH_POSITION, selection);
                count = updateTable(db, BraceletInfo.Profile.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BRACELET_MEASURE:
                count = updateTable(db, BraceletInfo.Measure.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BRACELET_MEASURE_ID:
                finalWhere = setConditionWhereClause(BraceletInfo.Measure._ID, uri, 
                        BraceletInfo.Measure.MEASURE_ID_PATH_POSITION, selection);
                count = updateTable(db, BraceletInfo.Measure.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BRACELET_COACHING:
                count = updateTable(db, BraceletInfo.Coaching.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BRACELET_COACHING_ID:
                finalWhere = setConditionWhereClause(BraceletInfo.Coaching._ID, uri, 
                        BraceletInfo.Coaching.COACHING_ID_PATH_POSITION, selection);
                count = updateTable(db, BraceletInfo.Coaching.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BRACELET_DAYCOACHING:
                count = updateTable(db, BraceletInfo.DayCoaching.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BRACELET_DAYCOACHING_ID:
                finalWhere = setConditionWhereClause(BraceletInfo.Profile._ID, uri, 
                        BraceletInfo.DayCoaching.DAYCOACHING_ID_PATH_POSITION, selection);
                count = updateTable(db, BraceletInfo.DayCoaching.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        mContentResolver.notifyChange(uri, null);
        //Returns the number of rows updated.
        return count;
    }

    private int updateTable(SQLiteDatabase db,String table,ContentValues values, String selection,
            String[] selectionArgs){
        return db.update(
                table, // The database table name.
                values,                   // A map of column names and new values to use.
                selection,                    // The where clause column names.
                selectionArgs                 // The where clause column values to select on.
            );
    }

}
