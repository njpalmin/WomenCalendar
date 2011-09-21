package com.njpalmin.womencalendar;

import java.util.Calendar;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog.Calls;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.njpalmin.womencalendar.WomenCalendar.Profile;
import com.njpalmin.womencalendar.WomenCalendar.Record;

public class WomenCalendarActivity extends Activity {
	private static final String TAG = "WomenCalendarActivity";
	public static final int DAY_ACTIVITY_DETAILS = 1;
	private static final int QUERY_TOKEN = 53;
	
	private WomenCalendarView mWomenCalendarView;
	private CalendarView mView;
    private int mStartDay;
    private ContentResolver mContentResolver;
    private RecordObserver mRecordObserver = null;
	//private CallLogObserver mCallLogObserver = null;
	private Handler mHandler = new Handler();
    private Time mTime;
    private Time mStartedPirodTime;
    private LinearLayout mStartPeriod;
    private ImageView mBMTChartImageView;
    private ImageView mWeightChartImageView;
    private ImageView mPreMonthIV;
    private ImageView mNextMonthIV;
    private CalendarView mCalendarView;
    private LinearLayout mCalendarLayout;
    //private WomenCalendarDbAdapter mWCDbAdapter;
    private WomenCalendarDatabaseHelper mHelper;
    private Cursor mCursor;
    //private Time mStartedTime;
    //private ContentResolver mCr;
    //private WomenCalendarAdapter mAdapter;
    private QueryHandler mQueryHandler;

    private static final int DAY_OF_WEEK_LABEL_IDS[] = {
        R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6
    };
    private static final int DAY_OF_WEEK_KINDS[] = {
        Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
    };
    
    static final String[] RECORD_PROJECTION = new String[] {
		Record._ID,
		Record.PROFILEPK,
		Record.DATE,
		Record.TYPE,
		Record.INTVALUE,
		Record.FLOATVALUE,
		Record.STRINGGVALUE,
	};
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //LinearLayout mainLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.womencalendar_main, null);
        setContentView(R.layout.womencalendar_main);
        mContentResolver = getContentResolver();

        setRequestedOrientation(LinearLayout.VERTICAL);
        mStartDay = Calendar.getInstance().getFirstDayOfWeek();
        long now = System.currentTimeMillis();

        mTime = new Time();
        mTime.set(now);
        mTime.normalize(true);
        
        // Get first day of week based on locale and populate the day headers
        mStartDay = Calendar.getInstance().getFirstDayOfWeek();
        int diff = mStartDay - Calendar.SUNDAY - 1;
        final int startDay = Utils.getFirstDayOfWeek();
        final int sundayColor = getResources().getColor(R.color.sunday_text_color);
        final int saturdayColor = getResources().getColor(R.color.saturday_text_color);

        for (int day = 0; day < 7; day++) {
            final String dayString = DateUtils.getDayOfWeekString(
                    (DAY_OF_WEEK_KINDS[day] + diff) % 7 + 1, DateUtils.LENGTH_MEDIUM);
            final TextView label = (TextView) findViewById(DAY_OF_WEEK_LABEL_IDS[day]);
            label.setText(dayString);
            label.setTextColor(getResources().getColor(R.color.weekdays));
            /*
            if (Utils.isSunday(day, startDay)) {
                label.setTextColor(sundayColor);
            } else if (Utils.isSaturday(day, startDay)) {
                label.setTextColor(saturdayColor);
            }*/
        }
        
        // Set the initial title
        TextView title = (TextView) findViewById(R.id.month_title);
        StringBuffer date = new StringBuffer(Utils.formatMonthYear(this, mTime));
        title.setText(date.toString());
        
        mContentResolver = getContentResolver();
        
        mCursor = mContentResolver.query(Profile.CONTENT_URI,null,null,null,null);
        
        if(mCursor == null || mCursor.getCount() == 0){
        	Log.d(TAG,"mCursor == null");
        	ContentValues values = new ContentValues();
        	values.put(Profile.CYCLELENGTH,Utils.CYCLE_LENGTH);
        	values.put(Profile.PERIODLENGTH,Utils.PERIOD_LENGTH);
        	values.put(Profile.LASTACCESS,now/1000);
        	values.put(Profile.LUTEALPHASELENGTH, 0);
        	values.put(Profile.AUTOMATICFORECAST, 0);
        	values.put(Profile.PILLNOTIFICATION, 0);
        	values.put(Profile.PERIODNOTIFICATION, 0);
        	values.put(Profile.PERIODNOTIFICATIONDAYSBEFORE, 0);
        	values.put(Profile.PERIODNOTIFICATIONREPEAT, 0);
        	values.put(Profile.OVULATIONNOTIFICATIONREPEAT, 0);
            values.put(Profile.OVULATIONNOTIFICATIONDAYSBEFORE, 0); 
            values.put(Profile.OVULATIONNOTIFICATION, 0);
            mContentResolver.insert(Profile.CONTENT_URI,values);
        }
        
