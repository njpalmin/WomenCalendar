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
	
    private ImageView mPreDay;
    private ImageView mNextDay;
    
    private LinearLayout mCervicalMucus;
    
    
    private Button mRemoveAll;
    private Button mBack;
    private String mTemperatureScale;
    private String mWeightScale;
    private String mLocalTemperatureScale;
    private String mLocalWeightScale;
    
    private ContentResolver mContentResolver;
    private long mProfilePK = -1;
    private String mDate;
    private Day mDay;
    
	@Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mContext = this;
		setContentView(R.layout.womencalendarday_main);
		setRequestedOrientation(LinearLayout.VERTICAL);
		
		mDate = getIntent().getStringExtra(Utils.EXTRAS_SELECTED_DAY);
		mDay = Utils.getDay(mContext, mDate);
		
		mContentResolver = getContentResolver();
		getConfig();
		initView();
	}
	
	void initView(){
		initHeaderView();
		
		//init buttons
		LinearLayout startPeriod =(LinearLayout)findViewById(R.id.start_period);
		LinearLayout endPeriod =(LinearLayout)findViewById(R.id.end_period);
		LinearLayout pill = (LinearLayout)findViewById(R.id.day_pill);
		LinearLayout sex  = (LinearLayout)findViewById(R.id.day_sex);
		LinearLayout mood = (LinearLayout)findViewById(R.id.day_mood);
		LinearLayout bmt  = (LinearLayout)findViewById(R.id.day_bmt);
		LinearLayout note = (LinearLayout)findViewById(R.id.day_note);
		LinearLayout weight = (LinearLayout)findViewById(R.id.day_weight);
		
        TextView startAdd = (TextView)findViewById(R.id.day_start_period_add);
        TextView endAdd = (TextView)findViewById(R.id.day_end_period_add);
        TextView pillAdd = (TextView)findViewById(R.id.day_pill_add);
        TextView sexAdd = (TextView)findViewById(R.id.day_sex_add);
        TextView bmtAdd = (TextView)findViewById(R.id.day_bmt_add);
        TextView weightAdd = (TextView)findViewById(R.id.day_weight_add);
        TextView noteAdd = (TextView)findViewById(R.id.day_note_add);
          
        if(mDay.mPeriodDay >1 ){
            endPeriod.setEnabled(true);
            endPeriod.setClickable(true);
            endAdd.setVisibility(View.VISIBLE);
            endAdd.setText(R.string.add);
        }
        
        if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_SEX) != 0) {
            sexAdd.setText(R.string.remove);
        }
        if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_PILL) != 0) {
            pillAdd.setText(R.string.remove);
        }
        if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_BMT) !=  0) {
            bmtAdd.setText(R.string.edit_remove);
        }
        if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_NOTE) !=  0) {
            noteAdd.setText(R.string.edit_remove);
        }
        if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_WEIGHT) != 0) {
            weightAdd.setText(R.string.edit_remove);
        }
        
		switch(mDay.DAYTYPE){
            case Utils.DAY_TYPE_START:  
                startAdd = (TextView)findViewById(R.id.day_start_period_add);
                startAdd.setText(getString(R.string.remove)); 
                endAdd.setVisibility(View.GONE);
                break;
            case Utils.DAY_TYPE_END:
                endAdd = (TextView)findViewById(R.id.day_end_period_add);
                endAdd.setText(getString(R.string.remove)); 
                startAdd.setVisibility(View.GONE);
                startPeriod.setEnabled(false);
                startPeriod.setClickable(false);
                break;
            case Utils.DAY_TYPE_IN_PERIOD:
                endAdd = (TextView)findViewById(R.id.day_end_period_add);
                endAdd.setText(getString(R.string.add));
                startAdd.setVisibility(View.GONE);
                startPeriod.setEnabled(false);
                startPeriod.setClickable(false);
                break;

		}

    	mPreDay= (ImageView)findViewById(R.id.prev);
    	mPreDay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDay.TIME.monthDay -= 1;
                mDay.TIME.normalize(true);
            	goToDay(mDay.TIME);
            }
        });
    	
    	mNextDay = (ImageView)findViewById(R.id.next);
    	mNextDay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mDay.TIME.monthDay += 1;
            	mDay.TIME.normalize(true);
            	goToDay(mDay.TIME);
            }
        });
		
    	startPeriod.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Log.d(TAG,"Start onClick");
				TextView sAdd = (TextView)findViewById(R.id.day_start_period_add);
				if(sAdd.getText().toString().equals(mContext.getResources().getString(R.string.add))){
				    ContentValues values = new ContentValues();
	                values.put(Record.PROFILEPK, 1);
	                values.put(Record.DATE, Integer.parseInt(mDay.getDate()));
	                values.put(Record.TYPE, Utils.RECORD_TYPE_START);
	                values.put(Record.INTVALUE, Utils.getPeriodLength(mContext));
	                mContentResolver.insert(Record.CONTENT_URI, values);    
				}else if(sAdd.getText().toString().equals(mContext.getResources().getString(R.string.remove))){
				    mContentResolver.delete(Record.CONTENT_URI, "date=?", new String[]{mDay.getDate()});
				}
				finish();
			}
		});

    	endPeriod.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Log.d(TAG,"end onClick");
				TextView eAdd = (TextView)findViewById(R.id.day_end_period_add);
				if(eAdd.getText().toString().equals(mContext.getResources().getString(R.string.add))){
				    Day startDay = Utils.getLastStartDay(mContext, mDay);
				    int length = (int)((mDay.TIME.toMillis(true) - startDay.TIME.toMillis(true)) / Utils.DAY_IN_MILLIS)+1;
				    ContentValues values = new ContentValues();
				    values.put(Record.INTVALUE, length);
				    mContentResolver.update(Record.CONTENT_URI, values, "date=?", new String[]{startDay.getDate()});
				}else if(eAdd.getText().toString().equals(mContext.getResources().getString(R.string.remove))){
				    Day startDay = Utils.getLastStartDay(mContext, mDay);
                    int length = (int)((mDay.TIME.toMillis(true) - startDay.TIME.toMillis(true)) / Utils.DAY_IN_MILLIS);
                    ContentValues values = new ContentValues();
                    values.put(Record.INTVALUE, length);
                    mContentResolver.update(Record.CONTENT_URI, values, "date=?", new String[]{startDay.getDate()}); 
				}
				finish();
			}
		});
		
		pill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView add = (TextView)findViewById(R.id.day_pill_add);
                if(add.getText().toString().equals(mContext.getResources().getString(R.string.add))){
                    ContentValues values = new ContentValues();
                    values.put(Record.PROFILEPK, 1);
                    values.put(Record.DATE, Integer.parseInt(mDay.getDate()));
                    values.put(Record.TYPE, Utils.RECORD_TYPE_PILL);
                    mContentResolver.insert(Record.CONTENT_URI, values);
                }else if(add.getText().toString().equals(mContext.getResources().getString(R.string.remove))){
                    mContentResolver.delete(Record.CONTENT_URI, "date=? and type=?", new String[]{mDay.getDate(),Utils.RECORD_TYPE_PILL});
                }
                finish();
            }
        });

        sex.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView add = (TextView)findViewById(R.id.day_sex_add);
                if(add.getText().toString().equals(mContext.getResources().getString(R.string.add))){
                    ContentValues values = new ContentValues();
                    values.put(Record.PROFILEPK, 1);
                    values.put(Record.DATE, Integer.parseInt(mDay.getDate()));
                    values.put(Record.TYPE, Utils.RECORD_TYPE_SEX);
                    mContentResolver.insert(Record.CONTENT_URI, values);
                }else if(add.getText().toString().equals(mContext.getResources().getString(R.string.remove))){
                    mContentResolver.delete(Record.CONTENT_URI, "date=? and type=?", new String[]{mDay.getDate(),Utils.RECORD_TYPE_SEX});
                }
                finish();          
            }
        });
        

        mood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //finish();
            }
        });
		
		bmt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_BMT) != 0){
            		showDialog(DIALOG_REMOVE_BMT);
            	}else {
            		showDialog(DIALOG_ADD_BMT);
            	}
            }
        });
		
		note.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//finish();
            	if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_NOTE) != 0){
            		showDialog(DIALOG_REMOVE_NOTE);
            	}else{
            		showDialog(DIALOG_ADD_NOTE);
            	}
            }
        });
		

		weight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//finish();
            	if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_WEIGHT) != 0){
            		showDialog(DIALOG_REMOVE_WEIGHT);
            	}else{
            		showDialog(DIALOG_ADD_WEIGHT);
            	}
            }
        });
		
		mCervicalMucus = (LinearLayout)findViewById(R.id.day_cervical_mucus);
		mCervicalMucus.setOnClickListener(new View.OnClickListener() {
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
        updateTitle(mDay.TIME);
    	LinearLayout dayInfoLayout = (LinearLayout)findViewById(R.id.date_info_layout);
    	
    	if(dayInfoLayout != null){
    		dayInfoLayout.removeAllViews();
    		dayInfoLayout.setVisibility(View.VISIBLE);
    		
    		TextView paramsDescription = (TextView)findViewById(R.id.parameter_description_text_view);
    		TextView noParameter = (TextView)findViewById(R.id.no_parameters_label_view);
    		
    		switch (mDay.DAYTYPE){
    			case Utils.DAY_TYPE_START:
    			case Utils.DAY_TYPE_IN_PERIOD:
    			    ImageView startPeriod = new ImageView(this);
    			    startPeriod.setImageResource(R.drawable.day_start_period);
    			    dayInfoLayout.addView(startPeriod); 
    			    break;
    			case Utils.DAY_TYPE_END:
    			    ImageView endPeriod = new ImageView(this);
    			    endPeriod.setImageResource(R.drawable.day_end_period);
    			    dayInfoLayout.addView(endPeriod);
    			    break;
                case Utils.DAY_TYPE_FERTILITY:
                    ImageView fertilityDay = new ImageView(this);
                    fertilityDay.setImageResource(R.drawable.day_fertility);
                    dayInfoLayout.addView(fertilityDay);
                    paramsDescription.setVisibility(View.VISIBLE);
                    paramsDescription.setText(getString(R.string.fertility_forecast));
                    noParameter.setVisibility(View.GONE);
                break;
                case Utils.DAY_TYPE_OVULATION:
                    ImageView ovulationDay = new ImageView(this);
                    ovulationDay.setImageResource(R.drawable.day_ovulation);
                    dayInfoLayout.addView(ovulationDay);
                    paramsDescription.setVisibility(View.VISIBLE);
                    paramsDescription.setText(getString(R.string.ovulation_forecast));
                    noParameter.setVisibility(View.GONE);
                    break;
                case Utils.DAY_TYPE_PREDICT:
                    ImageView predictDay = new ImageView(this);
                    predictDay.setImageResource(R.drawable.day_start_period_forecast);
                    dayInfoLayout.addView(predictDay);
                    paramsDescription.setVisibility(View.VISIBLE);
                    paramsDescription.setText(getString(R.string.automatic_forecast));
                    noParameter.setVisibility(View.GONE);
                    break;                    
    		}
    		
    		if(mDay.DAYNOTIFICATION != 0){
    		    noParameter.setVisibility(View.GONE);;
    		}
    		
    		if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_PILL) != 0){
				ImageView pill = new ImageView(this);
				pill.setImageResource(R.drawable.day_pill);
	    		dayInfoLayout.addView(pill);
    		}
    		
    		if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_SEX) != 0){
				ImageView sex = new ImageView(this);
				sex.setImageResource(R.drawable.day_sex);
	    		dayInfoLayout.addView(sex);
    		}
    		
    		if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_BMT) != 0){
    			TextView bmt = new TextView(this);
    			bmt.setText(String.valueOf(getBmtValue()));
    			dayInfoLayout.addView(bmt);
    		}
    		
    		if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_WEIGHT) != 0){
    			TextView weight = new TextView(this);
    			weight.setText(String.valueOf(getWeightValue()));
    			dayInfoLayout.addView(weight);   			
    		}
    		
    		if((mDay.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_NOTE) != 0){
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
    									new String[]{Utils.RECORD_TYPE_BMT ,mDay.getDate()},null);
    	if(c != null ){
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
    									new String[]{Utils.RECORD_TYPE_WEIGHT,mDay.getDate()},null);
    	if(c != null && c.getCount() != 0){
    		c.moveToFirst();
    		weightValue = c.getFloat(c.getColumnIndex(Record.FLOATVALUE));

    	}
        c.close();    	
    	return weightValue;
    }
    
    private String getNoteValue(){
    	String noteValue = "";
    	String selection = Record.TYPE + "=?" + " AND " + Record.DATE + "=?";
    	Cursor c = mContentResolver.query(Record.CONTENT_URI,null,selection,
    									 new String[]{Utils.RECORD_TYPE_NOTE,mDay.getDate()},null);
    	if(c != null && c.getCount() != 0){
    		c.moveToFirst();
    		noteValue = c.getString(c.getColumnIndex(Record.STRINGVALUE));

    	}
        c.close();    	
    	return noteValue;
    }
    
    private void goToDay(Time time){
    	updateTitle(time);
    	mDay = Utils.getDay(mContext, time.format("%Y%m%d"));
    	initView();
    }
    
    private Dialog createBmtAddDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View editEntryView = factory.inflate(R.layout.alert_dialog_edit_entry, null);
        
