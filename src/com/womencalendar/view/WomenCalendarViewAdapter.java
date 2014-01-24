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
//    private ArrayList<Period> mPeriods;
    
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
//        return 42;
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
        ImageView sex = (ImageView)retView.findViewById(R.id.sex_notification);
        ImageView pill = (ImageView)retView.findViewById(R.id.pill_notification);
        ImageView note = (ImageView)retView.findViewById(R.id.note_notification);
        TextView  weight = (TextView)retView.findViewById(R.id.weight);
        TextView  bmt = (TextView)retView.findViewById(R.id.temperature);
        mMood = (ImageView)retView.findViewById(R.id.mood_notification);
        mOther = (ImageView)retView.findViewById(R.id.others_notification);
        
        final Day day = mCalendarDays.get(position);
        if(cursor.isWithinCurrentMonth(position/7, position%7)){
            retView.setBackgroundResource(R.drawable.calendar_day);
            
            if(day.DAYTYPE == Utils.DAY_TYPE_START) {
                retView.setBackgroundResource(R.drawable.calendar_day_start_period);

            } else if (day.DAYTYPE == Utils.DAY_TYPE_IN_PERIOD) {
                retView.setBackgroundResource(R.drawable.calendar_day_middle_period);
            } else if (day.DAYTYPE ==  Utils.DAY_TYPE_END) {
                retView.setBackgroundResource(R.drawable.calendar_day_end_period);
            } 
        }else {
            retView.setBackgroundResource(R.drawable.calendar_day_other);
        }
        
        if(day.mPeriodDay != 0){
            mPeriodDay.setVisibility(View.VISIBLE);
            mPeriodDay.setText("("+day.mPeriodDay+")");
        }
        
        if (day.DAYTYPE == Utils.DAY_TYPE_FERTILITY) {
            mOther.setVisibility(View.VISIBLE);
            mOther.setImageResource(R.drawable.calendar_fertility);
        }
        
        if (day.DAYTYPE == Utils.DAY_TYPE_OVULATION) {
            mOther.setVisibility(View.VISIBLE);
            mOther.setImageResource(R.drawable.calendar_ovulation);
        }
        
        if ((day.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_PILL) != 0) {
            pill.setVisibility(View.VISIBLE);
        } 
        if((day.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_SEX) != 0) {
            sex.setVisibility(View.VISIBLE);
        }
        if ((day.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_WEIGHT) != 0) {
            weight.setVisibility(View.VISIBLE);
            weight.setText(String.valueOf(day.weight));
        } 
        if ((day.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_BMT) != 0) {
            bmt.setVisibility(View.VISIBLE);
            bmt.setText(String.valueOf(day.bmt));
        }
        
        if ((day.DAYNOTIFICATION & Utils.NOTIFICATION_TYPE_NOTE) != 0) {
            note.setVisibility(View.VISIBLE);
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
                intent.putExtra(Utils.EXTRAS_SELECTED_DAY_DAYTYPE,day.DAYTYPE);
                intent.putExtra(Utils.EXTRAS_SELECTED_DAY_PERIOD_DAY, day.mPeriodDay);
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
