package com.fihtdc.smartbracelet.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BraceletSQLiteHelper extends SQLiteOpenHelper{

    private static BraceletSQLiteHelper sSingleton = null;
    private static final String DATABASE_NAME = "bracelet.db";
    private static final int DATABASE_VERSION = 301;
    public BraceletSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public static synchronized BraceletSQLiteHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new BraceletSQLiteHelper(context);
        }
        return sSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        createProfileTable(db);
        createMeasureTable(db);
        createCoachingTable(db);
        createDayCoachingTable(db);
    }

    private void createProfileTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+BraceletInfo.Profile.TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY ," +
                BraceletInfo.Profile.COLUMN_NAME_PROFILE_NAME + " TEXT NOT NULL," +
                BraceletInfo.Profile.COLUMN_NAME_PROFILE_GENDER + " BOOLEAN NOT NULL,"+
                BraceletInfo.Profile.COLUMN_NAME_WEIGHT + " INTEGER," +
                BraceletInfo.Profile.COLUMN_NAME_WEIGHT_UNIT + " INTEGER," +
                BraceletInfo.Profile.COLUMN_NAME_HEIGHT + " INTEGER,"+
                BraceletInfo.Profile.COLUMN_NAME_HEIGHT_UNIT + " INTEGER,"+
                BraceletInfo.Profile.COLUMN_NAME_BIRTHDAY + " TEXT,"+
                BraceletInfo.Profile.COLUMN_NAME_DATA1 + " TEXT,"+
                BraceletInfo.Profile.COLUMN_NAME_DATA2 +" TEXT,"+
                BraceletInfo.Profile.COLUMN_NAME_DATA3 + " TEXT,"+
                BraceletInfo.Profile.COLUMN_NAME_DATA4 +" TEXT,"+
                BraceletInfo.Profile.COLUMN_NAME_DATA5 + " TEXT"+
                ")");
    }
    
    private void createMeasureTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+BraceletInfo.Measure.TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY ," +
                BraceletInfo.Measure.COLUMN_NAME_PROFILE_NAME + " TEXT NOT NULL," +
                BraceletInfo.Measure.COLUMN_NAME_ANS_AGE + " INTEGER,"+
                BraceletInfo.Measure.COLUMN_NAME_AGILITY + " INTEGER," +
                BraceletInfo.Measure.COLUMN_NAME_BPM + " INTEGER,"+
                BraceletInfo.Measure.COLUMN_NAME_SDNN + " INTEGER,"+
                BraceletInfo.Measure.COLUMN_NAME_AGE_HF + " FLOAT,"+
                BraceletInfo.Measure.COLUMN_NAME_AGE_LF + " FLOAT,"+
                BraceletInfo.Measure.COLUMN_NAME_EMOTION_STATUS + " INTEGER,"+
                BraceletInfo.Measure.COLUMN_NAME_PERSONALITY_TYPE + " TEXT,"+
                BraceletInfo.Measure.COLUMN_NAME_TEST_TIME + " LONG,"+
                BraceletInfo.Measure.COLUMN_NAME_DATA1 + " TEXT,"+
                BraceletInfo.Measure.COLUMN_NAME_DATA2 +" TEXT,"+
                BraceletInfo.Measure.COLUMN_NAME_DATA3 + " TEXT,"+
                BraceletInfo.Measure.COLUMN_NAME_DATA4 +" TEXT,"+
                BraceletInfo.Measure.COLUMN_NAME_DATA5 + " TEXT"+
                ")");
    }
    private void createCoachingTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+BraceletInfo.Coaching.TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY ," +
                BraceletInfo.Coaching.COLUMN_NAME_PROFILE_NAME + " TEXT NOT NULL," +
                BraceletInfo.Coaching.COLUMN_NAME_LEVEL + " TEXT NOT NULL,"+
                BraceletInfo.Coaching.COLUMN_NAME_CYCLE + " INTEGER," +
                BraceletInfo.Coaching.COLUMN_NAME_HIT_NUM + " INTEGER,"+
                BraceletInfo.Coaching.COLUMN_NAME_PERSONALITY_TYPE + " TEXT,"+
                BraceletInfo.Coaching.COLUMN_NAME_AGILITY + " INTEGER,"+
                BraceletInfo.Coaching.COLUMN_NAME_ANS_AGE + " INTEGER,"+
                BraceletInfo.Measure.COLUMN_NAME_SDNN + " INTEGER,"+
                BraceletInfo.Measure.COLUMN_NAME_AGE_HF + " FLOAT,"+
                BraceletInfo.Measure.COLUMN_NAME_AGE_LF + " FLOAT,"+
                BraceletInfo.Measure.COLUMN_NAME_EMOTION_STATUS + " INTEGER,"+
                BraceletInfo.Coaching.COLUMN_NAME_BPM + " INTEGER,"+
                BraceletInfo.Coaching.COLUMN_NAME_TEST_TIME + " LONG,"+
                
                BraceletInfo.Coaching.COLUMN_NAME_DATA1 + " TEXT,"+
                BraceletInfo.Coaching.COLUMN_NAME_DATA2 +" TEXT,"+
                BraceletInfo.Coaching.COLUMN_NAME_DATA3 + " TEXT,"+
                BraceletInfo.Coaching.COLUMN_NAME_DATA4 +" TEXT,"+
                BraceletInfo.Coaching.COLUMN_NAME_DATA5 + " TEXT"+
                ")");
    }
    private void createDayCoachingTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+BraceletInfo.DayCoaching.TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY ," +
                BraceletInfo.DayCoaching.COLUMN_NAME_DAYCOACHING_DATE + " TEXT NOT NULL," +
                BraceletInfo.DayCoaching.COLUMN_NAME_TEST_TIME + " LONG,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE1 + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_HIT1 + " INTEGER," +
                BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE2+ " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_HIT2 + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE3 + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_HIT3 + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE4 + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_HIT4 + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_CYCLE5 + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_HIT5 + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_CYCLEM + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_HITM + " INTEGER,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_DATA1 + " TEXT,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_DATA2 +" TEXT,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_DATA3 + " TEXT,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_DATA4 +" TEXT,"+
                BraceletInfo.DayCoaching.COLUMN_NAME_DATA5 + " TEXT"+
                ")");
    }
    //TODO
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }
}
