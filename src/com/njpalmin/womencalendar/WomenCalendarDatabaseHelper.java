package com.njpalmin.womencalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.njpalmin.womencalendar.WomenCalendar.Config;
import com.njpalmin.womencalendar.WomenCalendar.Product;
import com.njpalmin.womencalendar.WomenCalendar.Profile;
import com.njpalmin.womencalendar.WomenCalendar.Record;

public class WomenCalendarDatabaseHelper  extends SQLiteOpenHelper{
	
	private static final String TAG="WomenCalendarDatabaseHelper";
    private static final String DATABASE_NAME = "womencalender.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CONFIG_TABLE ="config";
    private static final String DATABASE_PRODUCT_TABLE ="product";
    private static final String DATABASE_PROFILE_TABLE ="profile";
    private static final String DATABASE_RECORD_TABLE ="record";

    private static final String CREATE_CONFIG_TABLE ="CREATE TABLE "+ DATABASE_CONFIG_TABLE+ "("+
    												  Config.PASSWORD + " TEXT, "+ 
    												  Config.LOCALE +" TEXT," +
    												  Config.WEEKSTART +" INTEGER, " + 
    												  Config.TEMPERATURESCALE + " TEXT," + 
    												  Config.FIRSTLAUNCH + " INTEGER," + 
    												  Config.EMAIL + " TEXT," + 
    												  Config.WEIGHTSCALE + " TEXT," + 
    												  Config.SKIN + " INTEGER NOT NULL DEFAULT 0);";

    private static final String CREATE_PROFILE_TABLE ="CREATE TABLE "+ DATABASE_PROFILE_TABLE + "(" +
    												   Profile._ID + " INTEGER PRIMARY KEY NOT NULL," + 
    												   Profile.NAME +" TEXT," + 
    												   Profile.LASTACCESS +" INTEGER NOT NULL," + 
    												   Profile.CYCLELENGTH + " INTEGER NOT NULL," + 
    												   Profile.PERIODLENGTH + " INTEGER NOT NULL," + 
    												   Profile.AUTOMATICFORECAST + " INTEGER NOT NULL," +
    												   Profile.PILLNOTIFICATION + " INTEGER NOT NULL," +  
    												   Profile.PERIODNOTIFICATION + " INTEGER NOT NULL," + 
    												   Profile.PERIODNOTIFICATIONDAYSBEFORE + " INTEGER NOT NULL," +
    												   Profile.PERIODNOTIFICATIONREPEAT +  " INTEGER NOT NULL," + 
    												   Profile.OVULATIONNOTIFICATIONREPEAT + " INTEGER NOT NULL," + 
    												   Profile.OVULATIONNOTIFICATIONDAYSBEFORE + " INTEGER NOT NULL," + 
    												   Profile.OVULATIONNOTIFICATION + " INTEGER NOT NULL," + 
    												   Profile.LUTEALPHASELENGTH + " INTEGER NOT NULL);";
    												   
    private static final String CREATE_PRODUCT_TABLE = "CREATE TABLE "+ DATABASE_PRODUCT_TABLE + "(" + 
    													Product._ID + " INTEGER PRIMARY KEY NOT NULL," + 
    													Product.PRODUCTID + " TEXT NOT NULL);";
    													
    private static final String CREATE_RECORD_TABLE = "CREATE TABLE "+ DATABASE_RECORD_TABLE + "(" +
    													Record._ID + " INTEGER PRIMARY KEY NOT NULL," + 
    													Record.PROFILEPK + " INTEGER NOT NULL," + 
    													Record.DATE + " INTEGER NOT NULL," + 
    													Record.TYPE + " TEXT NOT NULL," + 
    													Record.FLOATVALUE + " REAL," +
    													Record.STRINGVALUE + " TEXT," + 
    													Record.INTVALUE + " INTEGER);"; 

    private final Context mContext;    												   
    private static WomenCalendarDatabaseHelper sSingleton = null;       
    
    
    public static synchronized WomenCalendarDatabaseHelper getInstance(Context context) {
    	if (sSingleton == null) {
    		sSingleton = new WomenCalendarDatabaseHelper(context);
    	}
        return sSingleton;
    }

    public WomenCalendarDatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	mContext = context;
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
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS profile, record, config, product");
        onCreate(db);
	}
}       
