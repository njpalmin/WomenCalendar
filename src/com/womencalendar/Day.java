package com.womencalendar;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

import com.womencalendar.utils.Utils;

public class Day {
    public Time TIME;
    public int  DAYTYPE;
    public int  DAYNOTIFICATION;
    public int  mPeriodDay;
    public float weight;
    public float bmt;
    public String note;
    
    public String getDate(){
        return TIME.format("%Y%m%d");
    }
    
    public Day() {
        DAYTYPE = Utils.DAY_TYPE_NORMAL;
        DAYNOTIFICATION = 0;
    }
    
    public String toString() {
        return getDate();
    }

}
