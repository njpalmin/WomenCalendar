package com.njpalmin.womencalendar;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.text.format.Time;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class CalendarView extends View {

	private static int WEEKS= 6;
	private static int WEEKDAYS = 7;
	//private static int HORIZONTAL_FLING_THRESHOLD = 50;

	private LinearLayout[] mWeekLayout;
	private Context mContext;
	private LinearLayout mParentLayout;
	private Time mTime;
	private Time mOtherViewCalendar;
	private Time mViewCalendar;
	private int mHeight;
	private int mWidth;
	private Canvas mCanvas;
	private DayOfMonthCursor mCursor;
	private GestureDetector mGestureDetector;
	private WomenCalendarActivity mParentActivity;
	private boolean mLaunchDayView;
	private boolean mRedrawScreen = true;
	private int mRow = -1;
	
	/*
	public CalendarView(Context context){
		super(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.id.calendar_layout, this);
		
        long now = System.currentTimeMillis();

        mTime = new Time();
        mTime.set(now);
        mTime.normalize(true);

		mCursor = new DayOfMonthCursor(mTime.year,  mTime.month,mTime.monthDay, Calendar.getInstance().getFirstDayOfWeek());

        initView();
	}

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.id.calendar_layout, this);
    }
	
    */
	public CalendarView(Context context,Time time,LinearLayout parentLayout) {	
		super(context);
		mContext = context;
		mParentActivity = (WomenCalendarActivity)context;
		// TODO Auto-generated constructor stub
		mWeekLayout = new LinearLayout[WEEKS];
		
		mParentLayout = parentLayout;
		mHeight = parentLayout.getHeight()/WEEKS;
		mWidth = parentLayout.getWidth();
		mTime = time;
		
		mWeekLayout = new LinearLayout[WEEKS];
		for(int i=0;i<WEEKS;i++){
			mWeekLayout[i] = new LinearLayout(mContext);
			mWeekLayout[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,1));
			mWeekLayout[i].setOrientation(0);
		}
		
		initView(mTime);

	}

	private void initView(Time time){
		
		mCursor = new DayOfMonthCursor(time.year,  time.month, time.monthDay, Calendar.getInstance().getFirstDayOfWeek());
		
		//LinearLayout[] weekLayout = new LinearLayout[WEEKS];
		
		for(int i=0;i<WEEKS;i++){
			/*
			mWeekLayout[i] = new LinearLayout(mContext);
			mWeekLayout[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,1));
			mWeekLayout[i].setOrientation(0);
			*/
			for(int j=0;j<WEEKDAYS;j++){
				DayInfoView[] weekDays = new DayInfoView[WEEKDAYS];
				//int monthDay = mCursor.getDayAt(i,j);
				//boolean withinCurrentMonth = mCursor.isWithinCurrentMonth(i, j);
				weekDays[j] = new DayInfoView(mContext,i,j,mCursor);
				LinearLayout.LayoutParams llp  = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT,1);
				llp.setMargins(2,2,2,2);
				weekDays[j].setLayoutParams(llp);
				weekDays[j].setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	Intent intent = new Intent(getContext(), WomenCalendarDayActivity.class);
		            	intent.putExtra(Utils.EVENT_BEGIN_TIME, ((DayInfoView)v).getMillis());
		            	intent.putExtra(Utils.EVENT_ROW, ((DayInfoView)v).getRow());
		            	intent.putExtra(Utils.EVENT_COLUMN, ((DayInfoView)v).getColumn());
		            	
		            	mParentActivity.startActivityForResult(intent,WomenCalendarActivity.DAY_ACTIVITY_DETAILS);
		                //startActivityForResult(intent,DAY_ACTIVITY_DETAILS);
		            }
		        });
				mWeekLayout[i].addView(weekDays[j]);
			}
			mParentLayout.addView(mWeekLayout[i]);
			//addView(mWeekLayout[i]);
		}
	}
	
	public void reDrawView(Time time){
		for(int i=0;i<WEEKS;i++){
			mWeekLayout[i].removeAllViews();
			mParentLayout.removeView(mWeekLayout[i]);
		}
		
		initView(time);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		mCanvas = canvas;
	}
	
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        return super.onTouchEvent(event);
    }
    
}
