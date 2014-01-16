package com.womencalendar;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.womencalendar.WomenCalendar.Profile;
import com.womencalendar.WomenCalendar.Record;
import com.womencalendar.utils.Utils;
import com.womencalendar.view.WomenCalendarView;

public class WomenCalendarActivity extends Activity {
	private static final String TAG = "WomenCalendarActivity";
	public static final int DAY_ACTIVITY_DETAILS = 1;
	private static final int QUERY_TOKEN = 53;
	
	private static final int MENU_CYCLE_PERIOD_LENGTH = Menu.FIRST + 1;
    private static final int MENU_SKINS = MENU_CYCLE_PERIOD_LENGTH + 1;
    private static final int MENU_SET_PASSWORD = MENU_SKINS + 1;
    private static final int MENU_NOTIFICATIONS = MENU_SET_PASSWORD + 1;
    private static final int MENU_HELP_ABOUT = MENU_NOTIFICATIONS + 1;
    private static final int MENU_SELECT_LOCALE = MENU_HELP_ABOUT+ 1;
    private static final int MENU_TEMPERATURE_SCALE = MENU_SELECT_LOCALE + 1;
    private static final int MENU_WEEK_START = MENU_TEMPERATURE_SCALE + 1;
    private static final int MENU_BACK_UP = MENU_WEEK_START + 1;
    private static final int MENU_STATISTICS = MENU_BACK_UP + 1;
	
    private static final int DIALOG_CYCLE_PERIOD_LENGTH= 1;
    private static final int DIALOG_SET_PASSWORD = 2;
    private static final int DIALOG_SELECT_SCALE = 3;
    private static final int DIALOG_TEMPERATURE_SCALE = 4;
    private static final int DIALOG_WEEK_START = 5;
    private static final int DIALOG_BACK_UP = 6;
    private static final int DIALOG_STATISTICS = 7;
    private static final int DIALOG_SKINS = 8;
    
	private WomenCalendarView mView;
    private int mStartDay;
    private ContentResolver mContentResolver;
    private RecordObserver mRecordObserver = null;
    private ProfileObserver mProfileObserver = null;
    private ConfigObserver mConfigObserver = null;
	//private CallLogObserver mCallLogObserver = null;
	private Handler mHandler = new Handler();
    private Time mTime;
    private ImageView mBMTChartImageView;
    private ImageView mWeightChartImageView;
    private ImageView mPreMonthIV;
    private ImageView mNextMonthIV;
    private LinearLayout mCalendarLayout;
    private Cursor mCursor;
//    private QueryHandler mQueryHandler;

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
		Record.STRINGVALUE,
	};
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //LinearLayout mainLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.womencalendar_main, null);
        setContentView(R.layout.womencalendar_main);
        
        mContentResolver = getContentResolver();

        setRequestedOrientation(LinearLayout.VERTICAL);
        SharedPreferences appShared = 
                getApplicationContext().getSharedPreferences(null, MODE_PRIVATE);
        mStartDay = appShared.getInt(Utils.SHARED_PREF_START_DAY, Calendar.getInstance().getFirstDayOfWeek());
        
        long now = System.currentTimeMillis();

        mTime = new Time();
        mTime.set(now);
        mTime.set(mTime.monthDay,mTime.month,mTime.year);
        mTime.normalize(true);
       
        // Get first day of week based on locale and populate the day headers
        int diff = mStartDay - Calendar.SUNDAY - 1;
        for (int day = 0; day < 7; day++) {
            final String dayString = DateUtils.getDayOfWeekString(
                    (DAY_OF_WEEK_KINDS[day] + diff) % 7 + 1, DateUtils.LENGTH_MEDIUM);
            final TextView label = (TextView) findViewById(DAY_OF_WEEK_LABEL_IDS[day]);
            label.setText(dayString);
            label.setTextColor(getResources().getColor(R.color.weekdays));
        }
        
        // Set the initial title
        TextView title = (TextView) findViewById(R.id.month_title);
        StringBuffer date = new StringBuffer(Utils.formatMonthYear(this, mTime));
        title.setText(date.toString());
        
        if (getApplicationContext().getSharedPreferences(null, MODE_PRIVATE)
        		.getBoolean("first_start", true)) {
        		startActivity(new Intent(this, WomenSetupWizard.class));
        }
//
//        mContentResolver = getContentResolver();
        
