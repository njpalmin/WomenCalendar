package com.njpalmin.womencalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WomenCalendarDbAdapter  {
    private static final String DATABASE_NAME = "womencalender.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CONFIG_TABLE ="config";
    private static final String DATABASE_PRODUCT_TABLE ="product";
    private static final String DATABASE_PROFILE_TABLE ="profile";
    private static final String DATABASE_RECORD_TABLE ="record";
	
    public static final String KEY_ROWID = "_id";
    //for config table
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_LOCALE = "locale";
    public static final String KEY_WEEKSTART = "weekstart";
    public static final String KEY_TEMPERATURESCALE = "temperaturescale";
    public static final String KEY_FIRSTLAUNCH = "firstlaunch";
    public static final String KEY_EMAIL= "email";
    public static final String KEY_WEIGHTSCALE = "weightscale";
    public static final String KEY_SKIN = "skin";
    
    //for product
    public static final String KEY_PRODUCTID = "productid";
    
    //for profile
    public static final String KEY_PK = "pk";
    public static final String KEY_NAME = "name";
    public static final String KEY_LASTACCESS = "lastaccess";
    public static final String KEY_CYCLELENGTH = "cyclelength";
    public static final String KEY_PERIODLENGTH= "periodlength";
    public static final String KEY_AUTOMATICFORECAST = "automaticforecast";
    public static final String KEY_PILLNOTIFICATION = "pillnotification";
    
    public static final String KEY_PERIODNOTIFICATION = "periodnotification";
    public static final String KEY_PERIODNOTIFICATIONDAYSBEFORE = "periodnotificationdaysbefore";
    public static final String KEY_PERIODNOTIFICATIONREPEAT = "periodnotificationrepeat";
    
    public static final String KEY_OVULATIONNOTIFICATIONREPEAT = "ovulationotification";
    public static final String KEY_OVULATIONNOTIFICATIONDAYSBEFORE = "ovulationnotificationdaysbefore";
    public static final String KEY_OVULATIONNOTIFICATION = "ovulationnotification";
    public static final String KEY_LUTEALPHASELENGTH = "lutealphaselength";
    
    //for record
    //public static final String KEY_PK = "pk";
    public static final String KEY_PROFILEPK = "profilepk";
    public static final String KEY_DATE = "date";
    public static final String KEY_TYPE = "type";
    public static final String KEY_FLOATVALUE = "floatvalue";
    public static final String KEY_STRINGVALUE = "stringvale";
    public static final String KEY_INTVALUE = "intvale";
    
    private static final String CREATE_CONFIG_TABLE ="CREATE TABLE "+ DATABASE_CONFIG_TABLE+ "("+
    												  KEY_PASSWORD + " TEXT, "+ 
    												  KEY_LOCALE +" TEXT," +
    												  KEY_WEEKSTART +" INTEGER, " + 
    												  KEY_TEMPERATURESCALE + " TEXT," + 
    												  KEY_FIRSTLAUNCH + " INTEGER," + 
    												  KEY_EMAIL + " TEXT," + 
    												  KEY_WEIGHTSCALE + " TEXT," + 
    												  KEY_SKIN + " INTEGER NOT NULL DEFAULT 0);";

    private static final String CREATE_PROFILE_TABLE ="CREATE TABLE "+ DATABASE_PROFILE_TABLE + "(" +
    												   KEY_PK + " INTEGER PRIMARY KEY NOT NULL," + 
    												   KEY_NAME +" TEXT," + 
    												   KEY_LASTACCESS +" INTEGER NOT NULL," + 
    												   KEY_CYCLELENGTH + " INTEGER NOT NULL," + 
    												   KEY_PERIODLENGTH + " INTEGER NOT NULL," + 
    												   KEY_AUTOMATICFORECAST + " INTEGER NOT NULL," +
    												   KEY_PILLNOTIFICATION + " INTEGER NOT NULL," +  
    												   KEY_PERIODNOTIFICATION + " INTEGER NOT NULL," + 
    												   KEY_PERIODNOTIFICATIONDAYSBEFORE + " INTEGER NOT NULL," +
    												   KEY_PERIODNOTIFICATIONREPEAT +  " INTEGER NOT NULL," + 
    												   KEY_OVULATIONNOTIFICATIONREPEAT + " INTEGER NOT NULL," + 
    												   KEY_OVULATIONNOTIFICATIONDAYSBEFORE + " INTEGER NOT NULL," + 
    												   KEY_OVULATIONNOTIFICATION + " INTEGER NOT NULL," + 
    												   KEY_LUTEALPHASELENGTH + " INTEGER NOT NULL);";
    												   
    private static final String CREATE_PRODUCT_TABLE = "CREATE TABLE "+ DATABASE_PRODUCT_TABLE + "(" + 
    													KEY_PK + " INTEGER PRIMARY KEY NOT NULL," + 
    													KEY_PRODUCTID + " TEXT NOT NULL);";
    													
    private static final String CREATE_RECORD_TABLE = "CREATE TABLE "+ DATABASE_RECORD_TABLE + "(" +
    													KEY_PK + " INTEGER PRIMARY KEY NOT NULL," + 
    													KEY_PROFILEPK + " INTEGER NOT NULL," + 
    													KEY_DATE + " INTEGER NOT NULL," + 
    													KEY_TYPE + " TEXT NOT NULL," + 
    													KEY_FLOATVALUE + " REAL," +
    													KEY_STRINGVALUE + " TEXT," + 
    													KEY_INTVALUE + " INTEGER);"; 
    
    public static final String[] PROFILE_PROJECTION = new String[] {
    	KEY_PK,
        KEY_NAME,
        KEY_LASTACCESS,
        KEY_CYCLELENGTH,
        KEY_PERIODLENGTH,
        KEY_AUTOMATICFORECAST,
        KEY_PILLNOTIFICATION,
        KEY_PERIODNOTIFICATION,
        KEY_PERIODNOTIFICATIONDAYSBEFORE,
        KEY_PERIODNOTIFICATIONREPEAT,                                             
        KEY_OVULATIONNOTIFICATIONREPEAT,
        KEY_OVULATIONNOTIFICATIONDAYSBEFORE,
        KEY_OVULATIONNOTIFICATION,
        KEY_LUTEALPHASELENGTH
    };  
    								
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mContext;    												   
        
        
    private static class DatabaseHelper extends SQLiteOpenHelper {
        
    	DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
        
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATE_CONFIG_TABLE);
			db.execSQL(CREATE_PROFILE_TABLE);
			db.execSQL(CREATE_PRODUCT_TABLE);
			db.execSQL(CREATE_RECORD_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		 
	}
    
    public WomenCalendarDbAdapter(Context context){
    	mContext = context;
    }
    
    public WomenCalendarDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    
    public long createProfile(long time,int cycleLength, int periodLength, int lutealPhaseLength) {
        ContentValues initialValues = new ContentValues();
        
        initialValues.put(KEY_LASTACCESS, time);
        initialValues.put(KEY_CYCLELENGTH, cycleLength);
        initialValues.put(KEY_PERIODLENGTH, periodLength);
        initialValues.put(KEY_LUTEALPHASELENGTH, lutealPhaseLength);
        initialValues.put(KEY_AUTOMATICFORECAST, 0);
        initialValues.put(KEY_PILLNOTIFICATION, 0);
        initialValues.put(KEY_PERIODNOTIFICATION, 0);
        initialValues.put(KEY_PERIODNOTIFICATIONDAYSBEFORE, 0);
        initialValues.put(KEY_PERIODNOTIFICATIONREPEAT, 0);
        initialValues.put(KEY_OVULATIONNOTIFICATIONREPEAT, 0);
        initialValues.put(KEY_OVULATIONNOTIFICATIONDAYSBEFORE, 0); 
        initialValues.put(KEY_OVULATIONNOTIFICATION, 0);
        
        
        return mDb.insert(DATABASE_PROFILE_TABLE, null, initialValues);
    }   
        
    public Cursor getProfile(){
        return mDb.query(DATABASE_PROFILE_TABLE, PROFILE_PROJECTION, null, null, null, null, null);
    }   
}       
