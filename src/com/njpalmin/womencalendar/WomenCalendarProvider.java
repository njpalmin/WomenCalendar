package com.njpalmin.womencalendar;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.njpalmin.womencalendar.WomenCalendar.Config;
import com.njpalmin.womencalendar.WomenCalendar.Product;
import com.njpalmin.womencalendar.WomenCalendar.Profile;
import com.njpalmin.womencalendar.WomenCalendar.Record;

public class WomenCalendarProvider extends ContentProvider{

	private static final String TAG = "WomenCalendarProvider";
		
	private static final int PROFILE = 1;
    private static final int RECORD = 2;
    private static final int CONFIG = 3;
    private static final int PRODUCT = 4;
    private static final int PROFILE_ID = 5;
    private static final int RECORD_ID = 6;
    private static final int PRODUCT_ID = 7;
    
    
    public static final String TABLE_PROFILE = "profile";
    public static final String TABLE_RECORD = "record";
    public static final String TABLE_CONFIG = "config";
    public static final String TABLE_PRODUCT = "product";
    
    private static final HashMap<String, String> sProfileProjectionMap;
    private static final HashMap<String, String> sRecordProjectionMap;
    private static final HashMap<String, String> sConfigProjectionMap;
    private static final HashMap<String, String> sProductProjectionMap;
    
    private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    static {
        sURLMatcher.addURI(WomenCalendar.AUTHORITY, "profile", PROFILE);
        sURLMatcher.addURI(WomenCalendar.AUTHORITY, "record", RECORD);
        sURLMatcher.addURI(WomenCalendar.AUTHORITY, "config", CONFIG);
        sURLMatcher.addURI(WomenCalendar.AUTHORITY, "product", PRODUCT);
        sURLMatcher.addURI(WomenCalendar.AUTHORITY, "profile/#", PROFILE_ID);
        sURLMatcher.addURI(WomenCalendar.AUTHORITY, "record/#", RECORD_ID);
        sURLMatcher.addURI(WomenCalendar.AUTHORITY, "product/#", PRODUCT_ID);
        
        sProfileProjectionMap = new HashMap<String,String>();
        sProfileProjectionMap.put(Profile._ID, Profile._ID);
        sProfileProjectionMap.put(Profile.NAME, Profile.NAME);
        sProfileProjectionMap.put(Profile.LASTACCESS, Profile.LASTACCESS);     
        sProfileProjectionMap.put(Profile.CYCLELENGTH, Profile.CYCLELENGTH);   
        sProfileProjectionMap.put(Profile.PERIODLENGTH, Profile.PERIODLENGTH); 
        sProfileProjectionMap.put(Profile.AUTOMATICFORECAST, Profile.AUTOMATICFORECAST);
        sProfileProjectionMap.put(Profile.PILLNOTIFICATION, Profile.PILLNOTIFICATION);
        sProfileProjectionMap.put(Profile.PERIODNOTIFICATION, Profile.PERIODNOTIFICATION);
        sProfileProjectionMap.put(Profile.PERIODNOTIFICATIONDAYSBEFORE, Profile.PERIODNOTIFICATIONDAYSBEFORE);
        sProfileProjectionMap.put(Profile.PERIODNOTIFICATIONREPEAT, Profile.PERIODNOTIFICATIONREPEAT); 
        sProfileProjectionMap.put(Profile.OVULATIONNOTIFICATIONREPEAT, Profile.OVULATIONNOTIFICATIONREPEAT);
        sProfileProjectionMap.put(Profile.OVULATIONNOTIFICATIONDAYSBEFORE, Profile.OVULATIONNOTIFICATIONDAYSBEFORE);
        sProfileProjectionMap.put(Profile.OVULATIONNOTIFICATION, Profile.OVULATIONNOTIFICATION);
        sProfileProjectionMap.put(Profile.LUTEALPHASELENGTH, Profile.LUTEALPHASELENGTH);
                                  
        sRecordProjectionMap = new HashMap<String,String>();
        sRecordProjectionMap.put(Record._ID, Record._ID);
        sRecordProjectionMap.put(Record.PROFILEPK, Record.PROFILEPK);
        sRecordProjectionMap.put(Record.DATE, Record.DATE);
        sRecordProjectionMap.put(Record.TYPE, Record.TYPE);
        sRecordProjectionMap.put(Record.FLOATVALUE, Record.FLOATVALUE);
        sRecordProjectionMap.put(Record.STRINGGVALUE, Record.STRINGGVALUE);
        sRecordProjectionMap.put(Record.INTVALUE, Record.INTVALUE);
        
        sConfigProjectionMap = new HashMap<String,String>();
        sConfigProjectionMap.put(Config.PASSWORD, Config.PASSWORD);
        sConfigProjectionMap.put(Config.LOCALE, Config.LOCALE);
        sConfigProjectionMap.put(Config.WEEKSTART, Config.WEEKSTART);
        sConfigProjectionMap.put(Config.TEMPERATURESCALE, Config.TEMPERATURESCALE);
        sConfigProjectionMap.put(Config.FIRSTLAUNCH, Config.FIRSTLAUNCH);
        sConfigProjectionMap.put(Config.EMAIL, Config.EMAIL);
        sConfigProjectionMap.put(Config.WEIGHTSCALE, Config.WEIGHTSCALE);
        sConfigProjectionMap.put(Config.SKIN, Config.SKIN);
        
        sProductProjectionMap = new HashMap<String,String>();
        sProductProjectionMap.put(Product._ID, Product._ID);
        sProductProjectionMap.put(Product.PRODUCTID, Product.PRODUCTID);
    }