//        final RadioGroup  scale = (RadioGroup)editEntryView.findViewById(R.id.scale);
//        final RadioButton unit1 = (RadioButton)editEntryView.findViewById(R.id.unit1);
//        final RadioButton unit2 = (RadioButton)editEntryView.findViewById(R.id.unit2);
        final EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
        final TextView tv = (TextView)editEntryView.findViewById(R.id.value_view);
        
//        unit1.setText(R.string.celsius);
//        unit2.setText(R.string.fahrenheit);
//        
//        scale.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				// TODO Auto-generated method stub
//				if(checkedId == unit1.getId()){
//					tv.setText(R.string.celsius_symbol);
//					//ev.setText(R.string.default_value_in_celsius);
//					ev.setText(Utils.fahrenheitToCelsius(ev.getText().toString()));
//					mLocalTemperatureScale = Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE;
//				}else if (checkedId == unit2.getId()){
//					mLocalTemperatureScale = Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE;
//					tv.setText(R.string.fahrenheit_symbol);
//					ev.setText(Utils.celsiusToFahrenheit(ev.getText().toString()));
//					//ev.setText(R.string.default_value_in_fahrenheit);
//				}
//			}
//        	
//        });
//        
//        if(mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE)){
//        	scale.check(unit2.getId());
//        	tv.setText(R.string.fahrenheit_symbol);
//        	ev.setText(R.string.default_value_in_fahrenheit);
//        }else if (mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE)){
//        	scale.check(unit1.getId());
//        	tv.setText(R.string.celsius_symbol);
//        	ev.setText(R.string.default_value_in_celsius);
//        }
        
        tv.setText(R.string.celsius_symbol);
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
				values.put(Record.DATE, mDay.getDate());
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
				finish(); 
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
				values.put(Record.PROFILEPK, 1);
				values.put(Record.DATE, mDay.getDate());
				values.put(Record.TYPE, Utils.RECORD_TYPE_NOTE);
				values.put(Record.STRINGVALUE, stringValue);
				mContentResolver.insert(Record.CONTENT_URI,values);
				finish();
