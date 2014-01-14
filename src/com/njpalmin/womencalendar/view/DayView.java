package com.njpalmin.womencalendar.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.njpalmin.womencalendar.DayOfMonthCursor;
import com.njpalmin.womencalendar.R;

public class DayView extends RelativeLayout{
    TextView mDate;
    TextView mPeriodDay;
    ImageView mSex,mPill,mNote,mMood,mOther;
    TextView mWeight,mTemperature;
    
    private boolean mWithinCurrentMonth;
    private boolean mIsToday;
    private int mDay;
    
    public DayView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public DayView(Context context, int row, int column, DayOfMonthCursor cursor) {
        super(context);
        // TODO Auto-generated constructor stub
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day, this, true);
        mDate = (TextView)findViewById(R.id.date);
        mPeriodDay = (TextView)findViewById(R.id.period_day);
        mSex = (ImageView)findViewById(R.id.sex_notification);
        mPill = (ImageView)findViewById(R.id.pill_notification);
        mMood = (ImageView)findViewById(R.id.mood_notification);
        mOther = (ImageView)findViewById(R.id.others_notification);
        
        mDay = cursor.getDateAt(row, column);
        mWithinCurrentMonth = cursor.isWithinCurrentMonth(row, column);
    }

    private void initView(){
        
    }
}
