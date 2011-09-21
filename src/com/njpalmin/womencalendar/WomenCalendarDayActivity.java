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
	
		mRow = getIntent().getIntExtra(Utils.EVENT_ROW,-1);
		mColumn = getIntent().getIntExtra(Utils.EVENT_COLUMN,-1);
		
		mMillis = getIntent().getLongExtra(Utils.EVENT_BEGIN_TIME, 0);
		//mDayType = getIntent().getIntExtra(Utils.DAY_TYPE,Utils.DAY_TYPE_NORMAL_DAY);
		
		mTime = new Time();
		mTime.set(mMillis);
		updateTitle(mTime);
		
		//String str = mTime.format("%Y%m%d");
		mContentResolver = getContentResolver();
		mCursor = mContentResolver.query(Profile.CONTENT_URI,null,null,null,null);
		if(mCursor != null && mCursor.getCount() != 0){
			mCursor.moveToFirst();
			mProfilePK = mCursor.getLong(mCursor.getColumnIndex(Profile._ID));
		}
		
		
		initView();
		
	}
	
	void initView(){
		mDate = Integer.parseInt(mTime.format(getString(R.string.date_format)));
		
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
		});

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
		});
		
		mPill = (LinearLayout)findViewById(R.id.day_pill);
		mPill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	long millis = mTime.toMillis(true);
            	if((mRecordType & RECORD_TYPE_PILL) != 0 ){
            		mContentResolver.delete(Record.CONTENT_URI, Record.DATE + "=?" + " AND " + Record.TYPE + "=?", 
            					new String[]{String.valueOf(mDate),Utils.RECORD_TYPE_PILL});
            	}else{
    				ContentValues values = new ContentValues();
    				values.put(Record.PROFILEPK, mProfilePK);
    				values.put(Record.DATE, mDate);
    				values.put(Record.TYPE, Utils.RECORD_TYPE_PILL);
    				values.put(Record.INTVALUE, mDate);
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
            	if((mRecordType & RECORD_TYPE_SEX) != 0 ){
            		mContentResolver.delete(Record.CONTENT_URI, Record.DATE + "=?" + " AND " + Record.TYPE + "=?", 
            					new String[]{String.valueOf(mDate),Utils.RECORD_TYPE_SEX});
            	}else{
    				ContentValues values = new ContentValues();
    				values.put(Record.PROFILEPK, mProfilePK);
    				values.put(Record.DATE, mDate);
    				values.put(Record.TYPE, Utils.RECORD_TYPE_SEX);
    				values.put(Record.INTVALUE, mDate);
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
        	//mWCDbAdapter.close();
            finish();
        }
    } 
    
    private boolean hasRecord(int date){
    	boolean hasRecord = false;
    	String selection = Record.DATE + "='" + String.valueOf(date) + "'";
    	
    	mCursor = mContentResolver.query(Record.CONTENT_URI,null,selection,null, Record.DEFAULT_SORT_ORDER);
    	
    	if(mCursor != null && mCursor.getCount() != 0){
			mCursor.moveToFirst();
			int size = mCursor.getCount();
			
			mRecordTypes = new String[size];
			
			for(int i=0;i<size;i++){
				mRecordTypes[i] = mCursor.getString(mCursor.getColumnIndex(Record.TYPE));
			}
			hasRecord = true;
    	}
    	
    	return hasRecord;	
    }
    
    private boolean  initHeaderView(){
    	LinearLayout dayInfoLayout = (LinearLayout)findViewById(R.id.date_info_layout);
    	
    	boolean hasParameters = false;
    	
    	if(dayInfoLayout != null){
    		dayInfoLayout.removeAllViews();
    		dayInfoLayout.setVisibility(View.VISIBLE);
    		
    		
    		switch (mDayType){
    			case Utils.DAY_TYPE_START_DAY:
    			case Utils.DAY_TYPE_MIDDLE_DAY:
    	    		ImageView startPeriod = new ImageView(this);
    	    		startPeriod.setImageResource(R.drawable.day_start_period);
    	    		dayInfoLayout.addView(startPeriod); 
    	    		hasParameters = true;
    	    	break;
    			case Utils.DAY_TYPE_END_DAY:
    	    		ImageView endPeriod = new ImageView(this);
    	    		endPeriod.setImageResource(R.drawable.day_end_period);
    	    		dayInfoLayout.addView(endPeriod);
    	    		hasParameters = true;
    	    	break;
    			case Utils.DAY_TYPE_FERTILITY_DAY:
    	    		ImageView fertilityDay = new ImageView(this);
    	    		fertilityDay.setImageResource(R.drawable.day_fertility);
    	    		dayInfoLayout.addView(fertilityDay);
    	    		hasParameters = true;
    	    	break;
    			case Utils.DAY_TYPE_FORECAST_DAY:
    	    		ImageView forecastDay = new ImageView(this);
    	    		forecastDay.setImageResource(R.drawable.day_start_period_forecast);
    	    		dayInfoLayout.addView(forecastDay);
    	    		hasParameters = true;
    	    	break;    	    	
    			case Utils.DAY_TYPE_NORMAL_DAY:
    			default:
    				break;
    			
    		}
    		
    		if(hasRecord(mDate)){
    			//int size = mRecordTypes.length;
    			for(int i=0; i< mRecordTypes.length;i++){
    				if(mRecordTypes[i].equals(Utils.RECORD_TYPE_PILL)){
    					mRecordType |= RECORD_TYPE_PILL;
    					ImageView pill = new ImageView(this);
    					pill.setImageResource(R.drawable.day_pill);
        	    		dayInfoLayout.addView(pill);
        	    		TextView pillAdd = (TextView)findViewById(R.id.day_pill_add);
        	    		pillAdd.setText(getString(R.string.remove));
    				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_SEX)){
    					mRecordType |= RECORD_TYPE_SEX;
    					ImageView sex = new ImageView(this);
    					sex.setImageResource(R.drawable.day_sex);
        	    		dayInfoLayout.addView(sex);
        	    		TextView sexAdd = (TextView)findViewById(R.id.day_sex_add);
        	    		sexAdd.setText(getString(R.string.remove));        	    		
    				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_BMT)){
    					mRecordType |= RECORD_TYPE_BMT;
    				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_WEIGHT)){
    					mRecordType |= RECORD_TYPE_WEIGHT;
    				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_NOTE)){
    					mRecordType |= RECORD_TYPE_NOTE;
    				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_START)){
    					mRecordType |= RECORD_TYPE_START;
    				}else if (mRecordTypes[i].equals(Utils.RECORD_TYPE_END)){
    					mRecordType &= RECORD_TYPE_START;
    				}
    			}
    			hasParameters = true;
    		}
    	}
	
		return hasParameters;
    }
    
    private void goToDay(Time time){
    	updateTitle(time);
    	mTime = time;
    	initView();
    }
}
