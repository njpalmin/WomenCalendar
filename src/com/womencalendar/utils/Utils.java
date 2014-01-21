package com.womencalendar.utils;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.util.Log;

import com.womencalendar.Day;
import com.womencalendar.R;

public class Utils {
    private static final boolean DEBUG = true;
    private static final String TAG = "CalUtils";
    
    public static final String SHARED_PREF_CYCLE_LENGTH = "cycle_legnth";
    public static final String SHARED_PREF_PERIOD_LENGTH = "period_legnth";
    public static final String SHARED_PREF_START_DAY = "start_day";
    public static final String SHARED_PREF_LOCALE = "locale";
    
    public static final String EVENT_BEGIN_TIME = "beginTime";
    public static final String EVENT_DAY_TYPE = "dayType";
    public static final String EVENT_NOTIFICATION = "notification";
    public static final String EXTRAS_SELECTED_DAY = "extras_selected_day";
    
    public static final String EVENT_ROW = "row";
    public static final String EVENT_COLUMN = "column";
    public static final String OPERATION ="operation";
    public static final String OPERATION_ADD_PARAMETER="add";
    public static final String OPERATION_REMOVE_PARAMETER="remove";
    
    public static final int LUTEAL_PHASE_LENGTH = 14;
    public final static int CYCLE_LENGTH = 28;
    public final static int PERIOD_LENGTH = 4;
    
    public final static String RECORD_TYPE_START = "start";
    public final static String RECORD_TYPE_END = "end";
    public final static String RECORD_TYPE_PILL = "pill";
    public final static String RECORD_TYPE_SEX = "sex";
    public final static String RECORD_TYPE_BMT = "bmt";
    public final static String RECORD_TYPE_WEIGHT = "weight";
    public final static String RECORD_TYPE_NOTE = "note";
    
    public final static String CONFIG_TEMPERATURE_FAHRENHEIT_SCALE= "FAHRENHEIT";
    public final static String CONFIG_TEMPERATURE_CELSIUS_SCALE= "CELSIUS";
    public final static String CONFIG_WEIGHT_KILOGRAM_SCALE= "KILOGRAM";
    public final static String CONFIG_WEIGHT_POUND_SCALE= "POUND";
    
	public final static int DAY_TYPE_NORMAL = 1;
	public final static int DAY_TYPE_START = 2;
	public final static int DAY_TYPE_IN_PERIOD = 3;
	public final static int DAY_TYPE_END = 4;
	public final static int DAY_TYPE_FERTILITY = 5;
	public final static int DAY_TYPE_OVULATION = 6;
    
	public final static int NOTIFICATION_TYPE_START = 1<<0;
	public final static int NOTIFICATION_TYPE_PILL = 1<<1;
	public final static int NOTIFICATION_TYPE_BMT = 1<<2;
	public final static int NOTIFICATION_TYPE_NOTE = 1<<3;
	public final static int NOTIFICATION_TYPE_SEX = 1<<4;
	public final static int NOTIFICATION_TYPE_WEIGHT = 1<<5;
	
	public final static int DAY_IN_MILLIS = 86400000;
	public final static int DAY_IN_SECONDS = DAY_IN_MILLIS / 1000;
	
    public static enum DayType{
    	NORMAL_DAY,
    	START_DAY,
    	MIDDLE_DAY,
    	END_DAY,
    	FERTILITY_DAY,
    	OVULATION_DAY,
    	PERIOD_FORECAST_DAY
    }
    
//    public final static int DAY_TYPE_NORMAL_DAY = 1;
//    public final static int DAY_TYPE_START_DAY = 2;
//    public final static int DAY_TYPE_MIDDLE_DAY = 3;
//    public final static int DAY_TYPE_END_DAY= 4;
//    public final static int DAY_TYPE_FERTILITY_DAY = 5;
//    public final static int DAY_TYPE_OVULATION_DAY = 6;
//    public final static int DAY_TYPE_FORECAST_DAY = 7;
//    
    
