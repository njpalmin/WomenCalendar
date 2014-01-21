package com.womencalendar.view;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.womencalendar.DayInfo;
import com.womencalendar.WomenCalendar.Record;
import com.womencalendar.WomenCalendarActivity;
import com.womencalendar.WomenCalendarDayActivity;
import com.womencalendar.utils.DayOfMonthCursor;
import com.womencalendar.utils.Utils;

public class WomenCalendarView extends View {
	private final static String TAG="CalendarView";
	private static int WEEKS= 6;
	private static int WEEKDAYS = 7;
	//private static int HORIZONTAL_FLING_THRESHOLD = 50;

	private LinearLayout[] mWeekLayout;
	private Context mContext;
	private LinearLayout mParentLayout;
	private Time mTime;
	private DayOfMonthCursor mCursor;
	private WomenCalendarActivity mParentActivity;
	private ContentResolver mContentResolver;
	private ArrayList<DayInfo> mStartDays;
	private int mPeriodLength;
	private int mCycleLength;
	
    private static final String[] RECORD_PROJECTION = new String[] {
        Record._ID, 
        Record.DATE,
        Record.TYPE,
        Record.FLOATVALUE,
        Record.STRINGVALUE,
        Record.INTVALUE,        
    };
    
	public WomenCalendarView(Context context,Time time,LinearLayout parentLayout) {	
		super(context);
		mContext = context;
		mParentActivity = (WomenCalendarActivity)context;
		// TODO Auto-generated constructor stub
		mWeekLayout = new LinearLayout[WEEKS];
		
		mParentLayout = parentLayout;
		mTime = time;
		
		mContentResolver = context.getContentResolver();
		mStartDays = new ArrayList<DayInfo>();
		
		for(int i=0;i<WEEKS;i++){
			mWeekLayout[i] = new LinearLayout(mContext);
			mWeekLayout[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,1));
			mWeekLayout[i].setOrientation(0);
		}
		getCycleAndPeriodLength();
		initView(mTime);
	}

	private void initView(Time time){
		mCursor = new DayOfMonthCursor(time.year,  time.month, time.monthDay, Utils.getStartDayOfWeek(mContext));
//		getStartDays();
		for(int i=0;i<WEEKS;i++){
			for(int j=0;j<WEEKDAYS;j++){
//				DayInfoView[] weekDays = new DayInfoView[WEEKDAYS];
				DayView[] weekDays = new DayView[WEEKDAYS];
				int dayType = getDayType(i,j);
//				int notification = getNotification(i,j);
//				weekDays[j] = new DayInfoView(mContext,i,j,mCursor,dayType,notification);
				weekDays[j] = new DayView(mContext,i,j,mCursor,Utils.getStartDayOfWeek(mContext));
				LinearLayout.LayoutParams llp  = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT,1);
				llp.setMargins(2,2,0,0);
				weekDays[j].setLayoutParams(llp);
				weekDays[j].setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	Intent intent = new Intent(getContext(), WomenCalendarDayActivity.class);
		            	intent.putExtra(Utils.EXTRAS_SELECTED_DAY, ((DayView)v).getDate());
//		            	intent.putExtra(Utils.EVENT_DAY_TYPE, ((DayView)v).getDayType());
//		            	intent.putExtra(Utils.EVENT_NOTIFICATION, ((DayView)v).getNotification());
		            	mParentActivity.startActivityForResult(intent,WomenCalendarActivity.DAY_ACTIVITY_DETAILS);
		            }
		        });
				mWeekLayout[i].addView(weekDays[j]);
			}
			mParentLayout.addView(mWeekLayout[i]);
		}
	}
	
	public void reDrawView(Time time){
		for(int i=0;i<WEEKS;i++){
			mWeekLayout[i].removeAllViews();
			mParentLayout.removeView(mWeekLayout[i]);
		}
		
		initView(time);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		//mCanvas = canvas;
	}
	
	
	private void getStartDaysWithinMonth(){
		int startDay = mCursor.getDayAt(0,0);
		int endDay = mCursor.getDayAt(6,7);
		mStartDays.clear();
		
		Time startTime = new Time();
		startTime.set(startDay, mCursor.getMonth(),mCursor.getYear());
		startTime.monthDay = 1- mCursor.getOffset();
		startTime.normalize(true);
		
		Time endTime = new Time();
		endTime.set(endDay, mCursor.getMonth(),mCursor.getYear());
		endTime.monthDay = 7 * 6 - mCursor.getOffset();
		endTime.normalize(true);
			
		String selection = startTime.toMillis(true)/1000 + "<" + Record.DATE + 
						  " AND " +  Record.DATE + "<" + endTime.toMillis(true)/1000 +
						  " AND " + Record.TYPE + "=?";
		
		Log.d(TAG,"Selection = "+ selection);
		Cursor c = mContentResolver.query(Record.CONTENT_URI,RECORD_PROJECTION,selection,new String[]{Utils.RECORD_TYPE_START},Record.DEFAULT_SORT_ORDER);
		
		if(c != null){
			c.moveToFirst();
			while(!c.isAfterLast()){
				long date = c.getLong(c.getColumnIndex(Record.DATE));
				long intValue = c.getLong(c.getColumnIndex(Record.INTVALUE));
				
				DayInfo dayInfo = new DayInfo(date,Utils.RECORD_TYPE_START,intValue,"",0.0f);
				mStartDays.add(dayInfo);
				c.moveToNext();
			}
			c.close();
		}
		
	}
	
	private int getDayType(int row, int column){
		int day = mCursor.getDayAt(row,column);
		Time time = new Time();
		time.set(day,mCursor.getMonth(),mCursor.getYear());
		time.monthDay = 7 * row + column - mCursor.getOffset() + 1;
		time.normalize(true);
//		String date = time.format("%Y%M%D");
		
		int startDay = mCursor.getDayAt(0,0);
		int endDay = mCursor.getDayAt(6,7);
		Time startTime = new Time();
		startTime.set(startDay, mCursor.getMonth(),mCursor.getYear());
        startTime.monthDay = 1- mCursor.getOffset();
        startTime.normalize(true);
	        
        Time endTime = new Time();
        endTime.set(endDay, mCursor.getMonth(),mCursor.getYear());
        endTime.monthDay = 7 * 6 - mCursor.getOffset();
        endTime.normalize(true);
		
		Cursor c = mContentResolver.query(Record.CONTENT_URI, RECORD_PROJECTION, 
		           "date>? and date<? and type=?",
		           new String[]{startTime.format("%Y%M%D"),endTime.format("%Y%M%D"),Utils.RECORD_TYPE_START},null);
		
		if(c != null && c.getCount() != 0 ){
		    Log.d(TAG,"date = "+ c.getInt(c.getColumnIndex("date")));
		    return Utils.DAY_TYPE_START;
		}
		c.close();
		/*
		long millis = time.toMillis(true)/1000;
		Log.d(TAG,"Day row="+ row+"|| column="+column+" || millis="+millis);
		
		for(int i=0;i<mStartDays.size();i++){
			DayInfo dayInfo = mStartDays.get(i);
			Log.d(TAG,"dayInfo="+dayInfo.mDate);
			
			if (millis == dayInfo.mDate){
				return Utils.DAY_TYPE_START;
			}else if (millis - dayInfo.mDate < (mPeriodLength -1) * Utils.DAY_IN_SECONDS && millis - dayInfo.mDate > 0){
				return Utils.DAY_TYPE_MIDDLE;
			}else if (millis - dayInfo.mDate == (mPeriodLength-1) * Utils.DAY_IN_SECONDS){
				return Utils.DAY_TYPE_END;
			}else if (millis - dayInfo.mDate > (mCycleLength - 18)* Utils.DAY_IN_SECONDS 
					  &&  millis - dayInfo.mDate < (mCycleLength - 10)* Utils.DAY_IN_SECONDS){
				if(millis - dayInfo.mDate == (mCycleLength - 14)* Utils.DAY_IN_SECONDS ){
					return Utils.DAY_TYPE_OVULATION;
				}else{
					return Utils.DAY_TYPE_FERTILITY;
				}
			}
		}
		*/
		return Utils.DAY_TYPE_NORMAL;
	}
	
    private void getCycleAndPeriodLength(){
//    	Cursor c = mContentResolver.query(Profile.CONTENT_URI,null,null,null,null);
//    	if(c != null){
//    		c.moveToFirst();
//    		mCycleLength = c.getInt(c.getColumnIndex(Profile.CYCLELENGTH));
//    		mPeriodLength = c.getInt(c.getColumnIndex(Profile.PERIODLENGTH));
//    	}
//    	c.close();
        SharedPreferences prefs = mContext.getSharedPreferences(null, Context.MODE_PRIVATE);
        mCycleLength = prefs.getInt(Utils.SHARED_PREF_CYCLE_LENGTH, 28);
        mPeriodLength = prefs.getInt(Utils.SHARED_PREF_PERIOD_LENGTH, 4);
    }
    
    private int getNotification(int row, int column){
    	int notificationType = 0;
    	int day = mCursor.getDayAt(row,column);
    	
    	Time time = new Time();
    	time.set(day,mCursor.getMonth(),mCursor.getYear());
		time.monthDay = 7 * row + column - mCursor.getOffset() + 1;
		time.normalize(true);
		
		
    	String selection = Record.TYPE + "!=?" + " AND " + Record.DATE + "=" + String.valueOf(time.toMillis(true)/1000);
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,RECORD_PROJECTION,selection,
    			                          new String[]{Utils.RECORD_TYPE_START},Record.DEFAULT_SORT_ORDER);
    	
    	if(c != null){
    		c.moveToFirst();
    		while(!c.isAfterLast()){
    			String type = c.getString(c.getColumnIndex(Record.TYPE));
    			if(type.equals(Utils.RECORD_TYPE_PILL)){
    				notificationType |= Utils.NOTIFICATION_TYPE_PILL;
    			}else if (type.equals(Utils.RECORD_TYPE_SEX)){
    				notificationType |= Utils.NOTIFICATION_TYPE_SEX;
    			}else if (type.equals(Utils.RECORD_TYPE_WEIGHT)){
    				notificationType |= Utils.NOTIFICATION_TYPE_WEIGHT;
    			}else if (type.equals(Utils.RECORD_TYPE_BMT)){
    				notificationType |= Utils.NOTIFICATION_TYPE_BMT;
    			}else if (type.equals(Utils.RECORD_TYPE_NOTE)){
    				notificationType |= Utils.NOTIFICATION_TYPE_NOTE;
    			}
    			c.moveToNext();
    		}
    		c.close();
    	}
    	
    	return notificationType;
    }
}
