package com.njpalmin.womencalendar;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.njpalmin.womencalendar.WomenCalendar.Profile;
import com.njpalmin.womencalendar.WomenCalendar.Record;

public class CalendarView extends View {
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
        Record.STRINGGVALUE,
        Record.INTVALUE,        
    };
    
	public CalendarView(Context context,Time time,LinearLayout parentLayout) {	
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
		//getStartDays();
		
		initView(mTime);

	}

	private void initView(Time time){
		mCursor = new DayOfMonthCursor(time.year,  time.month, time.monthDay, Calendar.getInstance().getFirstDayOfWeek());
		getStartDays();
		
		for(int i=0;i<WEEKS;i++){
			for(int j=0;j<WEEKDAYS;j++){
				DayInfoView[] weekDays = new DayInfoView[WEEKDAYS];
				//int monthDay = mCursor.getDayAt(i,j);
				//boolean withinCurrentMonth = mCursor.isWithinCurrentMonth(i, j);
				int dayType = getDayType(i,j);
				weekDays[j] = new DayInfoView(mContext,i,j,mCursor,dayType);
				LinearLayout.LayoutParams llp  = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT,1);
				llp.setMargins(2,2,2,2);
				weekDays[j].setLayoutParams(llp);
				weekDays[j].setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	Intent intent = new Intent(getContext(), WomenCalendarDayActivity.class);
		            	intent.putExtra(Utils.EVENT_BEGIN_TIME, ((DayInfoView)v).getMillis());
		            	intent.putExtra(Utils.EVENT_ROW, ((DayInfoView)v).getRow());
		            	intent.putExtra(Utils.EVENT_COLUMN, ((DayInfoView)v).getColumn());
		            	
		            	mParentActivity.startActivityForResult(intent,WomenCalendarActivity.DAY_ACTIVITY_DETAILS);
		                //startActivityForResult(intent,DAY_ACTIVITY_DETAILS);
		            }
		        });
				mWeekLayout[i].addView(weekDays[j]);
			}
			mParentLayout.addView(mWeekLayout[i]);
			//addView(mWeekLayout[i]);
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
	
	private void getStartDays(){
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
		
		return Utils.DAY_TYPE_NORMAL;
	}
	
    private void getCycleAndPeriodLength(){
    	Cursor c = mContentResolver.query(Profile.CONTENT_URI,null,null,null,null);
    	if(c != null){
    		c.moveToFirst();
    		mCycleLength = c.getInt(c.getColumnIndex(Profile.CYCLELENGTH));
    		mPeriodLength = c.getInt(c.getColumnIndex(Profile.PERIODLENGTH));
    	}
    	c.close();
    }
    
    private void checkRecordType(int row, int column){
    	
    	int day = mCursor.getDayAt(row,column);
    	
    //	Time time = new 
    	String selection = Record.TYPE + "!=?";
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,RECORD_PROJECTION,selection,
    			                          new String[]{Utils.RECORD_TYPE_START},Record.DEFAULT_SORT_ORDER);
    	
    	if(c != null){
    		c.moveToFirst();
    	}
    }
}
