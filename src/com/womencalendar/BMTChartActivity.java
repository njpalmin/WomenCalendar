package com.womencalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.womencalendar.WomenCalendar.Record;
import com.womencalendar.chart.BarChart;
import com.womencalendar.chart.CombinedXYChart;
import com.womencalendar.chart.GraphicalView;
import com.womencalendar.chart.LineChart;
import com.womencalendar.chart.PointStyle;
import com.womencalendar.chart.TimeSeries;
import com.womencalendar.chart.XYMultipleSeriesDataset;
import com.womencalendar.chart.XYMultipleSeriesRenderer;
import com.womencalendar.chart.XYSeriesRenderer;
import com.womencalendar.utils.Utils;

/**
 * An activity that encapsulates a graphical view of the chart.
 */
public class BMTChartActivity extends AbstractImplChart {
	  private final static double BMT_MIN_VALUE = 35.0;
	  private final static double BMT_MAX_VALUE = 39.0;
	  private final static int XLABELS_NUMBER = 36;
	  private final static int YLABELS_NUMBER = 9;
	  private final static double BMT_INVALID_VALUE = -1.0;
	  
	  /** The encapsulated graphical view. */
	  private GraphicalView mView;
	  private XYMultipleSeriesDataset dataset = null;
	  private XYMultipleSeriesRenderer renderer = null;
	  private String[] types = new String[] { BarChart.TYPE, LineChart.TYPE };
	  private double bmtMaxValue, bmtMinValue;
  
