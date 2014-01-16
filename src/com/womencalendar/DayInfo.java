package com.womencalendar;

public class DayInfo {
	public long mDate;
	public long mIntValue;
	public String mType;
	public String mStringValue;
	public float mFloatValue;
	
	
	public DayInfo(long date, String type, long intValue, String stringValue, float floatValue){
		mDate = date;
		mType = type;
		mIntValue = intValue;
		mStringValue = stringValue;
		mFloatValue = floatValue;
	}
	
	
}
