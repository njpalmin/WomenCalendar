package com.womencalendar;

import com.womencalendar.utils.Utils;

import android.text.format.Time;

public class Day {
    public Time TIME;
    public int  DAYTYPE;
    public int  DAYNOTIFICATION;
    public int  mPeriodDay;
//    public Day  NextDay;
    
    public String getDate(){
        return TIME.format("%Y%m%d");
    }
    
    public Day() {
        DAYTYPE = Utils.DAY_TYPE_NORMAL;
    }
    
    public String toString() {
        return getDate();
    }
}
