package com.njpalmin.womencalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WomenCalendarDayActivity extends Activity {
	private final static String TAG="WomenCalendarDayActivity";
	
	private Time mTime;
    private String mTitle;
    
    private LinearLayout mStart;
    private ImageView mEnd;
    private ImageView mPill;
    private ImageView mBmt;
    private ImageView mNote;
    private ImageView mSex;
    private ImageView mCervicalMucus;
    private ImageView mMood;
    private ImageView mWeight;
    
    private Button mRemoveAll;
    private Button mBack;
    
    
	@Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		long millis = 0;
		
		setContentView(R.layout.womencalendarday_main);
		millis = getIntent().getLongExtra(Utils.EVENT_BEGIN_TIME, 0);
		
		mTime = new Time();
		mTime.set(millis);
		updateTitle(mTime);
		
		
		initView();
	}
	
	void initView(){
		
		mStart = (LinearLayout)findViewById(R.id.start_period);
		mStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Log.d(TAG,"mStart onClick");
				long millis = mTime.toMillis(true);
				setIntentAndFinish(true, millis);
			}
		});
		
		
		mBack = (Button)findViewById(R.id.back);
		mBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
	}
	
    void updateTitle(Time time) {
        TextView title = (TextView) findViewById(R.id.date);
        StringBuffer date = new StringBuffer(Utils.formatDate(this, time));
        title.setText(date.toString());
    }
    
    void setStartDay(Time time){
    	
    }
    
    private void setIntentAndFinish(boolean finish, long  millis) {
        Intent intent = new Intent();
        intent.putExtra(Utils.EVENT_BEGIN_TIME, millis);
        setResult(WomenCalendarActivity.RESULT_OK, intent);
        if(finish) {
            finish();
        }
    }    
}
