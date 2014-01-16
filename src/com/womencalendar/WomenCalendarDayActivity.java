package com.womencalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.womencalendar.WomenCalendar.Config;
import com.womencalendar.WomenCalendar.Profile;
import com.womencalendar.WomenCalendar.Record;
import com.womencalendar.utils.Utils;

public class WomenCalendarDayActivity extends Activity {
	private final static String TAG="WomenCalendarDayActivity";

    private static final int DIALOG_ADD_BMT = 1;
    private static final int DIALOG_REMOVE_BMT = 2;
    private static final int DIALOG_ADD_NOTE = 3;
    private static final int DIALOG_REMOVE_NOTE = 4;
    private static final int DIALOG_ADD_WEIGHT = 5;
    private static final int DIALOG_REMOVE_WEIGHT = 6;
    
    private Context mContext;
	private Time mTime;
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
    
    private AlertDialog mBmtDialog;
    private AlertDialog mNoteDialog;
    private AlertDialog mWeightDialog;
    
    private Button mRemoveAll;
    private Button mBack;
    private boolean mHasRecord = false;
    private String mTemperatureScale;
    private String mWeightScale;
    private String mLocalTemperatureScale;
    private String mLocalWeightScale;
    
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
    private String mDate;
    
	@Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mContext = this;
		setContentView(R.layout.womencalendarday_main);
		setRequestedOrientation(LinearLayout.VERTICAL);
		
