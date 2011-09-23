package com.njpalmin.womencalendar;

import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.njpalmin.womencalendar.Utils.DayType;
import com.njpalmin.womencalendar.WomenCalendar.Profile;
import com.njpalmin.womencalendar.WomenCalendar.Record;

public class WomenCalendarDayActivity extends Activity {
	private final static String TAG="WomenCalendarDayActivity";
	
	private final static int RECORD_TYPE_START = 1<<0;
	private final static int RECORD_TYPE_PILL = 1<<1;
	private final static int RECORD_TYPE_BMT = 1<<2;
	private final static int RECORD_TYPE_NOTE = 1<<3;
	private final static int RECORD_TYPE_SEX = 1<<4;
	private final static int RECORD_TYPE_WEIGHT = 1<<5;
	
	private int mRecordType = 0;
	private Time mTime;
	private int mDate;
    private String mTitle;
    private String [] mRecordTypes;
    private int mDayType;
    private int mNotification;
	private int mPeriodLength;
	private int mCycleLength;
	
	
    private ImageView mPreMonthIV;
    private ImageView mNextMonthIV;
    
    
    private LinearLayout mStartPeriod;
    private LinearLayout mEndPeriod;
    private LinearLayout mPill;
    private LinearLayout mBmt;
    private LinearLayout mNote;
    private LinearLayout mSex;
    private LinearLayout mCervicalMucus;
    private LinearLayout mMood;
    private LinearLayout mWeight;
    
    private Button mRemoveAll;
    private Button mBack;
    private boolean mHasRecord = false;
    
    
    
    //private WomenCalendarDbAdapter mWCDbAdapter;
    private Cursor mCursor;
    private ContentResolver mContentResolver;
    private int mYearDay;
    private String mSelection;
    private String [] mSelectionArgs;
    private int mRow = -1;
    private int mColumn = -1;
    private long mMillis = 0;
    private long mProfilePK = -1;
    
	@Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		
		setContentView(R.layout.womencalendarday_main);
		setRequestedOrientation(LinearLayout.VERTICAL);
		
		mRow = getIntent().getIntExtra(Utils.EVENT_ROW,-1);
		mColumn = getIntent().getIntExtra(Utils.EVENT_COLUMN,-1);
		
		mMillis = getIntent().getLongExtra(Utils.EVENT_BEGIN_TIME, 0);
		mDayType = getIntent().getIntExtra(Utils.EVENT_DAY_TYPE, Utils.DAY_TYPE_NORMAL);
		mNotification = getIntent().getIntExtra(Utils.EVENT_NOTIFICATION, 0);
		
		mTime = new Time();
		mTime.set(mMillis);
		updateTitle(mTime);
		