	  void initView(){
	      CombinedXYChart bmtChart = new CombinedXYChart(dataset, renderer, types);
	      mView = new GraphicalView(this, bmtChart);
	      LinearLayout chartLayout = (LinearLayout)findViewById(R.id.chart_layout);
	      chartLayout.addView(mView);
	      
	      ImageView womanCalendar = (ImageView)findViewById(R.id.top_calendar);
	      womanCalendar.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
//	              Intent intent = new Intent(BMTChartActivity.this, WomenCalendarActivity.class);
//	              startActivity(intent);
	              finish();
	          }
	      });
	
	      ImageView weightChart = (ImageView)findViewById(R.id.top_weight_chart);
	      weightChart.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
	              Intent intent = new Intent(BMTChartActivity.this, WeightChartActivity.class);
	              startActivity(intent);
	              finish();
	          }
	      });
	  }
  
	  void initData() {
    	  int[] colors = new int[] { Color.RED };
    	    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
    	    renderer = buildRenderer(colors, styles);
    	    
    	    renderer.setPointSize((float)3.5);
    	    renderer.setXLabels(XLABELS_NUMBER);
    	    renderer.setYLabels(YLABELS_NUMBER);
    	    renderer.setShowGrid(true);
    	    renderer.setXLabelsAlign(Align.LEFT);
    	    renderer.setYLabelsAlign(Align.LEFT);  
    		renderer.setApplyBackgroundColor(true);
    	    renderer.setBackgroundColor(Color.WHITE);
    	    renderer.setGridColor(Color.GRAY);
    	    renderer.setLabelsTextSize(10);
    	    renderer.setMargins(new int[] { 0, 30, 30, 0 });
    	    renderer.setMarginsColor(Color.rgb(200, 174, 224));
    	    renderer.setPanLimits(new double[] { 0, Integer.MAX_VALUE, BMT_MIN_VALUE, BMT_MAX_VALUE});
    	    renderer.setBarSpacing(0.5);
    	    renderer.setYlabelSuffix("\u00b0");
	    
    	    TimeSeries BMTSeries = new TimeSeries();
    	    XYSeriesRenderer BMTRenderer = new XYSeriesRenderer();
    	    BMTRenderer.setPointStyle(PointStyle.CIRCLE);
    	    BMTRenderer.setFillPoints(true);
    	    BMTRenderer.setColor(Color.rgb(137, 0, 235));
    	    
    	    int dataCount = 0;
    	    int dataCountOfBmt = 0;
        	int count = 0;
        	int countOfBmt = 0;
        	Time time = new Time();
        	List<Date[]> dates = new ArrayList<Date[]>();
        	List<double[]> values = new ArrayList<double[]>();
        	List<long[]> days = new ArrayList<long[]>();
        	
        	bmtMaxValue = BMT_MAX_VALUE;
        	bmtMinValue = BMT_MIN_VALUE;
    	    ContentResolver resolver = getContentResolver();
    	    Cursor cursor = 
    	    	resolver.query(Record.CONTENT_URI, null, Record.TYPE + "='" + Utils.DAY_TYPE_START + "'", null, Record.DATE + " DESC");
    	    Cursor cursorOfBmt =
    		    resolver.query(Record.CONTENT_URI, null, Record.TYPE + "='" + Utils.RECORD_TYPE_BMT + "'", null, Record.DATE + " DESC");
    	    
    	    try {    	
    	    	if (cursor != null) count = cursor.getCount();
    	    	if (count != 0) {
    	    		dates.add(new Date[count]);
    	    		values.add(new double[count]);
    	    		days.add(new long[count]);
    	    		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
    	    		    String date = cursor.getString(cursor.getColumnIndex(Record.DATE));
    	    		    time.set(Utils.getMonthOfDayFromDate(date), Utils.getMonthFromDate(date)-1 , Utils.getYearFromDate(date));
    	    		    time.normalize(true);
//    	    			time.set(((long)cursor.getInt(cursor.getColumnIndex(Record.DATE))) * 1000);
    	    			dates.get(0)[dataCount] = new Date(time.year - 1900, time.month, time.monthDay);
    	    			values.get(0)[dataCount] = BMT_MAX_VALUE;
//    	    			values.get(0)[dataCount] = cursor.getFloat(cursor.getColumnIndex(Record.FLOATVALUE));
//    	    			if(cursor.getFloat(cursor.getColumnIndex(Record.FLOATVALUE)) > BMT_MAX_VALUE ){
//    	    			    BmtMaxValue = values.get(0)[dataCount]; 	    			    
//    	    			}
    	    			days.get(0)[dataCount] = time.toMillis(true)/TimeSeries.DAY;//cursor.getInt(cursor.getColumnIndex(Record.INTVALUE)) * 1000 / TimeSeries.DAY;
    	    			dataCount++;
    	    		}
    	    	}
    	    	
    	    	if (cursorOfBmt != null) countOfBmt = cursorOfBmt.getCount();
    	    	if (countOfBmt != 0) {
    	    		while(cursorOfBmt.moveToNext()) {
//    	    			time.set(((long)cursorOfBmt.getInt(cursorOfBmt.getColumnIndex(Record.DATE))) * 1000);
    	    		    String date = cursorOfBmt.getString(cursor.getColumnIndex(Record.DATE));
                        time.set(Utils.getMonthOfDayFromDate(date), Utils.getMonthFromDate(date)-1 , Utils.getYearFromDate(date));
                        time.normalize(true);
                        if(cursorOfBmt.getDouble(cursorOfBmt.getColumnIndex(Record.FLOATVALUE)) > bmtMaxValue) {
                            bmtMaxValue = cursorOfBmt.getDouble(cursorOfBmt.getColumnIndex(Record.FLOATVALUE));
                        }
                        if(cursorOfBmt.getDouble(cursorOfBmt.getColumnIndex(Record.FLOATVALUE)) < bmtMinValue) {
                            bmtMinValue = cursorOfBmt.getDouble(cursorOfBmt.getColumnIndex(Record.FLOATVALUE));
                        }
                        
    	    			BMTSeries.add(new Date(time.year - 1900, time.month, time.monthDay) ,
    	    					cursorOfBmt.getFloat(cursorOfBmt.getColumnIndex(Record.FLOATVALUE)));			
    	    			dataCountOfBmt++;
    	    		}
    	    	}
    	    } finally {
    	        time.monthDay -= 20;
    	        time.normalize(true);
//    	    	time.setToNow();
    	    	if (count == 0 || dataCount == 0) {
    	    		dates.clear();
    	    		values.clear();
    	    		days.clear();
    	    		days = null;
    	    		dates.add(new Date[1]);
    	    		values.add(new double[1]);
    	    		
    	    		dates.get(0)[0] = new Date(time.year - 1900, time.month, time.monthDay);
    	    		values.get(0)[0] = BMT_INVALID_VALUE;
    	    	}
    	    	
    	    	if (countOfBmt == 0 || dataCountOfBmt == 0) { 
    	    		BMTSeries.add(dates.get(0)[0], BMT_INVALID_VALUE);
    	    	}
    	    	
    	    	setChartSettings(renderer, dates.get(0)[0].getTime()/TimeSeries.DAY, 
    		    		dates.get(0)[0].getTime()/TimeSeries.DAY + XLABELS_NUMBER,
    		    		bmtMinValue - 2.0, bmtMaxValue + 2.0, Color.LTGRAY, Color.DKGRAY);
    
    		    dataset = buildDateset(dates, days, values);
    		    dataset.addSeries(1, BMTSeries);
    		    renderer.addSeriesRenderer(1, BMTRenderer);
    		    
    	    	if (cursor != null) cursor.close();
    	    	if (cursorOfBmt != null) cursorOfBmt.close();
    	    }   	    
	  }
}