//        
//        mCursor = mContentResolver.query(Profile.CONTENT_URI,null,null,null,null);
//        
//        if(mCursor == null || mCursor.getCount() == 0){
//        	Log.d(TAG,"mCursor == null");
//        	ContentValues values = new ContentValues();
//        	values.put(Profile.CYCLELENGTH,Utils.CYCLE_LENGTH);
//        	values.put(Profile.PERIODLENGTH,Utils.PERIOD_LENGTH);
//        	values.put(Profile.LASTACCESS,now/1000);
//        	values.put(Profile.LUTEALPHASELENGTH, 14);
//        	values.put(Profile.AUTOMATICFORECAST, 0);
//        	values.put(Profile.PILLNOTIFICATION, 0);
//        	values.put(Profile.PERIODNOTIFICATION, 0);
//        	values.put(Profile.PERIODNOTIFICATIONDAYSBEFORE, 0);
//        	values.put(Profile.PERIODNOTIFICATIONREPEAT, 0);
//        	values.put(Profile.OVULATIONNOTIFICATIONREPEAT, 0);
//            values.put(Profile.OVULATIONNOTIFICATIONDAYSBEFORE, 0); 
//            values.put(Profile.OVULATIONNOTIFICATION, 0);
//            mContentResolver.insert(Profile.CONTENT_URI,values);
//        }

