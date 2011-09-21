package com.njpalmin.womencalendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;

import com.njpalmin.womencalendar.WomenCalendar.Profile;

public class DayOfPeriodCursor {
	private Context mContext;
	private ContentResolver mContentResolver;
	private Cursor mCursor;
	private int mCycleLength;
	private int mPeriodLength;
	//private int mStartDay;
	
    public DayOfPeriodCursor(Context context, Time Date) {
    	mContext = context;
    	mContentResolver = context.getContentResolver();
		if(mCursor != null && mCursor.getCount() != 0){
			mCursor.moveToFirst();
			mCycleLength = mCursor.getInt(mCursor.getColumnIndex(Profile.CYCLELENGTH));
			mPeriodLength = mCursor.getInt(mCursor.getColumnIndex(Profile.PERIODLENGTH));
		}
    }


}