//				setIntentAndFinish(true,  mMillis,Utils.OPERATION_ADD_PARAMETER); 
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
        
//        final RadioGroup  scale = (RadioGroup)editEntryView.findViewById(R.id.scale);
//        final RadioButton unit1 = (RadioButton)editEntryView.findViewById(R.id.unit1);
//        final RadioButton unit2 = (RadioButton)editEntryView.findViewById(R.id.unit2);
        final EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
        final TextView tv = (TextView)editEntryView.findViewById(R.id.value_view);
        
//        unit1.setText(R.string.kilogram);
//        unit2.setText(R.string.pound);
//        
//        scale.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				// TODO Auto-generated method stub
//				if(checkedId == unit1.getId()){
//					tv.setText(R.string.kg);
//					ev.setText(Utils.poundToKg(ev.getText().toString()));
//					mLocalWeightScale = Utils.CONFIG_WEIGHT_KILOGRAM_SCALE;
//				}else if (checkedId == unit2.getId()){
//					mLocalTemperatureScale = Utils.CONFIG_WEIGHT_POUND_SCALE;
//					tv.setText(R.string.lb);
//					ev.setText(Utils.kgToPound(ev.getText().toString()));
//					//ev.setText(R.string.default_value_in_fahrenheit);
//				}
//			}
//        	
//        });
//        
//        if(mWeightScale.equals(Utils.CONFIG_WEIGHT_KILOGRAM_SCALE)){
//        	scale.check(unit2.getId());
//        	tv.setText(R.string.kg);
//        }else if (mWeightScale.equals(Utils.CONFIG_WEIGHT_POUND_SCALE)){
//        	scale.check(unit1.getId());
//        	tv.setText(R.string.lb);
//        }
        tv.setText(R.string.kg);
        
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
				values.put(Record.PROFILEPK, 1);
				values.put(Record.DATE, mDay.getDate());
				values.put(Record.TYPE, Utils.RECORD_TYPE_WEIGHT);