////        mRecordObserver = new RecordObserver(mHandler);
////        mProfileObserver = new ProfileObserver(mHandler);
////        mConfigObserver = new ConfigObserver(mHandler);
////        mQueryHandler = new QueryHandler(this);
//        registerObserver();
        initView();
    }
    
    @Override
	public void onDestroy(){
		Log.d(TAG,"onDestroy");
		super.onDestroy();
//		unregisterObserver();
	}
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    int getStartDay() {
        return mStartDay;
    }
    
    private void initView(){
   		mCalendarLayout =(LinearLayout)findViewById(R.id.calendar_layout);
        mView  = new WomenCalendarView(this,mTime,mCalendarLayout);
   	    
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
      	mView.reDrawView(time);
    }
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	if(requestCode == DAY_ACTIVITY_DETAILS && resultCode ==RESULT_OK){
    		long millis = intent.getLongExtra(Utils.EVENT_BEGIN_TIME, 0);
    	}
    }    
    
    private class ConfigObserver extends ContentObserver{

		public ConfigObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
    	
	    public void onChange(boolean selfChange){
	        try{
	            //Log.d(LOG_TAG, "Call log changed");
	            super.onChange(selfChange);
	            //mCallLog.setStatusToChanged();
	            //startQuery();
	            mView.reDrawView(mTime);
	        }catch (Exception e){
	            Log.e(TAG, e.toString());
	        }
	    }
    }
    
    private class ProfileObserver extends ContentObserver{

		public ProfileObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
    	
	    public void onChange(boolean selfChange){
	        try{
	            //Log.d(LOG_TAG, "Call log changed");
	            super.onChange(selfChange);
	            //mCallLog.setStatusToChanged();
	            //startQuery();
	            mView.reDrawView(mTime);
	        }catch (Exception e){
	            Log.e(TAG, e.toString());
	        }
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
	            //startQuery();
	            mView.reDrawView(mTime);
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
	
	private void registerObserver(){
		mContentResolver.registerContentObserver(Record.CONTENT_URI,true, mRecordObserver);
		mContentResolver.registerContentObserver(Record.CONTENT_URI,true, mProfileObserver);
		mContentResolver.registerContentObserver(Record.CONTENT_URI,true, mConfigObserver);
	}
	
	private void unregisterObserver(){
		if(mRecordObserver != null){
			mContentResolver.unregisterContentObserver(mRecordObserver);
			mRecordObserver = null;
		}
		if(mProfileObserver != null){
			mContentResolver.unregisterContentObserver(mProfileObserver);
			mProfileObserver = null;
		}
		if(mConfigObserver != null){
			mContentResolver.unregisterContentObserver(mConfigObserver);
			mConfigObserver = null;
		}		
	}
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_CYCLE_PERIOD_LENGTH, 0, R.string.cycle_period_length)
        .setIcon(R.drawable.ic_menu_period_lenght);

        menu.add(0, MENU_SKINS, 0, R.string.skins)
        .setIcon(R.drawable.ic_menu_skins);

        menu.add(0, MENU_SET_PASSWORD, 0, R.string.set_password)
        .setIcon(R.drawable.ic_menu_password);

        menu.add(0, MENU_NOTIFICATIONS, 0, R.string.notifications)
        .setIcon(R.drawable.ic_menu_notifications);

        menu.add(0, MENU_HELP_ABOUT, 0, R.string.help_about_us)
        .setIcon(R.drawable.ic_menu_help);

        menu.add(0, MENU_SELECT_LOCALE, 0, R.string.choose_language);
        //.setIcon(R.drawable.ic_menu_period_lenght)
        menu.add(0, MENU_TEMPERATURE_SCALE, 0, R.string.temperature);
        menu.add(0, MENU_WEEK_START, 0, R.string.week_starts_with);
        menu.add(0, MENU_BACK_UP, 0, R.string.backup);
        menu.add(0, MENU_STATISTICS, 0, R.string.statistics);
        
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case MENU_CYCLE_PERIOD_LENGTH:
    		showDialog(DIALOG_CYCLE_PERIOD_LENGTH);
    		return true;
    	case MENU_SKINS:
    		showDialog(DIALOG_SKINS);
    		return true;
    	case MENU_SET_PASSWORD:
    		showDialog(DIALOG_SKINS);
    		return true;
    	case MENU_NOTIFICATIONS:
    		showDialog(DIALOG_SKINS);
    		return true;
    	case MENU_HELP_ABOUT:
    		showDialog(DIALOG_SKINS);
    		return true;
    	case MENU_SELECT_LOCALE:
    		showDialog(DIALOG_SKINS);
    		return true;
    	case MENU_TEMPERATURE_SCALE:
    		showDialog(DIALOG_SKINS);
    		return true;
    	case MENU_WEEK_START:
    		showDialog(DIALOG_SKINS);
    		return true;
    	case MENU_BACK_UP:
    		showDialog(DIALOG_SKINS);
    		return true;
    	case MENU_STATISTICS:
    		showDialog(DIALOG_SKINS);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
*/
    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CYCLE_PERIOD_LENGTH:
                return createCyclePeriodLengthDialog();
            case DIALOG_SET_PASSWORD:
            	return createRestrictionDialog();
            case DIALOG_SELECT_SCALE:
            	return createRestrictionDialog();
            case DIALOG_TEMPERATURE_SCALE:
            	return createRestrictionDialog();
            case DIALOG_WEEK_START:
            	return createRestrictionDialog();
            case DIALOG_BACK_UP:
            	return createRestrictionDialog();  
            case DIALOG_STATISTICS:
            	return createRestrictionDialog();
            case DIALOG_SKINS:
            	return createRestrictionDialog();
        }
        return super.onCreateDialog(id);
    }
    
    private Dialog createRestrictionDialog(){
    	return new  AlertDialog.Builder(this)
    	.setMessage(getString(R.string.pro_only_functionality))
    	.setNeutralButton(R.string.send, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
			}
    	})
    	.create();
    }
    
    private Dialog createCyclePeriodLengthDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View editEntryView = factory.inflate(R.layout.alert_dialog_cycle_period_entry, null);
        final Cursor c = mContentResolver.query(Profile.CONTENT_URI,null,null,null,null);

    	return new  AlertDialog.Builder(this)
    	.setTitle(R.string.cycle_period_length)
    	.setView(editEntryView)
    	.setPositiveButton(R.string.save,new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				EditText cycleLength = (EditText)editEntryView.findViewById(R.id.cycle_length_value);
				EditText periodLength = (EditText)editEntryView.findViewById(R.id.period_length_value);
				CheckBox autoForecastCB = (CheckBox)editEntryView.findViewById(R.id.auto_forecast);
				int autoForecast =  autoForecastCB.isChecked()? 1:0;
				
				int rowId = 0;
				if(c != null && c.getCount() != 0){
					c.moveToFirst();
					rowId = c.getInt(c.getColumnIndex(Profile._ID));
					c.close();
				}
				
				if(rowId > 0){
					ContentValues values = new ContentValues();
					values.put(Profile.CYCLELENGTH, cycleLength.getText().toString());
					values.put(Profile.PERIODLENGTH, periodLength.getText().toString());
					values.put(Profile.AUTOMATICFORECAST,autoForecast);
					mContentResolver.update(Profile.CONTENT_URI,values,"Profile._ID=?",new String[]{String.valueOf(rowId)});
				}
				
				//finish();
			}
    		
    	})
    	.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				//finish();
			}
    		
    	})
    	.create();
    }
}