package com.womencalendar.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.womencalendar.Day;
import com.womencalendar.R;
import com.womencalendar.WomenCalendar.Record;
import com.womencalendar.WomenCalendarDayActivity;
import com.womencalendar.utils.DayOfMonthCursor;
import com.womencalendar.utils.Utils;

public class WomenCalendarViewAdapter extends BaseAdapter{
    
    private final static String TAG = "WomenCalendarViewAdapter";
    
    private static final String[] RECORD_PROJECTION = new String[] {
        Record.DATE,
        Record.TYPE,   
    };

    private Context mContext;
    private int mDay;
    private Time mToday,mTime;
    private ArrayList<Day> mCalendarDays;
    
    TextView mDayOfMonth;
    TextView mPeriodDay;
    ImageView mSex,mPill,mNote,mMood,mOther;
    TextView mWeight,mTemperature;
    
    
    public WomenCalendarViewAdapter(Context context, ArrayList<Day> calendarDays,Time time){
        mContext = context;
        mCalendarDays = calendarDays;
        mTime = time;
        mToday = new Time();
        mToday.set(System.currentTimeMillis());
        mToday.normalize(true);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mCalendarDays.size();        
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        //return mCursor.getDayAt(position/7, position%7);
        return mCalendarDays.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        final DayOfMonthCursor  cursor = new DayOfMonthCursor(mTime.year,mTime.month,mTime.monthDay,Utils.getStartDayOfWeek(mContext));  
        mDay = cursor.getDayAt(position/7, position%7);
        
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.day, parent, false);
        
        mDayOfMonth = (TextView)retView.findViewById(R.id.date);
        mPeriodDay = (TextView)retView.findViewById(R.id.period_day);
        mSex = (ImageView)retView.findViewById(R.id.sex_notification);
        mPill = (ImageView)retView.findViewById(R.id.pill_notification);
        mMood = (ImageView)retView.findViewById(R.id.mood_notification);
        mOther = (ImageView)retView.findViewById(R.id.others_notification);
        
        Day day = mCalendarDays.get(position);
        if(cursor.isWithinCurrentMonth(position/7, position%7)){
            retView.setBackgroundResource(R.drawable.calendar_day_standard);
            
            if(day.DAYTYPE == Utils.DAY_TYPE_START) {
                retView.setBackgroundResource(R.drawable.calendar_day_start_period_standard);
                mPeriodDay.setVisibility(View.VISIBLE);
                mPeriodDay.setText("("+day.mPeriodDay+")");
            } else if (day.DAYTYPE == Utils.DAY_TYPE_IN_PERIOD) {
                retView.setBackgroundResource(R.drawable.calendar_day_middle_period_standard);
            } else if (day.DAYTYPE ==  Utils.DAY_TYPE_END) {
                retView.setBackgroundResource(R.drawable.calendar_day_end_period_standard);
                mPeriodDay.setVisibility(View.VISIBLE);
                mPeriodDay.setText("("+day.mPeriodDay+")");
            } 
        }else {
            retView.setBackgroundResource(R.drawable.calendar_day_other_standard);
        }
        
        if (day.DAYTYPE == Utils.DAY_TYPE_OVULATION) {
            mOther.setVisibility(View.VISIBLE);
            mOther.setImageResource(R.drawable.calendar_fertility);
        }
        
        if (mDay == mToday.monthDay && cursor.getYear() == mToday.year
                && cursor.getMonth() == mToday.month) {
            mDayOfMonth.setTypeface(Typeface.DEFAULT_BOLD);
        }
        
        mDayOfMonth.setText(String.valueOf(mDay));
        
        retView.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                String date = mCalendarDays.get(position).TIME.format("%Y%m%d");
                Intent intent = new Intent(v.getContext(),WomenCalendarDayActivity.class);
                intent.putExtra(Utils.EXTRAS_SELECTED_DAY, date);
                mContext.startActivity(intent);                
            }
        });  
        
        return retView;
    }

    public void setTime(Time time) {
        mTime = time;
    }

    public void setCalendarDays(ArrayList<Day> calendarDays){
        mCalendarDays = calendarDays;
        this.notifyDataSetChanged();
    }
}
