package com.njpalmin.womencalendar;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.njpalmin.womencalendar.WomenCalendar.Profile;
import com.njpalmin.womencalendar.WomenCalendar.Record;

public class DayInfoView extends RelativeLayout {
	private final static String TAG="DayInfoView";
	
	private final static int RECORD_TYPE_START = 1<<0;
	private final static int RECORD_TYPE_PILL = 1<<1;
	private final static int RECORD_TYPE_BMT = 1<<2;
	private final static int RECORD_TYPE_NOTE = 1<<3;
	private final static int RECORD_TYPE_SEX = 1<<4;
	private final static int RECORD_TYPE_WEIGHT = 1<<5;
	
	private Time mToday;
	private Time mTime;
	private long mMillis;
	private RelativeLayout mDayInfoLayout;
	private TextView mDateTV;
	private Context mContext;
	private Canvas mCanvas;
	private LinearLayout mParent;
	private DayOfMonthCursor mCursor;
	private int mDay;
	private int mDate;
	private boolean mWithinCurrentMonth;
	private boolean mIsToday;
	private ContentResolver mContentResolver;
	private int mRow = -1;
	private int mColumn = -1;
	private int mRecordType = 0;
	private String [] mRecordTypes;
	private int mDayType;
	private int mNotification;
	
    private static final String[] RECORD_PROJECTION = new String[] {
        Record._ID, 
        Record.DATE,
        Record.TYPE,
        Record.FLOATVALUE,
        Record.STRINGVALUE,
        Record.INTVALUE,        
    };
	
	public DayInfoView(Context context, int row, int column, DayOfMonthCursor cursor, int dayType, int notification) {
		super(context);
		
		mRow = row;
		mColumn = column;
		mDayType = dayType;
		mNotification = notification;
		
		long now = System.currentTimeMillis();
		mToday = new Time();
		mToday.set(now);
		mToday.normalize(true);
		// TODO Auto-generated constructor stub
		mContext = context;
		if(cursor == null){
			mCursor = new DayOfMonthCursor(mToday.year,  mToday.month,mToday.monthDay, Calendar.getInstance().getFirstDayOfWeek());;
		}else{
			mCursor = cursor;
		}
		
		
		
		mDay = mCursor.getDayAt(row,column);
		
		mTime = new Time();
		mTime.set(mDay,cursor.getMonth(),cursor.getYear());
		mTime.monthDay = 7 * row + column - mCursor.getOffset() + 1;
		mTime.normalize(true);
		
		mMillis = mTime.toMillis(true);
		
		mWithinCurrentMonth = mCursor.isWithinCurrentMonth(row, column);

		if (mDay == mToday.monthDay && mCursor.getYear() == mToday.year
                && mCursor.getMonth() == mToday.month) {
			mIsToday = true;
        }
		
		mContentResolver = mContext.getContentResolver();
		mDate = Integer.parseInt(mTime.format("%Y%m%d"));
		
		initView();
	}
	
