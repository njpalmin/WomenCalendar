package com.njpalmin.womencalendar;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;

import com.njpalmin.womencalendar.WomenCalendar.Record;

public class DayInfoExt {
	
	private Time mTime;
	private int mDay;
	private int mDate;
	private int mDayType;
	private float mFloatValue;
	private String mStringValue;
	private int mIntValue;
	private DayOfMonthCursor mCursor;
	private Context mContext;
	private ContentResolver mContentResolver;
	
    private static final String[] RECORD_PROJECTION = new String[] {
        Record._ID, 
        Record.DATE,
        Record.TYPE,
        Record.FLOATVALUE,
        Record.STRINGGVALUE,
        Record.INTVALUE,        
    };
    
	public DayInfoExt(Context context,int row, int column, Time time){
		mContext = context;
		mContentResolver = context.getContentResolver();
		
		mCursor = new DayOfMonthCursor(time.year,  time.month, time.monthDay, Calendar.getInstance().getFirstDayOfWeek());
		mTime = time;
		mDate = Integer.parseInt(mTime.format("%Y%m%d"));
		
		mDay = mCursor.getDayAt(row,column);
		//mDate.set(cursor.getDayAt(row,column),cursor.getMonth(),cursor.getYear());
		//mDate = new Time (cursor.getDayAt(row,column);
		
		
	}
	
	private void getDayType(){
		
	}
	/*
	private boolean isStartDay(){
		//String selection =;
		Cursor c = mContentResolver.query(Record.CONTENT_URI,RECORD_PROJECTION,null,null,null); 
	}*/
		

	
}
