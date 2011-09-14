package com.njpalmin.womencalendar;

import java.util.Calendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
	
	private WomenCalendarView mWomenCalendarView;
    private int mStartDay;
    private ContentResolver mContentResolver;
    private Time mTime;
    private Time mStartedPirodTime;
    private LinearLayout mStartPeriod;
    private ImageView mBMTChartImageView;
    private ImageView mPreMonthIV;
    private ImageView mNextMonthIV;
    private CalendarView mCalendarView;
    private LinearLayout mCalendarLayout;
    //private WomenCalendarDbAdapter mWCDbAdapter;
    private WomenCalendarDatabaseHelper mHelper;
    private Cursor mCursor;
    private Time mStartedTime;
    private ContentResolver mCr;

    private static final int DAY_OF_WEEK_LABEL_IDS[] = {
        R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6
    };
    private static final int DAY_OF_WEEK_KINDS[] = {
        Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
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
        
        mCr = getContentResolver();
        
        mCursor = mCr.query(Profile.CONTENT_URI,null,null,null,null);
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
        	mCr.insert(Profile.CONTENT_URI,values);
        }
        initView();
    }
    
    int getStartDay() {
        return mStartDay;
    }
    
    private void initView(){
    	mStartedTime = getStartedTime();
    	
    	mWomenCalendarView = new WomenCalendarView(this, mTime ,mStartedTime);
        mCalendarLayout=(LinearLayout)findViewById(R.id.calendar_layout);
        mCalendarLayout.addView(mWomenCalendarView);
    	
        
        mBMTChartImageView = (ImageView)findViewById(R.id.top_bmt_chart);
    	mBMTChartImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WomenCalendarActivity.this, BMTChartActivity.class);
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
    	mCalendarLayout.removeView(mWomenCalendarView);
    	mWomenCalendarView = new WomenCalendarView(this, time, mStartedTime);
    	mCalendarLayout.addView(mWomenCalendarView);
    	mWomenCalendarView.requestFocus();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	if(requestCode == DAY_ACTIVITY_DETAILS && resultCode ==RESULT_OK){
    		long millis = intent.getLongExtra(Utils.EVENT_BEGIN_TIME, 0);
    		mTime.set(millis);
    	}
    }    
    
    private Time getStartedTime(){
    	Time started;
    	mCursor = mCr.query(Record.CONTENT_URI,null,null,null,null);

    	if(mCursor != null && mCursor.getCount() != 0){
    		started = new Time();
    		long millis = mCursor.getLong(mCursor.getColumnIndex(Record.DATE))* 1000;
        	started.set(millis);
        	started.normalize(true);
        	return started;
    	}else{
    		return null;
    	}
    }
    
    
}