    public static void startActivity(Context context, String className, long time) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setClassName(context, className);
        intent.putExtra(EVENT_BEGIN_TIME, time);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }
    public static boolean isSunday(int column, int firstDayOfWeek) {
        return (firstDayOfWeek == Time.SUNDAY && column == 0)
            || (firstDayOfWeek == Time.MONDAY && column == 6)
            || (firstDayOfWeek == Time.SATURDAY && column == 1);
    }
    
    public static boolean isSaturday(int column, int firstDayOfWeek) {
        return (firstDayOfWeek == Time.SUNDAY && column == 6)
            || (firstDayOfWeek == Time.MONDAY && column == 5)
            || (firstDayOfWeek == Time.SATURDAY && column == 0);
    }
    public static String formatMonthYear(Context context, Time time) {
        return time.format(context.getResources().getString(R.string.month_year));
    }
    
    public static String formatDate(Context context, Time time) {
        return time.format(context.getResources().getString(R.string.date));
    }
    
    public static int getFirstDayOfWeek() {
        int startDay = Calendar.getInstance().getFirstDayOfWeek();
        if (startDay == Calendar.SATURDAY) {
            return Time.SATURDAY;
        } else if (startDay == Calendar.MONDAY) {
            return Time.MONDAY;
        } else {
            return Time.SUNDAY;
        }
    }
    
    public static int calculateFertilityStartPeriod(int cycleLength){
    	return cycleLength - 18;
    }
    
    public static int calculateFertilityEndPeriod(int cycleLength){
    	return cycleLength - 11;
    }
    
    public static boolean isInOvulation(Context context, Day startDay, Day today){
        
        if(today.TIME.toMillis(true) >= (startDay.TIME.toMillis(true) + (Utils.getCycleLength(context) - 18) * Utils.DAY_IN_MILLIS) 
        && today.TIME.toMillis(true) <= (startDay.TIME.toMillis(true) + (Utils.getCycleLength(context) - 11) * Utils.DAY_IN_MILLIS)){
            return true;
            
        }
        
        return false;
    }
    
    public static Time formatDateToTime(int date){
    	String str = String.valueOf(date);
    	Time time = new Time();
    	
    	int year = Integer.parseInt(str.substring(0,3));
    	int month = Integer.parseInt(str.substring(4,5));
    	int day = Integer.parseInt(str.substring(6,7));
    	
    	time.set(day,month,year);
    	time.normalize(true);
    	
    	return time;
    }
    
    public static int getDaysOfMonth(Time time){
    	switch(time.month){
    		case 0:
    		case 1:
    		case 3:
    		case 5:
    		case 7:
    		case 8:
    		case 10:
    			return 31;
    		case 4:
    		case 6:
    		case 9:
    		case 11:
    			return 30;
    		case 2:
    		{
    			if(time.yearDay == 365){
    				return 29;
    			}else{
    				return 28;
    			}
    		}
    		default:
    			return 30;
    	}
    }
    public static String celsiusToFahrenheit(String celsius){
    	if(celsius.equals("")){
    		return String.valueOf(0.0f);
    	}
    	return String.valueOf(32.00f+ 1.8f * Float.valueOf(celsius).floatValue());
    }
    
    public static String fahrenheitToCelsius(String fahrenheit){
    	if(fahrenheit.equals("")){
    		return String.valueOf(0.0f);
    	}
    	
    	return String.valueOf((Float.valueOf(fahrenheit).floatValue()-32.00f)/1.8f);
    }

    public static String celsiusToK(String celsius){
    	if(celsius.equals("")){
    		return String.valueOf(0.0f);
    	}
    	return String.valueOf(273.15f +  Float.valueOf(celsius).floatValue());
    }
    
    public static String kToCelsius(String k){
    	if(k.equals("")){
    		return String.valueOf(0.0f);
    	}
    	return  String.valueOf( Float.valueOf(k).floatValue()- 273.15f);
    }

    public static String kgToPound(String kg){
    	return  String.valueOf(Float.valueOf(kg).floatValue()/2.2046f);
    }
    
    public static String poundToKg(String pound){
    	return  String.valueOf(Float.valueOf(pound).floatValue() * 2.2046f);
    }
    
    public static int getCycleLength(Context context){
        SharedPreferences prefs = context.getSharedPreferences(null, Context.MODE_PRIVATE);
        return prefs.getInt(Utils.SHARED_PREF_CYCLE_LENGTH, 28);
    }

    public static int getPeriodLength(Context context){
        SharedPreferences prefs = context.getSharedPreferences(null, Context.MODE_PRIVATE);
        return prefs.getInt(Utils.SHARED_PREF_PERIOD_LENGTH, 4);
    }
    
    public static int getStartDayOfWeek(Context context){
        SharedPreferences prefs = context.getSharedPreferences(null, Context.MODE_PRIVATE);
        return prefs.getInt(Utils.SHARED_PREF_START_DAY, 0);
    }
    
    public static int getYearFromDate(String date){
        return Integer.parseInt(date.substring(0, 4));
    }
    
    public static int getMonthFromDate(String date){
        return Integer.parseInt(date.substring(4,6));
    }    
    
    public static int getMonthOfDayFromDate(String date){
        return Integer.parseInt(date.substring(6));
    }
    
    
    
    
}