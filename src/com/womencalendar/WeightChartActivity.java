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
public class WeightChartActivity extends AbstractImplChart {
    private final static double WEIGHT_MIN_VALUE = 48.0;
    private final static double WEIGHT_MAX_VALUE = 53.0;
    private final static int XLABELS_NUMBER = 36;
    private final static int YLABELS_NUMBER = 7;
    private final static double WEIGHT_INVALID_VALUE = -1.0;

    /** The encapsulated graphical view. */
    private GraphicalView mView;
    private XYMultipleSeriesDataset dataset = null;
    private XYMultipleSeriesRenderer renderer = null;
    private String[] types = new String[] { LineChart.TYPE };
    private double weightMaxValue, weightMinValue;
  
    void initView(){
        CombinedXYChart weightChart = new CombinedXYChart(dataset, renderer, types);
        mView = new GraphicalView(this, weightChart);
        LinearLayout chartLayout = (LinearLayout)findViewById(R.id.chart_layout);
        chartLayout.addView(mView);

        ImageView womanCalnedar = (ImageView)findViewById(R.id.top_calendar);
        womanCalnedar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                Intent intent = new Intent(WeightChartActivity.this, WomenCalendarActivity.class);
//                startActivity(intent);
                    finish();
                }
        });

        ImageView bmtChart = (ImageView)findViewById(R.id.top_bmt_chart);
        bmtChart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(WeightChartActivity.this, BMTChartActivity.class);
                    startActivity(intent);
                    finish();
                }
        });
    }

    void initData() {
        int[] colors = new int[] { Color.rgb(137, 0, 235) };
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
        renderer = buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();

        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
            r.setLineWidth(2);
            r.setFillPoints(true);
        }

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
        renderer.setPanLimits(new double[] { 0, Integer.MAX_VALUE, WEIGHT_MIN_VALUE, WEIGHT_MAX_VALUE });
        renderer.setYlabelSuffix("kg");

        int dataCount = 0;
        int count = 0;
        Time time = new Time();
        List<Date[]> dates = new ArrayList<Date[]>();
        List<double[]> values = new ArrayList<double[]>();

        weightMaxValue = WEIGHT_MIN_VALUE;
        weightMinValue = WEIGHT_MAX_VALUE;
        
       
        ContentResolver resolver = getContentResolver();
        Cursor cursor = 
            resolver.query(Record.CONTENT_URI, null, Record.TYPE + "='" + Utils.RECORD_TYPE_WEIGHT + "'", null, Record.DATE + " DESC");

        try {    	
            if (cursor != null) count = cursor.getCount();
            if (count != 0) {
                dates.add(new Date[count]);
                values.add(new double[count]);
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String date = cursor.getString(cursor.getColumnIndex(Record.DATE));
                    time.set(Utils.getMonthOfDayFromDate(date), Utils.getMonthFromDate(date)-1 , Utils.getYearFromDate(date));
                    time.normalize(true);
                    dates.get(0)[dataCount] = new Date(time.year - 1900, time.month, time.monthDay);
                    values.get(0)[dataCount] = cursor.getFloat(cursor.getColumnIndex(Record.FLOATVALUE));
                    if( values.get(0)[dataCount]  > weightMaxValue) {
                        weightMaxValue =  values.get(0)[dataCount];
                    }
                    
                    if( values.get(0)[dataCount] < weightMinValue) {
                        weightMinValue = values.get(0)[dataCount]; 
                    }
                    dataCount++;
                }
            }
        } finally {
            if (cursor == null || count == 0 || dataCount == 0) {
                dates.clear();
                values.clear();		
                dates.add(new Date[1]);
                values.add(new double[1]);

//                time.setToNow();
                time.monthDay -= 20;
                time.normalize(true);
                dates.get(0)[0] = new Date(time.year - 1900, time.month, time.monthDay);
                values.get(0)[0] = WEIGHT_INVALID_VALUE;
            }

            setChartSettings(renderer, dates.get(0)[0].getTime()/TimeSeries.DAY, 
                    dates.get(0)[0].getTime()/TimeSeries.DAY + XLABELS_NUMBER,
                    weightMinValue - 10.0, weightMaxValue + 10.0, Color.LTGRAY, Color.DKGRAY);

            dataset = buildDateset(dates, null, values);
            if (cursor != null) cursor.close();
        }
    }

}