	private void initView(){
		mDayInfoLayout = new  RelativeLayout(mContext);
		mDayInfoLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
																	   RelativeLayout.LayoutParams.MATCH_PARENT));
		
		if(mWithinCurrentMonth){
			mDayInfoLayout.setBackgroundResource(R.drawable.calendar_day_standard);
		}else{
			mDayInfoLayout.setBackgroundResource(R.drawable.calendar_day_other_standard);
		}
		
		switch (mDayType){
			case Utils.DAY_TYPE_START:
				mDayInfoLayout.setBackgroundResource(R.drawable.calendar_day_start_period_standard);
				break;
			case Utils.DAY_TYPE_MIDDLE:
				mDayInfoLayout.setBackgroundResource(R.drawable.calendar_day_middle_period_standard);
				break;
			case Utils.DAY_TYPE_END:
				mDayInfoLayout.setBackgroundResource(R.drawable.calendar_day_end_period_standard);
				break;
			
			case Utils.DAY_TYPE_FERTILITY:
				ImageView fertilityDay = new ImageView(mContext);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
																					 RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				fertilityDay.setLayoutParams(params);
				fertilityDay.setBackgroundResource(R.drawable.calendar_fertility);
				mDayInfoLayout.addView(fertilityDay);
				break;
				
			case Utils.DAY_TYPE_OVULATION:
				ImageView ovulationDay = new ImageView(mContext);
				RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
						 RelativeLayout.LayoutParams.WRAP_CONTENT);
				params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
				params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				ovulationDay.setLayoutParams(params1);
				ovulationDay.setBackgroundResource(R.drawable.calendar_ovulation);
				mDayInfoLayout.addView(ovulationDay);
				break;
			case Utils.DAY_TYPE_NORMAL:
				default:
					break;
		}
		
		if((mNotification & Utils.NOTIFICATION_TYPE_PILL) != 0){
			ImageView pill = new ImageView(mContext);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					 RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			pill.setLayoutParams(params);
			pill.setBackgroundResource(R.drawable.calendar_pill);
			mDayInfoLayout.addView(pill);
		}
		
		if((mNotification & Utils.NOTIFICATION_TYPE_SEX) != 0){
			ImageView sex = new ImageView(mContext);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					 RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			sex.setLayoutParams(params);
			sex.setBackgroundResource(R.drawable.calendar_sex);
			mDayInfoLayout.addView(sex);
		}		
		
		if((mNotification & Utils.NOTIFICATION_TYPE_NOTE) != 0){
			ImageView note = new ImageView(mContext);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					 RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			note.setLayoutParams(params);
			note.setBackgroundResource(R.drawable.calendar_note);
			mDayInfoLayout.addView(note);
		}
		
		if((mNotification & Utils.NOTIFICATION_TYPE_BMT) != 0){
			TextView bmt = new TextView(mContext);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					 RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			bmt.setLayoutParams(params);
			bmt.setText(String.valueOf(getBmtValue()));
			bmt.setTextSize(6);
			bmt.setTextColor(Color.GRAY);
			mDayInfoLayout.addView(bmt);
		}
		
		if((mNotification & Utils.NOTIFICATION_TYPE_WEIGHT) != 0){
			TextView weight = new TextView(mContext);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					 RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			weight.setLayoutParams(params);
			weight.setText(String.valueOf(getWeightValue()));
			weight.setTextSize(6);
			weight.setTextColor(Color.GRAY);
			mDayInfoLayout.addView(weight);
		}
		
		
		mDateTV  = new TextView(mContext);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
		        								RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		mDateTV.setLayoutParams(params);
		mDateTV.setTextSize(14);
		if(mWithinCurrentMonth){
			mDateTV.setTextColor(Color.BLACK);
		}else{
			mDateTV.setTextColor(Color.GRAY);
		}
		if(mIsToday){
			mDateTV.setTypeface(Typeface.DEFAULT_BOLD);
		}
		//mDate.setTextColor(Color.BLACK);
		mDateTV.setText(String.valueOf(mDay));
		mDayInfoLayout.addView(mDateTV);
		addView(mDayInfoLayout);
		
	}
	
    private boolean  hasRecord(int date){
    	boolean hasRecord = false;
    	String selection = Record.DATE + "='" + String.valueOf(date) + "'";
    	
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,RECORD_PROJECTION,selection,null, Record.DEFAULT_SORT_ORDER);
    	
    	if(c != null && c.getCount() != 0){
			c.moveToFirst();
			int size = c.getCount();
			
			mRecordTypes = new String[size];
			
			for(int i=0;i<size;i++){
				mRecordTypes[i] = c.getString(c.getColumnIndex(Record.TYPE));
			}
			hasRecord = true;
    	}
    	
    	return hasRecord;	
    }
    
    private void initDay(){
    	if(hasRecord(mDay)){
    		for(int i=0; i< mRecordTypes.length;i++){
				if(mRecordTypes[i].equals(Utils.RECORD_TYPE_PILL)){
					mRecordType |= RECORD_TYPE_PILL;
				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_SEX)){
					mRecordType |= RECORD_TYPE_SEX;
				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_BMT)){
					mRecordType |= RECORD_TYPE_BMT;
				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_WEIGHT)){
					mRecordType |= RECORD_TYPE_WEIGHT;
				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_NOTE)){
					mRecordType |= RECORD_TYPE_NOTE;
				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_START)){
					mRecordType |= RECORD_TYPE_START;
					//mDayType = Utils.DAY_TYPE_FORECAST_DAY;
				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_END)){
					mRecordType &= RECORD_TYPE_START;
				}
			}
    	}
    }
    
    private float getWeightValue(){
    	float weightValue = 0.0f;
    	String selection = Record.TYPE + "=" + Utils.RECORD_TYPE_WEIGHT + " AND " + Record.FLOATVALUE + "=?";
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,null,selection,new String[]{String.valueOf(mMillis)},null);
    	if(c != null){
    		c.moveToFirst();
    		weightValue = c.getFloat(c.getColumnIndex(Record.FLOATVALUE));
    		c.close();
    	}
    	
    	return weightValue;
    }
 
    private float getBmtValue(){
    	float bmtValue = 0.0f;
    	String selection = Record.TYPE + "=" + Utils.RECORD_TYPE_BMT + " AND " + Record.FLOATVALUE + "=?";
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,null,selection,new String[]{String.valueOf(mMillis)},null);
    	if(c != null){
    		c.moveToFirst();
    		bmtValue = c.getFloat(c.getColumnIndex(Record.FLOATVALUE));
    		c.close();
    	}
    	
    	return bmtValue;
    }
	@Override
	protected void onDraw(Canvas canvas){
		mCanvas = canvas;
	}
	
	public int getRow() {
		return mRow;
	}

	public int getColumn() {
		return mColumn;
	}

	public long getMillis() {
		return mMillis;
	}
	
	public int getDayType() {
		return mDayType;
	}

	public int getNotification() {
		return mNotification;
	}
}
