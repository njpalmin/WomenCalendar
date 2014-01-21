package com.womencalendar.view;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.womencalendar.DayInfo;
import com.womencalendar.R;
import com.womencalendar.WomenCalendar.Record;
import com.womencalendar.utils.DayOfMonthCursor;
import com.womencalendar.utils.Utils;

public class DayView extends RelativeLayout{
    private static final String TAG = "DayView";
    
    private final static int RECORD_TYPE_START = 1<<0;
    private final static int RECORD_TYPE_PILL = 1<<1;
    private final static int RECORD_TYPE_BMT = 1<<2;
    private final static int RECORD_TYPE_NOTE = 1<<3;
    private final static int RECORD_TYPE_SEX = 1<<4;
    private final static int RECORD_TYPE_WEIGHT = 1<<5;
    
    
    TextView mDayOfMonth;
    TextView mPeriodDay;
    ImageView mSex,mPill,mNote,mMood,mOther;
    TextView mWeight,mTemperature;
    
    private Context mContext;
    private ContentResolver mContentResolver;
    private boolean mWithinCurrentMonth;
    private boolean mIsToday;
    private int mDay;
    private DayOfMonthCursor mCursor;
    private Time mToday,mTime;
    private DayInfo mDayInfo;
    private String mDate;
    
    
    private static final String[] RECORD_PROJECTION = new String[] {
        Record._ID, 
        Record.DATE,
        Record.TYPE,
        Record.FLOATVALUE,
        Record.STRINGVALUE,
        Record.INTVALUE,        
    };
    
    public String getDate() {
        return mDate;
    }

    public DayView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public DayView(Context context, int row, int column, DayOfMonthCursor cursor,int weekStartDay) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        mContentResolver = context.getContentResolver();
        mToday = new Time();
        mToday.set(System.currentTimeMillis());
        mToday.normalize(true);
        
        if(cursor == null){
            mCursor = new DayOfMonthCursor(mToday.year,  mToday.month,mToday.monthDay,weekStartDay);;
        }else{
            mCursor = cursor;
        }
        
        mDay = mCursor.getDayAt(row, column);
        
        mTime = new Time();
        mTime.set(mDay,cursor.getMonth(),cursor.getYear());
        mTime.monthDay = 7 * row + column - mCursor.getOffset() + 1;
        mTime.normalize(true);
        
        mDate = mTime.format("%Y%m%d");
              
        mWithinCurrentMonth = mCursor.isWithinCurrentMonth(row, column);
        
        initView();
    }

    private void initView(){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day, this, true);
        
        mDayOfMonth = (TextView)findViewById(R.id.date);
        mPeriodDay = (TextView)findViewById(R.id.period_day);
        mSex = (ImageView)findViewById(R.id.sex_notification);
        mPill = (ImageView)findViewById(R.id.pill_notification);
        mMood = (ImageView)findViewById(R.id.mood_notification);
        mOther = (ImageView)findViewById(R.id.others_notification);
        
        if(mWithinCurrentMonth) {
            this.setBackgroundResource(R.drawable.calendar_day_standard);
        } else {
            this.setBackgroundResource(R.drawable.calendar_day_other_standard);
        }
        
        
        if (mDay == mToday.monthDay && mCursor.getYear() == mToday.year
                && mCursor.getMonth() == mToday.month) {
            mDayOfMonth.setTypeface(Typeface.DEFAULT_BOLD);
        }
        
        mDayOfMonth.setText(String.valueOf(mDay));
    }
    
    private boolean isStartDay(String date) {
        Cursor c = mContentResolver.query(Record.CONTENT_URI, RECORD_PROJECTION, 
                "date=? AND type=?",new String[]{date,Utils.RECORD_TYPE_START},null);
        if(c != null && c.getCount() != 0) {
            return true;
        }
        c.close();
        return false;
    }
    
    

    
//    private int getDayNotification(String date){
//        Cursor c = mContentResolver.query(Record.CONTENT_URI, RECORD_PROJECTION, 
//                   "date=?",new String[]{date},null);
//        int notification = 0;
//        if(c != null && c.getCount() != 0 ){
//            String[] recordTypes = new String[c.getCount()];
//            int i =0;
//            while(c.moveToNext()){
//                recordTypes[i] = c.getString(c.getColumnIndex("type"));
//                i++;
//            }
//            c.close();
//            
//            for(int j=0; i< recordTypes.length;j++){
//                if(recordTypes[j].equals(Utils.RECORD_TYPE_PILL)){
//                    notification |= RECORD_TYPE_PILL;
//                }else if (recordTypes[j].equals(Utils.RECORD_TYPE_SEX)){
//                    notification |= RECORD_TYPE_SEX;
//                }else if (recordTypes[j].equals(Utils.RECORD_TYPE_BMT)){
//                    notification |= RECORD_TYPE_BMT;
//                }else if (recordTypes[j].equals(Utils.RECORD_TYPE_WEIGHT)){
//                    notification |= RECORD_TYPE_WEIGHT;
//                }else if (recordTypes[j].equals(Utils.RECORD_TYPE_NOTE)){
//                    notification |= RECORD_TYPE_NOTE;
////                }else if (recordTypes[j].equals(Utils.RECORD_TYPE_START)){
////                    type |= RECORD_TYPE_START;
////                }else if (recordTypes[j].equals(Utils.RECORD_TYPE_END)){
////                    type &= RECORD_TYPE_START;
//                }
//            }
//            return type;
//        }else {
//            return 0;
//        }
//    }
}