//				
//				if(!mLocalWeightScale.equals(mWeightScale)){
//					if(mWeightScale.equals(Utils.CONFIG_WEIGHT_KILOGRAM_SCALE)){
//						floatValue = Utils.poundToKg(floatValue);
//					}else if (mWeightScale.equals(Utils.CONFIG_WEIGHT_POUND_SCALE)){
//						floatValue = Utils.celsiusToFahrenheit(floatValue);
//					}
//				}
				values.put(Record.FLOATVALUE, floatValue);
				mContentResolver.insert(Record.CONTENT_URI,values);
				finish();
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
        
//        final RadioGroup  scale = (RadioGroup)editEntryView.findViewById(R.id.scale);
//        final RadioButton unit1 = (RadioButton)editEntryView.findViewById(R.id.unit1);
//        final RadioButton unit2 = (RadioButton)editEntryView.findViewById(R.id.unit2);
        final EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
        final TextView tv = (TextView)editEntryView.findViewById(R.id.value_view);
    
//        unit1.setText(R.string.kilogram);
//        unit2.setText(R.string.pound);
//        
//        
//        scale.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				// TODO Auto-generated method stub
//				if(checkedId == unit1.getId()){
//					tv.setText(R.string.kg);
//					ev.setText(Utils.poundToKg(ev.getText().toString()));
//					mLocalWeightScale = Utils.CONFIG_WEIGHT_KILOGRAM_SCALE;
//				}else if (checkedId == unit2.getId()){
//					mLocalWeightScale = Utils.CONFIG_WEIGHT_POUND_SCALE;
//					tv.setText(R.string.lb);
//					ev.setText(Utils.kgToPound(ev.getText().toString()));
//				}
//			}
//        	
//        });
//        
//        if(mWeightScale.equals(Utils.CONFIG_WEIGHT_KILOGRAM_SCALE)){
//        	scale.check(unit2.getId());
//        	tv.setText(R.string.kg);
//        	
//        }else if (mWeightScale.equals(Utils.CONFIG_WEIGHT_POUND_SCALE)){
//        	scale.check(unit1.getId());
//        	tv.setText(R.string.lb);
//        }
        ev.setText(String.valueOf(getWeightValue()));
        tv.setText(R.string.kg);
        
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
				values.put(Record.PROFILEPK, 1);
				values.put(Record.DATE, mDay.getDate());
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
				finish(); 
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
						new String[]{mDay.getDate(), Utils.RECORD_TYPE_WEIGHT});
				finish(); 
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
				
				values.put(Record.PROFILEPK, 1);
				values.put(Record.DATE, mDay.getDate());
				values.put(Record.TYPE, Utils.RECORD_TYPE_NOTE);
				values.put(Record.STRINGVALUE, floatValue);

				mContentResolver.update(Record.CONTENT_URI,values,where, 
										new String[]{Utils.RECORD_TYPE_NOTE,mDay.getDate()});
				finish(); 
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
						new String[]{mDay.getDate(), Utils.RECORD_TYPE_NOTE});
				finish(); 
			}
    		
    	})
    	.create();
    }    
    
    private Dialog createBmtRemoveDialog(){
    	
        LayoutInflater factory = LayoutInflater.from(this);
        final View editEntryView = factory.inflate(R.layout.alert_dialog_edit_entry, null);
        
//        final RadioGroup  scale = (RadioGroup)editEntryView.findViewById(R.id.scale);
//        final RadioButton unit1 = (RadioButton)editEntryView.findViewById(R.id.unit1);
//        final RadioButton unit2 = (RadioButton)editEntryView.findViewById(R.id.unit2);
        final EditText ev = (EditText)editEntryView.findViewById(R.id.value_edit);
        final TextView tv = (TextView)editEntryView.findViewById(R.id.value_view);
        
//        unit1.setText(R.string.celsius);
//        unit2.setText(R.string.fahrenheit);
//        
//        scale.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				// TODO Auto-generated method stub
//				if(checkedId == unit1.getId()){
//					tv.setText(R.string.celsius_symbol);
//					ev.setText(Utils.fahrenheitToCelsius(ev.getText().toString()));
//					mLocalTemperatureScale = Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE;
//				}else if (checkedId == unit2.getId()){
//					mLocalTemperatureScale = Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE;
//					tv.setText(R.string.fahrenheit_symbol);
//					ev.setText(Utils.celsiusToFahrenheit(ev.getText().toString()));
//				}
//			}
//        	
//        });
        
//        if(mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_FAHRENHEIT_SCALE)){
//        	scale.check(unit2.getId());
//        	tv.setText(R.string.fahrenheit_symbol);
//        	
//        }else if (mTemperatureScale.equals(Utils.CONFIG_TEMPERATURE_CELSIUS_SCALE)){
//        	scale.check(unit1.getId());
//        	tv.setText(R.string.celsius_symbol);
//        }
        ev.setText(String.valueOf(getBmtValue()));
        tv.setText(R.string.celsius_symbol);
        
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
				values.put(Record.PROFILEPK, 1);
				values.put(Record.DATE, mDay.getDate());
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
				finish();

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
						new String[]{mDay.getDate(), Utils.RECORD_TYPE_BMT});
				finish(); 
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