		mContentResolver = getContentResolver();
		mCursor = mContentResolver.query(Profile.CONTENT_URI,null,null,null,null);
		if(mCursor != null){
			mCursor.moveToFirst();
			mProfilePK = mCursor.getLong(mCursor.getColumnIndex(Profile._ID));
			mCursor.close();
		}
		getCycleAndPeriodLength();
		initView();
	}
	
	void initView(){
		initHeaderView();
		mDate = Integer.parseInt(mTime.format(getString(R.string.date_format)));
		/*
		if(initHeaderView()){
			TextView noParameters = (TextView)findViewById(R.id.no_parameters_label_view);
			if(noParameters != null){
				noParameters.setVisibility(View.GONE);
			}
		}
		
		switch (mDayType){
			case Utils.DAY_TYPE_START_DAY:
				LinearLayout endPeriod =(LinearLayout)findViewById(R.id.end_period);
				endPeriod.setEnabled(false);
				endPeriod.setClickable(false);
			break;
			default:
				break;
		}
		*/
    	mPreMonthIV = (ImageView)findViewById(R.id.prev);
    	mPreMonthIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mTime.monthDay -= 1;
            	mTime.normalize(true);
            	goToDay(mTime);
            }
        });
    	
    	mNextMonthIV = (ImageView)findViewById(R.id.next);
    	mNextMonthIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mTime.monthDay += 1;
            	mTime.normalize(true);
            	goToDay(mTime);
            }
        });
		
		mStartPeriod = (LinearLayout)findViewById(R.id.start_period);
		mStartPeriod.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Log.d(TAG,"mStart onClick");
				long millis = mTime.toMillis(true);
				if(mDayType == Utils.DAY_TYPE_START_DAY){
            		mContentResolver.delete(Record.CONTENT_URI, Record.DATE + "=?" + " AND " + Record.TYPE + "=?", 
        					new String[]{String.valueOf(mMillis/1000),Utils.RECORD_TYPE_START});					
				}else{
					if(mProfilePK > 0){
						ContentValues values = new ContentValues();
						values.put(Record.PROFILEPK, mProfilePK);
						values.put(Record.DATE, mTime.toMillis(true)/1000);
						values.put(Record.TYPE, Utils.RECORD_TYPE_START);
						values.put(Record.INTVALUE, (mPeriodLength * Utils.DAY_IN_SECONDS)/*Integer.parseInt(endTime.format(getString(R.string.date_format)))*/);
						mContentResolver.insert(Record.CONTENT_URI,values);
					}
				}
				setIntentAndFinish(true, millis,Utils.OPERATION_ADD_PARAMETER);
			}
		});

		/*
		mEndPeriod = (LinearLayout)findViewById(R.id.end_period);
		mEndPeriod.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Log.d(TAG,"mStart onClick");
				long millis = mTime.toMillis(true);
				
				mCursor = mContentResolver.query(Profile.CONTENT_URI,null,null,null,null);
		        if(mCursor != null){
					long profilePk = -1;
					int cycleLength =0;
					mCursor.moveToFirst();
					profilePk = mCursor.getLong(mCursor.getColumnIndex(Profile._ID));
					cycleLength = mCursor.getInt(mCursor.getColumnIndex(Profile.CYCLELENGTH));
		        
					if(profilePk > 0){
						ContentValues values = new ContentValues();
						values.put(Record.PROFILEPK, profilePk);
						values.put(Record.DATE, mDate);
						values.put(Record.TYPE, Utils.RECORD_TYPE_START);
						values.put(Record.INTVALUE, cycleLength);
						mContentResolver.insert(Record.CONTENT_URI,values);
					}
		        }
				setIntentAndFinish(true, millis,Utils.OPERATION_ADD_PARAMETER);
			}
		});*/
		
		mPill = (LinearLayout)findViewById(R.id.day_pill);
		mPill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	long millis = mTime.toMillis(true);
            	if((mNotification & Utils.NOTIFICATION_TYPE_PILL) != 0){
            		mContentResolver.delete(Record.CONTENT_URI, Record.DATE + "=?" + " AND " + Record.TYPE + "=?", 
            					new String[]{String.valueOf(mMillis/1000),Utils.RECORD_TYPE_PILL});
            	}else{
    				ContentValues values = new ContentValues();
    				values.put(Record.PROFILEPK, mProfilePK);
    				values.put(Record.DATE, mMillis/1000);
    				values.put(Record.TYPE, Utils.RECORD_TYPE_PILL);
    				mContentResolver.insert(Record.CONTENT_URI,values);
            	}
				setIntentAndFinish(true, millis,Utils.OPERATION_ADD_PARAMETER);  
            }
        });

		
		mBmt = (LinearLayout)findViewById(R.id.day_bmt);
		mBmt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//finish();
            }
        });
		
		mNote = (LinearLayout)findViewById(R.id.day_note);
		mNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//finish();
            }
        });
		
		mSex = (LinearLayout)findViewById(R.id.day_sex);
		mSex.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	long millis = mTime.toMillis(true);
            	if((mNotification & Utils.NOTIFICATION_TYPE_SEX) != 0 ){
            		mContentResolver.delete(Record.CONTENT_URI, Record.DATE + "=?" + " AND " + Record.TYPE + "=?", 
            					new String[]{String.valueOf(mMillis/1000),Utils.RECORD_TYPE_SEX});
            	}else{
    				ContentValues values = new ContentValues();
    				values.put(Record.PROFILEPK, mProfilePK);
    				values.put(Record.DATE, mMillis/1000);
    				values.put(Record.TYPE, Utils.RECORD_TYPE_SEX);
    				mContentResolver.insert(Record.CONTENT_URI,values);
            	}
				setIntentAndFinish(true, millis,Utils.OPERATION_ADD_PARAMETER);  
            }
        });
		
		mCervicalMucus = (LinearLayout)findViewById(R.id.day_cervical_mucus);
		mCervicalMucus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//finish();
            }
        });
		
		mMood = (LinearLayout)findViewById(R.id.day_mood);
		mMood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//finish();
            }
        });

		mWeight = (LinearLayout)findViewById(R.id.day_weight);
		mWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//finish();
            }
        });
		
		mBack = (Button)findViewById(R.id.back);
		mBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
	}
	
    void updateTitle(Time time) {
        TextView title = (TextView) findViewById(R.id.date);
        StringBuffer date = new StringBuffer(Utils.formatDate(this, time));
        title.setText(date.toString());
    }
    
    private void setIntentAndFinish(boolean finish, long  millis, String operate) {
        Intent intent = new Intent();
        intent.putExtra(Utils.EVENT_BEGIN_TIME, millis);
        intent.putExtra(Utils.OPERATION,operate);
        setResult(WomenCalendarActivity.RESULT_OK, intent);
        if(finish) {
            finish();
        }
    } 
    
    private void  initHeaderView(){
    	LinearLayout dayInfoLayout = (LinearLayout)findViewById(R.id.date_info_layout);
    	
    	
    	if(dayInfoLayout != null){
    		dayInfoLayout.removeAllViews();
    		dayInfoLayout.setVisibility(View.VISIBLE);
    		
    		
    		switch (mDayType){
    			case Utils.DAY_TYPE_START_DAY:
    			case Utils.DAY_TYPE_MIDDLE_DAY:
    	    		ImageView startPeriod = new ImageView(this);
    	    		startPeriod.setImageResource(R.drawable.day_start_period);
    	    		dayInfoLayout.addView(startPeriod); 
    	    		TextView startAdd = (TextView)findViewById(R.id.day_start_period_add);
    	    		startAdd.setText(getString(R.string.remove)); 
    	    	break;
    			case Utils.DAY_TYPE_END_DAY:
    	    		ImageView endPeriod = new ImageView(this);
    	    		endPeriod.setImageResource(R.drawable.day_end_period);
    	    		dayInfoLayout.addView(endPeriod);
    	    	break;
    			case Utils.DAY_TYPE_FERTILITY_DAY:
    	    		ImageView fertilityDay = new ImageView(this);
    	    		fertilityDay.setImageResource(R.drawable.day_fertility);
    	    		dayInfoLayout.addView(fertilityDay);
    	    	break;
    			case Utils.DAY_TYPE_FORECAST_DAY:
    	    		ImageView forecastDay = new ImageView(this);
    	    		forecastDay.setImageResource(R.drawable.day_start_period_forecast);
    	    		dayInfoLayout.addView(forecastDay);
    	    	break;    	    	
    			case Utils.DAY_TYPE_NORMAL_DAY:
    			default:
    				break;
    			
    		}
    		
    		if((mNotification & Utils.NOTIFICATION_TYPE_PILL) != 0){
				ImageView pill = new ImageView(this);
				pill.setImageResource(R.drawable.day_pill);
	    		dayInfoLayout.addView(pill);
	    		TextView pillAdd = (TextView)findViewById(R.id.day_pill_add);
	    		pillAdd.setText(getString(R.string.remove));
    		}
    		
    		if((mNotification & Utils.NOTIFICATION_TYPE_SEX) != 0){
				ImageView sex = new ImageView(this);
				sex.setImageResource(R.drawable.day_sex);
	    		dayInfoLayout.addView(sex);
	    		TextView sexAdd = (TextView)findViewById(R.id.day_sex_add);
	    		sexAdd.setText(getString(R.string.remove)); 
    		}
    		
    		if((mNotification & Utils.NOTIFICATION_TYPE_BMT) != 0){
    			TextView bmt = new TextView(this);
    			bmt.setText(String.valueOf(getBmtValue()));
    			dayInfoLayout.addView(bmt);
	    		TextView bmtAdd = (TextView)findViewById(R.id.day_bmt_add);
	    		bmtAdd.setText(getString(R.string.remove_edit));     			
    		}
    		
    		if((mNotification & Utils.NOTIFICATION_TYPE_WEIGHT) != 0){
    			TextView weight = new TextView(this);
    			weight.setText(String.valueOf(getWeightValue()));
    			dayInfoLayout.addView(weight);
	    		TextView weightAdd = (TextView)findViewById(R.id.day_weight_add);
	    		weightAdd.setText(getString(R.string.remove_edit));     			
    		}
    		
    		if((mNotification & Utils.NOTIFICATION_TYPE_NOTE) != 0){
	    		TextView noteAdd = (TextView)findViewById(R.id.day_note_add);
	    		noteAdd.setText(getString(R.string.remove_edit));
	    		
	    		TextView noParameter = (TextView)findViewById(R.id.no_parameters_label_view);
	    		noParameter.setVisibility(View.GONE); 
	    		
	    		LinearLayout noteLayout = (LinearLayout)findViewById(R.id.note_layout);
	    		noteLayout.setVisibility(View.VISIBLE);
	    		
    			TextView note = (TextView)findViewById(R.id.note_text_view);
    			note.setText(getNoteValue());
    		}
    	}
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
    
    private String getNoteValue(){
    	String noteValue = "";
    	String selection = Record.TYPE + "=" + Utils.RECORD_TYPE_NOTE + " AND " + Record.STRINGVALUE + "=?";
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,null,selection,new String[]{String.valueOf(mMillis)},null);
    	if(c != null){
    		c.moveToFirst();
    		noteValue = c.getString(c.getColumnIndex(Record.STRINGVALUE));
    		c.close();
    	}
    	
    	return noteValue;
    }
    
    private void goToDay(Time time){
    	updateTitle(time);
    	mTime = time;
    	initView();
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
}
