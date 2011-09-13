package com.njpalmin.womencalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CalendarView extends View {

	private static int WEEK_ROW = 6;
	private static int WEEKDAYS = 7;
	
	private LinearLayout[] mWeekLayout;
	private Context mContext;
	private LinearLayout mParentLayout;
	private RelativeLayout mChildLayout;
	private int mHeight;
	private int mWidth;
	private Canvas mCanvas;
	
	public CalendarView(Context context,LinearLayout parentLayout) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
		mWeekLayout = new LinearLayout[WEEK_ROW];
		mParentLayout = parentLayout;
		mHeight = parentLayout.getHeight()/WEEK_ROW;
		mWidth = parentLayout.getWidth();
		
		for(int i=0;i<WEEK_ROW;i++){
			LinearLayout weekLayout = new LinearLayout(mContext);
			weekLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,1));
			weekLayout.setOrientation(0);
			
			LinearLayout[] weekDays = new LinearLayout[WEEKDAYS];
			for(int j=0;j<WEEKDAYS;j++){
				LinearLayout weekDay = new LinearLayout(mContext);
				weekDay.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,1));
				weekDay.setBackgroundResource(R.drawable.calendar_day_other_standard);
				
				
				weekLayout.addView(weekDay);
				
				/*
				RelativeLayout weekDay = new RelativeLayout(mContext);

				ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
				layoutParams.setMargins(0,20,20,20);
			    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(layoutParams);
			    rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
	
			    weekDay.setLayoutParams(rlp);
	
				weekDay.setBackgroundResource(R.drawable.calendar_day_other_standard);
								
				weekLayout.addView(weekDay);*/
			}
			
			mParentLayout.addView(weekLayout);
		}
		
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		mCanvas = canvas;
	}
	
}