        mRecordObserver = new RecordObserver(mHandler);
        mQueryHandler = new QueryHandler(this);
        registerRecordObserver();
        initView();
    }
    
    @Override
	public void onDestroy(){
		Log.d(TAG,"onDestroy");
		super.onDestroy();
		unregisterRecordObserver();
	}
    
    
    int getStartDay() {
        return mStartDay;
    }
    
    private void initView(){
    	//mStartedTime = getStartedTime();
    	
    	//mAdapter = new WomenCalendarAdapter();
    	
    	//mWomenCalendarView = new WomenCalendarView(this, mTime ,mStartedTime);
    	
    	mCalendarLayout =(LinearLayout)findViewById(R.id.calendar_layout);
        mView  = new CalendarView(this,mTime,mCalendarLayout);
        //mWomenCalendarView.setAdapter(mAdapter);
        //mCalendarLayout.addView(mView);
    	
        
        mBMTChartImageView = (ImageView)findViewById(R.id.top_bmt_chart);
    	mBMTChartImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WomenCalendarActivity.this, BMTChartActivity.class);
                startActivity(intent);
                //startActivityForResult(intent,DAY_ACTIVITY_DETAILS);
            }
        });
    	
    	mWeightChartImageView = (ImageView)findViewById(R.id.top_weight_chart);
    	mWeightChartImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WomenCalendarActivity.this, WeightChartActivity.class);
                startActivity(intent);
                //startActivityForResult(intent,DAY_ACTIVITY_DETAILS);
            }
        });
    	
    	mPreMonthIV = (ImageView)findViewById(R.id.prev);
    	mPreMonthIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mTime.month -= 1;
            	mTime.normalize(true);
            	goToMonth(mTime);
            }
        });
    	
    	mNextMonthIV = (ImageView)findViewById(R.id.next);
    	mNextMonthIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mTime.month += 1;
            	mTime.normalize(true);
            	goToMonth(mTime);
            }
        });
    }
    
    public void updateTitle(Time time) {
        TextView title = (TextView) findViewById(R.id.month_title);
        StringBuffer date = new StringBuffer(Utils.formatMonthYear(this, time));
        title.setText(date.toString());
    }
    
    public void goToMonth(Time time){
    	updateTitle(time);
    	//mCalendarLayout=(LinearLayout)findViewById(R.id.calendar_layout);
    	/*
    	mCalendarLayout.removeView(mWomenCalendarView);
    	mView = new CalendarView(this, time, mCalendarLayout);
    	mCalendarLayout.addView(mView);
    	mView.requestFocus();*/
    	mView.reDrawView(time);
    	//mView.invalidate();
    }
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	if(requestCode == DAY_ACTIVITY_DETAILS && resultCode ==RESULT_OK){
    		long millis = intent.getLongExtra(Utils.EVENT_BEGIN_TIME, 0);
    		/*
    		String operate = intent.getStringExtra(Utils.DAY_TYPE);
    		
    		Time setTime = new Time();
    		setTime.set(millis);
    		
    		if(mWomenCalendarView != null){
    			mWomenCalendarView.invalidate();//InitView(this,mTime,setTime);
    		}*/
    	}
    }    
    
    private Time getStartedTime(){
    	Time started;
    	mCursor = mContentResolver.query(Record.CONTENT_URI,null,null,null,null);

    	if(mCursor != null && mCursor.getCount() != 0){
    		started = new Time();
    		mCursor.moveToFirst();
    		long millis = mCursor.getLong(mCursor.getColumnIndex(Record.DATE));
        	started.set(millis);
        	started.normalize(true);
        	return started;
    	}else{
    		return null;
    	}
    }
    
    private class RecordObserver extends ContentObserver{

		public RecordObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		
	    public void onChange(boolean selfChange){
	        try{
	            //Log.d(LOG_TAG, "Call log changed");
	            super.onChange(selfChange);
	            //mCallLog.setStatusToChanged();
	            startQuery();
	        }catch (Exception e){
	            Log.e(TAG, e.toString());
	        }
	    }
    };
    
	private final class QueryHandler extends AsyncQueryHandler{

		public QueryHandler(Context context) {
			super(context.getContentResolver());
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor){
			Log.d(TAG,"onQueryComplete");
			if( cursor == null){
				return;
			} else {
				//Calls call = new Calls();
				cursor.moveToFirst();
				Log.d(TAG,"id ="+cursor.getLong(cursor.getColumnIndex(Record._ID)));
			}
		}
		
	}
	
	private void registerRecordObserver(){
		mContentResolver.registerContentObserver(Record.CONTENT_URI,true, mRecordObserver);
	}
	
	private void unregisterRecordObserver(){
		if(mRecordObserver != null){
			mContentResolver.unregisterContentObserver(mRecordObserver);
			mRecordObserver = null;
		}
	}
	
    private void startQuery() {
    	Log.d(TAG,"startQuery");
    	// Cancel any pending queries
    	mQueryHandler.cancelOperation(QUERY_TOKEN);
    	mQueryHandler.startQuery(QUERY_TOKEN, null, Record.CONTENT_URI,
    			RECORD_PROJECTION, null, null, Record.DEFAULT_SORT_ORDER);
    }
}