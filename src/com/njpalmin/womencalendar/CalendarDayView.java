package com.njpalmin.womencalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;

public class CalendarDayView extends View {

	private static int WEEKDAYS = 7; 
	private Context mContext;
	private LinearLayout mParentLayout;
	private RelativeLayout[] mWeekDays;
	
	public CalendarDayView(Context context, LinearLayout parentLayout) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mParentLayout = parentLayout;

/*
		weekday.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.calendar_day_standard);
		weekday.setBackgroundResource(R.drawable.calendar_day_other_standard);
		mParentLayout.addView(weekday);*/
		}
	}


