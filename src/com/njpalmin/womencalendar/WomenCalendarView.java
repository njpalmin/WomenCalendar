package com.njpalmin.womencalendar;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.njpalmin.womencalendar.Utils.DayType;

public class WomenCalendarView extends View {

	final static public String TAG="WomenCalendarView";

    private static float mScale = 0; // Used for supporting different screen densities
    
	private static int WEEK_GAP = 4;
	private static int MONTH_DAY_GAP = 3;
	private static float HOUR_GAP = 0f;
	private static float MIN_EVENT_HEIGHT = 4f;
	private static int MONTH_DAY_TEXT_SIZE = 12;
	private static int WEEK_BANNER_HEIGHT = 17;
	private static int WEEK_TEXT_SIZE = 12;
	private static int WEEK_TEXT_PADDING = 3;
	private static int EVENT_DOT_TOP_MARGIN = 5;
	private static int EVENT_DOT_LEFT_MARGIN = 7;
	private static int EVENT_DOT_W_H = 10;
	private static int EVENT_NUM_DAYS = 31;
	private static int TEXT_TOP_MARGIN = 4;
	private static int BUSY_BITS_WIDTH = 10;
	private static int BUSY_BITS_MARGIN = 2;
	private static int DAY_NUMBER_OFFSET = 10;
    private static int HORIZONTAL_FLING_THRESHOLD = 50;
    
	
	private WomenCalendarActivity mParentActivity;
    private GestureDetector mGestureDetector;
    private Context mContext;
    
    private Resources mResources;
    
    private int mCellHeight;
    private int mBorder;
    private int mCellWidth;
    private boolean mLaunchDayView;
    
   
    /**
     * The first Julian day of the current month.
     */
    private int mFirstJulianDay;
    private int mStartDay;
    private DayOfMonthCursor mCursor;
    private Time mToday;
    private Time mViewCalendar;
    private Time mOtherViewCalendar;
    private Time mTempTime;
    private Time mStartedTime;
    
    // These booleans disable features that were taken out of the spec.
    private boolean mShowWeekNumbers = false;
    private boolean mShowToast = false;
    
    // For drawing to an off-screen Canvas
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private boolean mRedrawScreen = true;
    private Rect mBitmapRect = new Rect();
    private Rect mRect = new Rect();
    
    // Cached colors
    private int mMonthOtherMonthColor;
    private int mMonthWeekBannerColor;
    private int mMonthOtherMonthBannerColor;
    private int mMonthOtherMonthDayNumberColor;
    private int mMonthDayNumberColor;
    private int mMonthTodayNumberColor;
    private int mMonthTodayBackgroundColor;
    private int mMonthSaturdayColor;
    private int mMonthSundayColor;
    private int mBusybitsColor;
    private int mMonthBgColor;
    private int mPressedColor;
	
    //LinearLayout
    private LinearLayout mStartPeriod;
    
    
    
    private int DAY_IN_MONTH = 1<<0;
    private int TODAY = 1<<1;
    

    
	public WomenCalendarView(WomenCalendarActivity activity, Time time, Time startedTime) {
		super(activity);

        
		//mContext = activity;
		// TODO Auto-generated constructor stub
		//mWomenCalendarActivity = activity;
		/*
        if (mScale == 0) {
            mScale = getContext().getResources().getDisplayMetrics().density;
           if (mScale != 1) {
                    WEEK_GAP *= mScale;
                    MONTH_DAY_GAP *= mScale;
                    HOUR_GAP *= mScale;

                    MONTH_DAY_TEXT_SIZE *= mScale;
                    WEEK_BANNER_HEIGHT *= mScale;
                    WEEK_TEXT_SIZE *= mScale;
                    WEEK_TEXT_PADDING *= mScale;
                    EVENT_DOT_TOP_MARGIN *= mScale;
                    EVENT_DOT_LEFT_MARGIN *= mScale;
                    EVENT_DOT_W_H *= mScale;
                    TEXT_TOP_MARGIN *= mScale;
                    HORIZONTAL_FLING_THRESHOLD *= mScale;
                    MIN_EVENT_HEIGHT *= mScale;
                    BUSY_BITS_WIDTH *= mScale;
                    BUSY_BITS_MARGIN *= mScale;
                    DAY_NUMBER_OFFSET *= mScale;
                }
        }*/
		InitView(activity, time, startedTime);
	}