	private WomenCalendarDatabaseHelper mDbHelper;
	
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		String table;
		Uri contentUri;
		
		int match = sURLMatcher.match(uri);
		switch (match){
			case PROFILE:
				table = TABLE_PROFILE;
				contentUri = Profile.CONTENT_URI;
				break;
			case RECORD:
				table = TABLE_RECORD;
				contentUri = Record.CONTENT_URI;
				break;
			case CONFIG:
				table = TABLE_CONFIG;
				contentUri = Config.CONTENT_URI;
				break;
			case PRODUCT:
				table = TABLE_PRODUCT;
				contentUri = Product.CONTENT_URI;
				break;
			default:
			    throw new IllegalArgumentException("Unknown URI " + uri);
		}
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		int conunt = db.delete(table, where, whereArgs);
		
        getContext().getContentResolver().notifyChange(uri, null);
        
        return conunt;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
		final int match = sURLMatcher.match(uri);
		String table;
		Uri contentUri;
	
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
		switch (match){
		case PROFILE:
			table = TABLE_PROFILE;
			contentUri = Profile.CONTENT_URI;
			break;
		case RECORD:
			table = TABLE_RECORD;
			contentUri = Record.CONTENT_URI;
			break;
		case CONFIG:
			table = TABLE_CONFIG;
			contentUri = Config.CONTENT_URI;
			break;
		case PRODUCT:
			table = TABLE_PRODUCT;
			contentUri = Product.CONTENT_URI;
			break;
		default:
		    throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(table, null, values);
        
        if (rowId > 0) {
            Uri appendUri = ContentUris.withAppendedId(contentUri, rowId);
            getContext().getContentResolver().notifyChange(appendUri, null);
            return appendUri;
        }		
		
        throw new SQLException("Failed to insert row into " + uri);
		//return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mDbHelper = WomenCalendarDatabaseHelper.getInstance(getContext());
		//mHelper.createDatabase();
		
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int match = sURLMatcher.match(uri);
		
		switch (match){
			case PROFILE:
				qb.setTables(TABLE_PROFILE);
				qb.setProjectionMap(sProfileProjectionMap);
				break;
			case RECORD:
				qb.setTables(TABLE_RECORD);
				qb.setProjectionMap(sRecordProjectionMap);
				break;
			case CONFIG:
				qb.setTables(TABLE_CONFIG);
				qb.setProjectionMap(sConfigProjectionMap);
				break;
			case PRODUCT:
				qb.setTables(TABLE_PRODUCT);
				qb.setProjectionMap(sProductProjectionMap);
				break;
			default:
			    throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (c != null) {
        	c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        
		return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
