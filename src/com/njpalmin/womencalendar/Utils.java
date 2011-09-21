package com.njpalmin.womencalendar;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

public class Utils {
    private static final boolean DEBUG = true;
    private static final String TAG = "CalUtils";
    
    public static final String EVENT_BEGIN_TIME = "beginTime";
    public static final String DAY_TYPE = "dayType";
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
    
    
    public static enum DayType{
    	NORMAL_DAY,
    	START_DAY,
    	MIDDLE_DAY,
    	END_DAY,
    	FERTILITY_DAY,
    	OVULATION_DAY,
    	PERIOD_FORECAST_DAY
    }
    
    public final static int DAY_TYPE_NORMAL_DAY = 1;
    public final static int DAY_TYPE_START_DAY = 2;
    public final static int DAY_TYPE_MIDDLE_DAY = 3;
    public final static int DAY_TYPE_END_DAY= 4;
    public final static int DAY_TYPE_FERTILITY_DAY = 5;
    public final static int DAY_TYPE_OVULATION_DAY = 6;
    public final static int DAY_TYPE_FORECAST_DAY = 7;
    
    
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
    
    public static int calculateOvulationStartPeriod(int cycleLength){
    	return cycleLength - 18;
    }
    
    public static int calculateOvulationEndPeriod(int cycleLength){
    	return cycleLength - 11;
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
}