	public void InitView(WomenCalendarActivity activity, Time time, Time startedTime){
        setFocusable(true);
        setClickable(true);
		mParentActivity = activity;
		
        //long now = System.currentTimeMillis();
        
        if(startedTime != null){
        	mStartedTime = startedTime;	
        }
        
        mOtherViewCalendar = new Time();
        mTempTime = new Time();

        mViewCalendar = time;
        
        mCursor = new DayOfMonthCursor(mViewCalendar.year,  mViewCalendar.month,
                mViewCalendar.monthDay, mParentActivity.getStartDay());
        
        mToday = new Time();
        mToday.set(System.currentTimeMillis());
        
        mResources = activity.getResources();

        // Cache color lookups
        Resources res = getResources();
        mMonthOtherMonthColor = res.getColor(R.color.month_other_month);
        mMonthWeekBannerColor = res.getColor(R.color.month_week_banner);
        mMonthOtherMonthBannerColor = res.getColor(R.color.month_other_month_banner);
        mMonthOtherMonthDayNumberColor = res.getColor(R.color.month_other_month_day_number);
        mMonthDayNumberColor = res.getColor(R.color.month_day_number);
        mMonthTodayNumberColor = res.getColor(R.color.month_today_number);
        mMonthTodayBackgroundColor = res.getColor(R.color.month_today_bgcolor);
        mMonthSaturdayColor = res.getColor(R.color.month_saturday);
        mMonthSundayColor = res.getColor(R.color.month_sunday);
        mBusybitsColor = res.getColor(R.color.month_busybits);
        mMonthBgColor = res.getColor(R.color.month_bgcolor);
        mPressedColor = res.getColor(R.color.pressed);
        
      
        
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        	@Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                    float velocityY) {
        		mLaunchDayView = false;
                int distanceX = Math.abs((int) e2.getX() - (int) e1.getX());
                int distanceY = Math.abs((int) e2.getY() - (int) e1.getY());
                if (distanceX < HORIZONTAL_FLING_THRESHOLD) {
                    return false;
                }
                // Switch to a different month
                Time time = mOtherViewCalendar;
                time.set(mViewCalendar);
                if (velocityX < 0) {
                    time.month += 1;
                } else {
                    time.month -= 1;
                }
                time.normalize(true);
                mParentActivity.goToMonth(time);
        		
        		return true;
        	}
        	
