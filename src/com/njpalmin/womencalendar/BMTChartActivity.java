package com.njpalmin.womencalendar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class BMTChartActivity extends Activity {
	/*
	private ChartView mBMTChartView;
	private ChartViewLayout mBMTChartViewLayout;*/
	private Time mTime;
	private int mDaysOfMonth;
	
	
	@Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.bmtchart_main);
		LinearLayout mainLayout=(LinearLayout)findViewById(R.id.chart_layout);
		
		long now = System.currentTimeMillis();
        mTime = new Time();
        mTime.set(now);
        mTime.normalize(true);
        mDaysOfMonth = Utils.getDaysOfMonth(mTime);
		
		//mBMTChartView = new ChartView(this);
		/*
		mBMTChartViewLayout = new ChartViewLayout(this,mDaysOfMonth);
		
		mBMTChartViewLayout.addChartViewChangeListener(new OnChartViewChangeListener() {  
	            @Override  
            public void OnChartViewChange(int currentItem) {  
                // 干些想干的事件  
            	Log.v("m","currentItem:"+currentItem);
            }  
  
        });
		mBMTChartViewLayout.setBackgroundColor(Color.WHITE); 
		mainLayout.addView(mBMTChartViewLayout);
		*/
		mainLayout.setBackgroundColor(Color.WHITE); 
		//mainLayout.addView(mBMTChartView);
	}
	/*
	public boolean onTouchEvent(MotionEvent event) {
		return mBMTChartView.onChartViewTouchEvent(event);
	}*/
}
