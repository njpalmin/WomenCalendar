package com.njpalmin.womencalendar;

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

import com.njpalmin.womencalendar.WomenCalendar.Profile;
import com.njpalmin.womencalendar.WomenCalendar.Record;

public class WomenCalendarDayActivity extends Activity {
	private final static String TAG="WomenCalendarDayActivity";
	
	private Time mTime;
    private String mTitle;
    
    private LinearLayout mStartPeriod;
    private ImageView mEnd;
    private ImageView mPill;
    private ImageView mBmt;
    private ImageView mNote;
    private ImageView mSex;
    private ImageView mCervicalMucus;
    private ImageView mMood;
    private ImageView mWeight;
    
    private Button mRemoveAll;
    private Button mBack;
    private boolean mHasRecord = false;
    
    //private WomenCalendarDbAdapter mWCDbAdapter;
    private long mProfilePK;
    private Cursor mCursor;
    private ContentResolver mCr;
    private int mYearDay;
    private String mSelection;
    private String [] mSelectionArgs;
    
	@Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		long millis = 0;
		
		setContentView(R.layout.womencalendarday_main);
		millis = getIntent().getLongExtra(Utils.EVENT_BEGIN_TIME, 0);
		
		mTime = new Time();
		mTime.set(millis);
		updateTitle(mTime);
		
		mCr = getContentResolver();
				
		initView();
		
		
		
	}
	
	void initView(){
		/*
		if(mCursor != null && mCursor.getCount() == 0){
			mHasRecord = true;
		}
		*/
		//if(hasRecord(mTime)){
			Log.d(TAG,"mTime millis="+mTime.toMillis(true));
			
			mCursor = mCr.query(Record.CONTENT_URI,null,null,null,null);
			
			if(mCursor != null){
				mCursor.moveToFirst();
				long m = mCursor.getLong(mCursor.getColumnIndex(Record.DATE));
				Log.d(TAG,"m="+m);
			}
			
		//}
		mStartPeriod = (LinearLayout)findViewById(R.id.start_period);
		mStartPeriod.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Log.d(TAG,"mStart onClick");
				long millis = mTime.toMillis(true);
				
				mCursor = mCr.query(Profile.CONTENT_URI,null,null,null,null);
				
				long profilePk = -1;
				
		        if(mCursor != null){
					Log.d(TAG,"null");
					mCursor.moveToFirst();
					profilePk = mCursor.getLong(mCursor.getColumnIndex(Profile._ID));
		        }
		        
				if(profilePk > 0){
					ContentValues values = new ContentValues();
					values.put(Record.PROFILEPK, profilePk);
					values.put(Record.DATE, millis);
					values.put(Record.TYPE, Utils.RECORD_TYPE_PERIOD);
					mCr.insert(Record.CONTENT_URI,values);
				}
				
				setIntentAndFinish(true, millis);
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
    
    void setStartDay(Time time){
    	
    }
    
    private void setIntentAndFinish(boolean finish, long  millis) {
        Intent intent = new Intent();
        intent.putExtra(Utils.EVENT_BEGIN_TIME, millis);
        setResult(WomenCalendarActivity.RESULT_OK, intent);
        if(finish) {
        	//mWCDbAdapter.close();
            finish();
        }
    } 
    
    private boolean hasRecord(Time time){
    	boolean hasRecord = false;
    	
    	long millis = time.toMillis(true);
    	
    	String selection = Record.DATE + "='" + String.valueOf(millis) + "'";
    	
    	mCursor = mCr.query(Record.CONTENT_URI,null,selection,null,null);
    	
    	if(mCursor != null){
    		if(mCursor.getCount() == 0){
    			return false;
    		}else{
    			mCursor.moveToFirst();
    			return true;
    		}
    	}else{
    		return false;
    	}
    	
    	
    }
}