		mDate = getIntent().getStringExtra(Utils.EXTRAS_SELECTED_DAY);
		mTime = new Time();
		mTime.set(Utils.getMonthOfDayFromDate(mDate), Utils.getMonthFromDate(mDate), Utils.getYearFromDate(mDate));
		
		
//		mRow = getIntent().getIntExtra(Utils.EVENT_ROW,-1);
//		mColumn = getIntent().getIntExtra(Utils.EVENT_COLUMN,-1);
//		
//		mMillis = getIntent().getLongExtra(Utils.EVENT_BEGIN_TIME, 0);
//		mDayType = getIntent().getIntExtra(Utils.EVENT_DAY_TYPE, Utils.DAY_TYPE_NORMAL);
//		mNotification = getIntent().getIntExtra(Utils.EVENT_NOTIFICATION, 0);
//		
//		mTime = new Time();
//		mTime.set(mMillis);
//		
		mContentResolver = getContentResolver();
//		mCursor = mContentResolver.query(Profile.CONTENT_URI,null,null,null,null);
//		if(mCursor != null){
//			mCursor.moveToFirst();
//			mProfilePK = mCursor.getLong(mCursor.getColumnIndex(Profile._ID));
//			mCursor.close();
//		}
//		getCycleAndPeriodLength();
//		getConfig();
		initView();
	}
	
	void initView(){
		initHeaderView();
		/*
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
				Log.d(TAG,"Start onClick");
				ContentValues values = new ContentValues();
				values.put(Record.PROFILEPK, 1);
				values.put(Record.DATE, Integer.parseInt(mDate));
				values.put(Record.TYPE, Utils.RECORD_TYPE_START);
				values.put(Record.INTVALUE, Utils.getPeriodLength(mContext));
				mContentResolver.insert(Record.CONTENT_URI, values);
				finish();
//				long millis = mTime.toMillis(true);
//				if(mDayType == Utils.DAY_TYPE_START_DAY){
//            		mContentResolver.delete(Record.CONTENT_URI, Record.DATE + "=?" + " AND " + Record.TYPE + "=?", 
//        					new String[]{String.valueOf(mMillis/1000),Utils.RECORD_TYPE_START});					
//				}else{
//					if(mProfilePK > 0){
//						ContentValues values = new ContentValues();
//						values.put(Record.PROFILEPK, mProfilePK);
//						values.put(Record.DATE, mTime.toMillis(true)/1000);
//						values.put(Record.TYPE, Utils.RECORD_TYPE_START);
//						values.put(Record.INTVALUE, (mPeriodLength * Utils.DAY_IN_SECONDS)/*Integer.parseInt(endTime.format(getString(R.string.date_format)))*/);
//						mContentResolver.insert(Record.CONTENT_URI,values);
//					}
//				}
//				setIntentAndFinish(true, millis,Utils.OPERATION_ADD_PARAMETER);
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
            	long millis = mTime.toMillis(true);
            	if((mNotification & Utils.NOTIFICATION_TYPE_BMT) != 0){
            		showDialog(DIALOG_REMOVE_BMT);
            	}else {
            		showDialog(DIALOG_ADD_BMT);
            	}
            	//setIntentAndFinish(true, millis,Utils.OPERATION_ADD_PARAMETER);  
            }
        });
		
		mNote = (LinearLayout)findViewById(R.id.day_note);
		mNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//finish();
            	if((mNotification & Utils.NOTIFICATION_TYPE_NOTE) != 0){
            		showDialog(DIALOG_REMOVE_NOTE);
            	}else{
            		showDialog(DIALOG_ADD_NOTE);
            	}
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
            	if((mNotification & Utils.NOTIFICATION_TYPE_WEIGHT) != 0){
            		showDialog(DIALOG_REMOVE_WEIGHT);
            	}else{
            		showDialog(DIALOG_ADD_WEIGHT);
            	}
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
        updateTitle(mTime);
    	LinearLayout dayInfoLayout = (LinearLayout)findViewById(R.id.date_info_layout);
    	
    	
    	if(dayInfoLayout != null){
    		dayInfoLayout.removeAllViews();
    		dayInfoLayout.setVisibility(View.VISIBLE);
    		
    		TextView paramsDescription = (TextView)findViewById(R.id.parameter_description_text_view);
    		TextView noParameter = (TextView)findViewById(R.id.no_parameters_label_view);
    		
//    		switch (mDayType){
//    			case Utils.DAY_TYPE_START_DAY:
//    			case Utils.DAY_TYPE_MIDDLE_DAY:
//    	    		ImageView startPeriod = new ImageView(this);
//    	    		startPeriod.setImageResource(R.drawable.day_start_period);
//    	    		dayInfoLayout.addView(startPeriod); 
//    	    		TextView startAdd = (TextView)findViewById(R.id.day_start_period_add);
//    	    		startAdd.setText(getString(R.string.remove)); 
//    	    	break;
//    			case Utils.DAY_TYPE_END_DAY:
//    	    		ImageView endPeriod = new ImageView(this);
//    	    		endPeriod.setImageResource(R.drawable.day_end_period);
//    	    		dayInfoLayout.addView(endPeriod);
//    	    	break;
//    			case Utils.DAY_TYPE_FERTILITY_DAY:
//    	    		ImageView fertilityDay = new ImageView(this);
//    	    		fertilityDay.setImageResource(R.drawable.day_fertility);
//    	    		dayInfoLayout.addView(fertilityDay);
//    	    		paramsDescription.setVisibility(View.VISIBLE);
//    	    		paramsDescription.setText(getString(R.string.fertility_forecast));
//    	    		noParameter.setVisibility(View.GONE);
//    	    	break;
//    			case Utils.DAY_TYPE_OVULATION_DAY:
//    	    		ImageView ovulationDay = new ImageView(this);
//    	    		ovulationDay.setImageResource(R.drawable.day_ovulation);
//    	    		dayInfoLayout.addView(ovulationDay);
//    	    		paramsDescription.setVisibility(View.VISIBLE);
//    	    		paramsDescription.setText(getString(R.string.ovulation_forecast));
//    	    		noParameter.setVisibility(View.GONE);
//    				break;
//    			case Utils.DAY_TYPE_FORECAST_DAY:
//    	    		ImageView forecastDay = new ImageView(this);
//    	    		forecastDay.setImageResource(R.drawable.day_start_period_forecast);
//    	    		dayInfoLayout.addView(forecastDay);
//    	    	break;    	    	
//    			case Utils.DAY_TYPE_NORMAL_DAY:
//    			default:
//    				break;
//    			
//    		}
    		
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
	    		bmtAdd.setText(getString(R.string.edit_remove));     			
    		}
    		
    		if((mNotification & Utils.NOTIFICATION_TYPE_WEIGHT) != 0){
    			TextView weight = new TextView(this);
    			weight.setText(String.valueOf(getWeightValue()));
    			dayInfoLayout.addView(weight);
	    		TextView weightAdd = (TextView)findViewById(R.id.day_weight_add);
	    		weightAdd.setText(getString(R.string.edit_remove));     			
    		}
    		
    		if((mNotification & Utils.NOTIFICATION_TYPE_NOTE) != 0){
	    		TextView noteAdd = (TextView)findViewById(R.id.day_note_add);
	    		noteAdd.setText(getString(R.string.edit_remove));
	    		

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
    	String selection = Record.TYPE + "=?" + " AND " + Record.DATE + "=?";
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,null,selection,
    									new String[]{Utils.RECORD_TYPE_BMT ,String.valueOf(mMillis/1000)},null);
    	if(c != null){
    		if(c.getCount() != 0){
	    		c.moveToFirst();
	    		bmtValue = c.getFloat(c.getColumnIndex(Record.FLOATVALUE));
    		}
    		c.close();
    	}
    	
    	return bmtValue;
    }
 
    private float getWeightValue(){
    	float weightValue = 0.0f;
    	String selection = Record.TYPE + "=?" + " AND " + Record.DATE + "=?";
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,null,selection,
    									new String[]{Utils.RECORD_TYPE_WEIGHT,String.valueOf(mMillis/1000)},null);
    	if(c != null){
    		c.moveToFirst();
    		weightValue = c.getFloat(c.getColumnIndex(Record.FLOATVALUE));
    		c.close();
    	}
    	
    	return weightValue;
    }
    
    private String getNoteValue(){
    	String noteValue = "";
    	String selection = Record.TYPE + "=?" + " AND " + Record.DATE + "=?";
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,null,selection,
    									 new String[]{Utils.RECORD_TYPE_NOTE,String.valueOf(mMillis/1000)},null);
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
    
    private Dialog createBmtAddDialog(){
    	
        LayoutInflater factory = LayoutInflater.from(this);
        final View editEntryView = factory.inflate(R.layout.alert_dialog_edit_entry, null);
        
        final RadioGroup  scale = (RadioGroup)editEntryView.findViewById(R.id.scale);
        final RadioButton unit1 = (RadioButton)editEntryView.findViewById(R.id.unit1);
        final RadioButton unit2 = (RadioButton)editEntryView.findViewById(R.id.unit2);
        final EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
        final TextView tv = (TextView)editEntryView.findViewById(R.id.value_view);
        
        unit1.setText(R.string.celsius);
        unit2.setText(R.string.fahrenheit);
        
        scale.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == unit1.getId()){
					tv.setText(R.string.celsius_symbol);
					//ev.setText(R.string.default_value_in_celsius);
					ev.setText(Utils.fahrenheitToCelsius(ev.getText().toString()));
					mLocalTemperatureScale = Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE;
				}else if (checkedId == unit2.getId()){
					mLocalTemperatureScale = Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE;
					tv.setText(R.string.fahrenheit_symbol);
					ev.setText(Utils.celsiusToFahrenheit(ev.getText().toString()));
					//ev.setText(R.string.default_value_in_fahrenheit);
				}
			}
        	
        });
        
        if(mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE)){
        	scale.check(unit2.getId());
        	tv.setText(R.string.fahrenheit_symbol);
        	ev.setText(R.string.default_value_in_fahrenheit);
        }else if (mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE)){
        	scale.check(unit1.getId());
        	tv.setText(R.string.celsius_symbol);
        	ev.setText(R.string.default_value_in_celsius);
        }
        
        
    	return new  AlertDialog.Builder(this)
    	.setTitle(R.string.temperature)
    	.setView(editEntryView)
    	.setPositiveButton(R.string.save,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
		        EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
				String floatValue = ev.getText().toString();
				ContentValues values = new ContentValues();
				values.put(Record.PROFILEPK, mProfilePK);
				values.put(Record.DATE, mMillis/1000);
				values.put(Record.TYPE, Utils.RECORD_TYPE_BMT);
				
				if(!mLocalTemperatureScale.equals(mTemperatureScale)){
					if(mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE)){
						floatValue = Utils.fahrenheitToCelsius(floatValue);
					}else if (mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE)){
						floatValue = Utils.celsiusToFahrenheit(floatValue);
					}
				}
				values.put(Record.FLOATVALUE, floatValue);
				mContentResolver.insert(Record.CONTENT_URI,values);
				//finish();
				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
			}
    		
    	})
    	.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				finish();
			}
    		
    	})
    	.create();
    }
  
    private Dialog createNoteAddDialog(){
    	
        LayoutInflater factory = LayoutInflater.from(this);
        final View editEntryView = factory.inflate(R.layout.alert_dialog_note_entry, null);
        
        final EditText ev = (EditText)editEntryView.findViewById(R.id.note_value);
        
    	return new  AlertDialog.Builder(this)
    	.setTitle(R.string.note)
    	.setView(editEntryView)
    	.setPositiveButton(R.string.save,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
		        //EditText ev = (EditText)editEntryView.findViewById(R.id.note_value);
				String stringValue = ev.getText().toString();
				ContentValues values = new ContentValues();
				values.put(Record.PROFILEPK, mProfilePK);
				values.put(Record.DATE, mMillis/1000);
				values.put(Record.TYPE, Utils.RECORD_TYPE_NOTE);
				values.put(Record.STRINGVALUE, stringValue);
				mContentResolver.insert(Record.CONTENT_URI,values);
				//finish();
				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
			}
    		
    	})
    	.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				finish();
			}
    		
    	})
    	.create();
    }
    
    private Dialog createWeightAddDialog(){
    	
        LayoutInflater factory = LayoutInflater.from(this);
        final View editEntryView = factory.inflate(R.layout.alert_dialog_edit_entry, null);
        
        final RadioGroup  scale = (RadioGroup)editEntryView.findViewById(R.id.scale);
        final RadioButton unit1 = (RadioButton)editEntryView.findViewById(R.id.unit1);
        final RadioButton unit2 = (RadioButton)editEntryView.findViewById(R.id.unit2);
        final EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
        final TextView tv = (TextView)editEntryView.findViewById(R.id.value_view);
        
        unit1.setText(R.string.kilogram);
        unit2.setText(R.string.pound);
        
        scale.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == unit1.getId()){
					tv.setText(R.string.kg);
					ev.setText(Utils.poundToKg(ev.getText().toString()));
					mLocalWeightScale = Utils.CONFIG_WEIGHT_KILOGRAM_SCALE;
				}else if (checkedId == unit2.getId()){
					mLocalTemperatureScale = Utils.CONFIG_WEIGHT_POUND_SCALE;
					tv.setText(R.string.lb);
					ev.setText(Utils.kgToPound(ev.getText().toString()));
					//ev.setText(R.string.default_value_in_fahrenheit);
				}
			}
        	
        });
        
        if(mWeightScale.equals(Utils.CONFIG_WEIGHT_KILOGRAM_SCALE)){
        	scale.check(unit2.getId());
        	tv.setText(R.string.kg);
        }else if (mWeightScale.equals(Utils.CONFIG_WEIGHT_POUND_SCALE)){
        	scale.check(unit1.getId());
        	tv.setText(R.string.lb);
        }
        
        
    	return new  AlertDialog.Builder(this)
    	.setTitle(R.string.weight)
    	.setView(editEntryView)
    	.setPositiveButton(R.string.save,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
		        EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
				String floatValue = ev.getText().toString();
				ContentValues values = new ContentValues();
				values.put(Record.PROFILEPK, mProfilePK);
				values.put(Record.DATE, mMillis/1000);
				values.put(Record.TYPE, Utils.RECORD_TYPE_WEIGHT);
				
				if(!mLocalWeightScale.equals(mWeightScale)){
					if(mWeightScale.equals(Utils.CONFIG_WEIGHT_KILOGRAM_SCALE)){
						floatValue = Utils.poundToKg(floatValue);
					}else if (mWeightScale.equals(Utils.CONFIG_WEIGHT_POUND_SCALE)){
						floatValue = Utils.celsiusToFahrenheit(floatValue);
					}
				}
				values.put(Record.FLOATVALUE, floatValue);
				mContentResolver.insert(Record.CONTENT_URI,values);
				//finish();
				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
			}
    		
    	})
    	.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				finish();
			}
    		
    	})
    	.create();
    }
    
    private Dialog createWeightRemoveDialog(){
    	
        LayoutInflater factory = LayoutInflater.from(this);
        final View editEntryView = factory.inflate(R.layout.alert_dialog_edit_entry, null);
        
        final RadioGroup  scale = (RadioGroup)editEntryView.findViewById(R.id.scale);
        final RadioButton unit1 = (RadioButton)editEntryView.findViewById(R.id.unit1);
        final RadioButton unit2 = (RadioButton)editEntryView.findViewById(R.id.unit2);
        final EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
        final TextView tv = (TextView)editEntryView.findViewById(R.id.value_view);
    
        unit1.setText(R.string.kilogram);
        unit2.setText(R.string.pound);
        
        
        scale.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == unit1.getId()){
					tv.setText(R.string.kg);
					ev.setText(Utils.poundToKg(ev.getText().toString()));
					mLocalWeightScale = Utils.CONFIG_WEIGHT_KILOGRAM_SCALE;
				}else if (checkedId == unit2.getId()){
					mLocalWeightScale = Utils.CONFIG_WEIGHT_POUND_SCALE;
					tv.setText(R.string.lb);
					ev.setText(Utils.kgToPound(ev.getText().toString()));
				}
			}
        	
        });
        
        if(mWeightScale.equals(Utils.CONFIG_WEIGHT_KILOGRAM_SCALE)){
        	scale.check(unit2.getId());
        	tv.setText(R.string.kg);
        	
        }else if (mWeightScale.equals(Utils.CONFIG_WEIGHT_POUND_SCALE)){
        	scale.check(unit1.getId());
        	tv.setText(R.string.lb);
        }
        ev.setText(String.valueOf(getWeightValue()));
        
    	return new  AlertDialog.Builder(this)
    	.setTitle(R.string.weight)
    	.setView(editEntryView)
    	.setPositiveButton(R.string.save,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
		        EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
				String floatValue = ev.getText().toString();
				ContentValues values = new ContentValues();
				values.put(Record.PROFILEPK, mProfilePK);
				values.put(Record.DATE, mMillis/1000);
				values.put(Record.TYPE, Utils.RECORD_TYPE_WEIGHT);

				
				if(!mLocalWeightScale.equals(mWeightScale)){
					if(mWeightScale.equals(Utils.CONFIG_WEIGHT_KILOGRAM_SCALE)){
						//floatValue = Utils.celsiusToK(Utils.fahrenheitToCelsius(floatValue));
						floatValue = Utils.fahrenheitToCelsius(floatValue);
					}else if (mWeightScale.equals(Utils.CONFIG_WEIGHT_POUND_SCALE)){
						floatValue = Utils.celsiusToFahrenheit(floatValue);
					}
				}
				values.put(Record.FLOATVALUE, floatValue);
				mContentResolver.insert(Record.CONTENT_URI,values);
				//finish();
				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
			}
    		
    	})
    	.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				finish();
			}
    		
    	})
    	.setNeutralButton(R.string.remove, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				mContentResolver.delete(Record.CONTENT_URI,Record.DATE + "=? AND " + Record.TYPE+ "=?", 
						new String[]{String.valueOf(mMillis/1000), Utils.RECORD_TYPE_WEIGHT});
				//finish();
				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
			}
    		
    	})
    	.create();
    }
    
    private Dialog createNoteRemoveDialog(){
    	
        LayoutInflater factory = LayoutInflater.from(this);
        final View editEntryView = factory.inflate(R.layout.alert_dialog_note_entry, null);
        final EditText ev = (EditText)editEntryView.findViewById(R.id.note_value);
        
        ev.setText(String.valueOf(getNoteValue()));
        
    	return new  AlertDialog.Builder(this)
    	.setTitle(R.string.note)
    	.setView(editEntryView)
    	.setPositiveButton(R.string.save,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				String floatValue = ev.getText().toString();
				String where = Record.TYPE + "=?" + " AND " + Record.DATE + "=?";
				
				ContentValues values = new ContentValues();
				
				values.put(Record.PROFILEPK, mProfilePK);
				values.put(Record.DATE, mMillis/1000);
				values.put(Record.TYPE, Utils.RECORD_TYPE_NOTE);
				values.put(Record.STRINGVALUE, floatValue);

				mContentResolver.update(Record.CONTENT_URI,values,where, 
										new String[]{Utils.RECORD_TYPE_NOTE,String.valueOf(mMillis/1000)});
				//finish();
				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
			}
    		
    	})
    	.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				finish();
			}
    		
    	})
    	.setNeutralButton(R.string.remove, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				mContentResolver.delete(Record.CONTENT_URI,Record.DATE + "=? AND " + Record.TYPE+ "=?", 
						new String[]{String.valueOf(mMillis/1000), Utils.RECORD_TYPE_NOTE});
				//finish();
				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
			}
    		
    	})
    	.create();
    }    
    
    private Dialog createBmtRemoveDialog(){
    	
        LayoutInflater factory = LayoutInflater.from(this);
        final View editEntryView = factory.inflate(R.layout.alert_dialog_edit_entry, null);
        
        final RadioGroup  scale = (RadioGroup)editEntryView.findViewById(R.id.scale);
        final RadioButton unit1 = (RadioButton)editEntryView.findViewById(R.id.unit1);
        final RadioButton unit2 = (RadioButton)editEntryView.findViewById(R.id.unit2);
        final EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
        final TextView tv = (TextView)editEntryView.findViewById(R.id.value_view);
        
        unit1.setText(R.string.celsius);
        unit2.setText(R.string.fahrenheit);
        
        scale.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == unit1.getId()){
					tv.setText(R.string.celsius_symbol);
					ev.setText(Utils.fahrenheitToCelsius(ev.getText().toString()));
					mLocalTemperatureScale = Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE;
				}else if (checkedId == unit2.getId()){
					mLocalTemperatureScale = Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE;
					tv.setText(R.string.fahrenheit_symbol);
					ev.setText(Utils.celsiusToFahrenheit(ev.getText().toString()));
				}
			}
        	
        });
        
        if(mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE)){
        	scale.check(unit2.getId());
        	tv.setText(R.string.fahrenheit_symbol);
        	
        }else if (mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE)){
        	scale.check(unit1.getId());
        	tv.setText(R.string.celsius_symbol);
        }
        ev.setText(String.valueOf(getBmtValue()));
        
    	return new  AlertDialog.Builder(this)
    	.setTitle(R.string.temperature)
    	.setView(editEntryView)
    	.setPositiveButton(R.string.save,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
		        EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
				String floatValue = ev.getText().toString();
				ContentValues values = new ContentValues();
				values.put(Record.PROFILEPK, mProfilePK);
				values.put(Record.DATE, mMillis/1000);
				values.put(Record.TYPE, Utils.RECORD_TYPE_BMT);

				
				if(!mLocalTemperatureScale.equals(mTemperatureScale)){
					if(mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE)){
						//floatValue = Utils.celsiusToK(Utils.fahrenheitToCelsius(floatValue));
						floatValue = Utils.fahrenheitToCelsius(floatValue);
					}else if (mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE)){
						floatValue = Utils.celsiusToFahrenheit(floatValue);
					}
				}
				values.put(Record.FLOATVALUE, floatValue);
				mContentResolver.insert(Record.CONTENT_URI,values);
				//finish();
				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
			}
    		
    	})
    	.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				finish();
			}
    		
    	})
    	.setNeutralButton(R.string.remove, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// TODO Auto-generated method stub
				mContentResolver.delete(Record.CONTENT_URI,Record.DATE + "=? AND " + Record.TYPE+ "=?", 
						new String[]{String.valueOf(mMillis/1000), Utils.RECORD_TYPE_BMT});
				//finish();
				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
			}
    		
    	})
    	.create();
    }
    
    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ADD_BMT:
                return createBmtAddDialog();
            case DIALOG_REMOVE_BMT:
            	return createBmtRemoveDialog();
            case DIALOG_ADD_WEIGHT:
            	return createWeightAddDialog();
            case DIALOG_REMOVE_WEIGHT:
            	return createWeightRemoveDialog();
            case DIALOG_ADD_NOTE:
            	return createNoteAddDialog();
            case DIALOG_REMOVE_NOTE:
            	return createNoteRemoveDialog();            	
        }
        return super.onCreateDialog(id);
    }
    
    private void getConfig(){
    	Cursor cursor = mContentResolver.query(Config.CONTENT_URI,null,null,null,null);
    	if( cursor != null){
    		cursor.moveToFirst();
    		if(cursor.getCount() != 0){
	    		mTemperatureScale = cursor.getString(cursor.getColumnIndex(Config.TEMPERATURESCALE));
	    		mLocalTemperatureScale = mTemperatureScale;
	    		mWeightScale  = cursor.getString(cursor.getColumnIndex(Config.WEIGHTSCALE));
	    		mLocalWeightScale = mWeightScale;
	    		cursor.close();
    		}else{
    			mTemperatureScale = Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE;
	    		mLocalTemperatureScale = mTemperatureScale;
	    		mWeightScale  = Utils.CONFIG_WEIGHT_KILOGRAM_SCALE;
	    		mLocalWeightScale = mWeightScale;
    		}
    	}
    }
}