            @Override
            public boolean onDown(MotionEvent e) {
                // Launch the Day/Agenda view when the finger lifts up,
                // unless the finger moves before lifting up (onFling or onScroll).
                mLaunchDayView = true;
                return true;
            }
            
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                    float distanceX, float distanceY) {
                // If the user moves his finger after touching, then do not
                // launch the Day view when he lifts his finger.  Also, turn
                // off the selection.
                mLaunchDayView = false;
/*
                if (mSelectionMode != SELECTION_HIDDEN) {
                    mSelectionMode = SELECTION_HIDDEN;
                    mRedrawScreen = true;
                    invalidate();
                }*/
                return true;
            }
            
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (mLaunchDayView) {
                    //setSelectedCell(e);
                    //mSelectionMode = SELECTION_SELECTED;
                    mRedrawScreen = true;
                    invalidate();
                    mLaunchDayView = false;
                    int x = (int) e.getX();
                    int y = (int) e.getY();
                    long millis = getSelectedMillisFor(x, y);
                    
                    Time time = new Time();
                    time.set(millis);
                    
                    
                    //Utils.startActivity(getContext(), WomenCalendarDayActivity.class, millis);
                    Intent intent = new Intent(getContext(), WomenCalendarDayActivity.class);

                    //intent.setClassName(getContext(), WomenCalendarDayActivity.class);
                    intent.putExtra(Utils.EVENT_BEGIN_TIME, millis);
                    intent.putExtra(Utils.DAY_TYPE,getDayType(time.monthDay));
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    //getContext().startActivity(intent);
                    mParentActivity.startActivityForResult(intent,WomenCalendarActivity.DAY_ACTIVITY_DETAILS);
                }

                return true;
            }            
        });
     }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRedrawScreen) {
            if (mCanvas == null) {
                drawingCalc(getWidth(), getHeight());
            }

            // If we are zero-sized, the canvas will remain null so check again
            if (mCanvas != null) {
                // Clear the background
                final Canvas bitmapCanvas = mCanvas;
                bitmapCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                doDraw(bitmapCanvas);
                mRedrawScreen = false;
            }
        }

        // If we are zero-sized, the bitmap will be null so guard against this
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mBitmapRect, mBitmapRect, null);
        }    
    }

    private void drawingCalc(int width, int height) {
        mCellHeight = (height - (6 * WEEK_GAP)) / 6;
        //mEventGeometry.setHourHeight((mCellHeight - BUSY_BITS_MARGIN * 2 - TEXT_TOP_MARGIN) / 24.0f);
        mCellWidth = (width - (6 * MONTH_DAY_GAP)) / 7;
        
        mBorder = (width - 6 * (mCellWidth + MONTH_DAY_GAP) - mCellWidth) / 2;
/*
        if (mShowToast) {
            mPopup.dismiss();
            mPopup.setWidth(width - 20);
            mPopup.setHeight(POPUP_HEIGHT);
        }
*/
        if (((mBitmap == null)
                    || mBitmap.isRecycled()
                    || (mBitmap.getHeight() != height)
                    || (mBitmap.getWidth() != width))
                && (width > 0) && (height > 0)) {
            if (mBitmap != null) {
                mBitmap.recycle();
            }
            Bitmap p = BitmapFactory.decodeResource(getResources(), R.drawable.calendar_day_standard); 
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        mBitmapRect.top = 0;
        mBitmapRect.bottom = height;
        mBitmapRect.left = 0;
        mBitmapRect.right = width;
    }
    
    
    private void doDraw(Canvas canvas) {
        boolean isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        Paint p = new Paint();
        Rect r = mRect;
        int columnDay1 = mCursor.getColumnOf(0);

        // Get the Julian day for the date at row 0, column 0.
        int day = mFirstJulianDay - columnDay1;

        int weekNum = 0;
        Calendar calendar = null;
        if (mShowWeekNumbers) {
            calendar = Calendar.getInstance();
            boolean noPrevMonth = (columnDay1 == 0);

            // Compute the week number for the first row.
            weekNum = getWeekOfYear(0, 0, noPrevMonth, calendar);
        }

        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 7; column++) {
                drawBox(day, weekNum, row, column, canvas, p, r, isLandscape);
                day += 1;
            }

            if (mShowWeekNumbers) {
                weekNum += 1;
                if (weekNum >= 53) {
                    boolean inCurrentMonth = (day - mFirstJulianDay < 31);
                    weekNum = getWeekOfYear(row + 1, 0, inCurrentMonth, calendar);
                }
            }
        }

        drawGrid(canvas, p);
    }
    
    /**
     * Draw the grid lines for the calendar
     * @param canvas The canvas to draw on.
     * @param p The paint used for drawing.
     */
    private void drawGrid(Canvas canvas, Paint p) {
        p.setColor(Color.TRANSPARENT);
        p.setAntiAlias(false);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        for (int row = 0; row < 6; row++) {
            int y = WEEK_GAP + row * (WEEK_GAP + mCellHeight) - 1;
            canvas.drawLine(0, y, width, y, p);
        }
        for (int column = 1; column < 7; column++) {
            int x = mBorder + column * (MONTH_DAY_GAP + mCellWidth) - 1;
            canvas.drawLine(x, WEEK_GAP, x, height, p);
        }
    }
    
    private void drawBox(int day, int weekNum, int row, int column, Canvas canvas, Paint p,
            Rect r, boolean isLandscape) {

        // Only draw the selection if we are in the press state or if we have
        // moved the cursor with key input.
    	Log.d(TAG,"day= " + day +"||weeknumber= "+ weekNum+" ||row= " + row + "||column= "+ column);
       
    	boolean drawSelection = false;
        Bitmap bitmap;
        /*
        if (mSelectionMode != SELECTION_HIDDEN) {
            drawSelection = mCursor.isSelected(row, column);
        }*/

        int dayType = Utils.DAY_TYPE_NORMAL_DAY;
        boolean withinCurrentMonth = mCursor.isWithinCurrentMonth(row, column);
        
        
        boolean isToday = false;
        int dayOfBox = mCursor.getDayAt(row, column);
        if (dayOfBox == mToday.monthDay && mCursor.getYear() == mToday.year
                && mCursor.getMonth() == mToday.month) {
            isToday = true;
        }
        
        dayType = getDayType(dayOfBox);
        
        Log.d(TAG,"day = "+ dayOfBox + "|| dayType="+ dayType);
        
        /*
        boolean isStarted = false;
        if( dayOfBox == mStartedTime.monthDay &&  mCursor.getYear() == mStartedTime.year
                && mCursor.getMonth() == mStartedTime.month) {
        	isStarted = true;
        }
        
        boolean withinCurrentPeriod = isWithinCurrentPeriod(dayOfBox);
        boolean isEndCurrentPeriod = isEndCurrentPeriod(dayOfBox);
        */
        
        int y = WEEK_GAP + row*(WEEK_GAP + mCellHeight);
        int x = mBorder + column*(MONTH_DAY_GAP + mCellWidth);

        r.left = x;
        r.top = y;
        r.right = x + mCellWidth;
        r.bottom = y + mCellHeight;

        
        // Adjust the left column, right column, and bottom row to leave
        // no border.
        if (column == 0) {
            r.left = -1;
        } else if (column == 6) {
            r.right += mBorder + 2;
        }

        if (row == 5) {
            r.bottom = getMeasuredHeight();
        }

        Log.d(TAG,"top="+r.top+" ||bottom="+r.bottom+"||left="+r.left+"||right="+r.right);
        // Draw the cell contents (excluding monthDay number)
        if (!withinCurrentMonth) {
            // Adjust cell boundaries to compensate for the different border
            // style.
        	/*
            r.top--;
            if (column != 0) {
                r.left--;
            }*/
        	bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.calendar_day_other_standard);
        	canvas.drawBitmap(bitmap, null,r, null);
            
        } else{
        	
        	switch (dayType){
        		case Utils.DAY_TYPE_START_DAY:
        			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.calendar_day_start_period_standard);
        			canvas.drawBitmap(bitmap, null,r, null);
        			break;
        		case Utils.DAY_TYPE_END_DAY:
        			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.calendar_day_end_period_standard);
        			canvas.drawBitmap(bitmap, null,r, null);
        			break;
        		case Utils.DAY_TYPE_MIDDLE_DAY:
        			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.calendar_day_middle_period_standard);
        			canvas.drawBitmap(bitmap, null,r, null);
        			break;
        		case Utils.DAY_TYPE_FERTILITY_DAY:
        			Log.d(TAG,"FERTILITY_DAY!");
        			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.calendar_day_standard);
        			Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.calendar_fertility);
        			canvas.drawBitmap(bitmap, null,r, null);
        			Rect rect = new Rect();
        			rect.top = r.bottom-b.getHeight();
        			rect.left = r.right -b.getWidth();
        			rect.bottom = r.bottom;
        			rect.right = r.right;
        			
        			canvas.drawBitmap(b, null,rect, null);
        			canvas.save(Canvas.ALL_SAVE_FLAG);
        			canvas.restore();
        			//Log.d(TAG,"bitmap height="+bitmap.getHeight()+ " width="+bitmap.getWidth());
        			break;
        		default:
        			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.calendar_day_standard);
        			canvas.drawBitmap(bitmap, null,r, null);
        			break;
        	}
        	
        }
        

        // Draw the monthDay number
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);
        p.setTypeface(null);
        p.setTextSize(MONTH_DAY_TEXT_SIZE);

        if (!withinCurrentMonth) {
            p.setColor(mMonthOtherMonthDayNumberColor);
        } else {
            if (isToday && !drawSelection) {
                p.setColor(mMonthTodayNumberColor);
                p.setFakeBoldText(true);
            }else {
            	p.setColor(mMonthOtherMonthDayNumberColor);
            }
            //bolds the day if there's an event that day
            
        }
        /*Drawing of day number is done here
         *easy to find tags draw number draw day*/
        p.setTextAlign(Paint.Align.CENTER);
        // center of text
        int textX = r.left + (r.right - BUSY_BITS_MARGIN - BUSY_BITS_WIDTH - r.left) / 2;
        int textY = (int) (r.top + p.getTextSize() + TEXT_TOP_MARGIN); // bottom of text
        canvas.drawText(String.valueOf(mCursor.getDayAt(row, column)), textX, textY, p);
    }

    
    private int getWeekOfYear(int row, int column, boolean isWithinCurrentMonth,
            Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, mCursor.getDayAt(row, column));
        if (isWithinCurrentMonth) {
            calendar.set(Calendar.MONTH, mCursor.getMonth());
            calendar.set(Calendar.YEAR, mCursor.getYear());
        } else {
            int month = mCursor.getMonth();
            int year = mCursor.getYear();
            if (row < 2) {
                // Previous month
                if (month == 0) {
                    year--;
                    month = 11;
                } else {
                    month--;
                }
            } else {
                // Next month
                if (month == 11) {
                    year++;
                    month = 0;
                } else {
                    month++;
                }
            }
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
        }

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        return super.onTouchEvent(event);
    }
    
    private long getSelectedMillisFor(int x, int y) {
        int row = (y - WEEK_GAP) / (WEEK_GAP + mCellHeight);
        int column = (x - mBorder) / (MONTH_DAY_GAP + mCellWidth);
        if (column > 6) {
            column = 6;
        }

        DayOfMonthCursor c = mCursor;
        Time time = mTempTime;
        time.set(mViewCalendar);

        // Compute the day number from the row and column.  If the row and
        // column are in a different month from the current one, then the
        // monthDay might be negative or it might be greater than the number
        // of days in this month, but that is okay because the normalize()
        // method will adjust the month (and year) if necessary.
        time.monthDay = 7 * row + column - c.getOffset() + 1;
        return time.normalize(true);
    }
    
    private int getDayType(int day){
    	int dayType = Utils.DAY_TYPE_NORMAL_DAY;
    	
    	
    	if(mStartedTime == null){
    		dayType = Utils.DAY_TYPE_NORMAL_DAY;
    		return dayType;
    	}
    	
        if( day == mStartedTime.monthDay &&  mCursor.getYear() == mStartedTime.year
                && mCursor.getMonth() == mStartedTime.month) {
        	dayType =Utils.DAY_TYPE_START_DAY;
        }
        
        if((day - mStartedTime.monthDay) >= 0 && (day - mStartedTime.monthDay) <= (Utils.PERIOD_LENGTH -2)){
        	dayType = Utils.DAY_TYPE_MIDDLE_DAY;
        }
        
        if((day - mStartedTime.monthDay) == (Utils.PERIOD_LENGTH -1)){
        	dayType = Utils.DAY_TYPE_END_DAY;
        }
        
        if((day - mStartedTime.monthDay >= Utils.calculateFertilityStartPeriod(Utils.CYCLE_LENGTH)) && 
           (day - mStartedTime.monthDay <= Utils.calculateFertilityEndPeriod(Utils.CYCLE_LENGTH))){
           dayType = Utils.DAY_TYPE_FERTILITY_DAY;
        }
        
        
        return dayType;
    }
    
}
