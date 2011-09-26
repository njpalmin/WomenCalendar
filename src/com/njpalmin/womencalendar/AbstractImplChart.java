package com.njpalmin.womencalendar;

import java.util.Date;
import java.util.List;

import com.njpalmin.womencalendar.chart.PointStyle;
import com.njpalmin.womencalendar.chart.TimeSeries;
import com.njpalmin.womencalendar.chart.XYMultipleSeriesDataset;
import com.njpalmin.womencalendar.chart.XYMultipleSeriesRenderer;
import com.njpalmin.womencalendar.chart.XYSeries;
import com.njpalmin.womencalendar.chart.XYSeriesRenderer;

import android.app.Activity;
import android.os.Bundle;

/**
 * An abstract class for the demo charts to extend.
 * It contains some methods for building datasets and renderers.
 */
public abstract class AbstractImplChart extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	    
	  setContentView(R.layout.bmtchart_main);
	    
	  initData();
	  initView();
	}
		
	abstract void initView();
	
	abstract void initData();

	/**
	 * Builds an XY multiple dataset using the provided values.
	 * 
	 * @param titles the series titles
	 * @param xValues the values for the X axis
	 * @param yValues the values for the Y axis
	 * @return the XY multiple dataset
	 */
	protected XYMultipleSeriesDataset buildDateset(List<Date[]> dateValues, List<long[]> days, List<double[]> yValues) {
	  XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	  addTimeSeries(dataset, dateValues, days, yValues, 0);
	  return dataset;
	}
	  
	/**
	 * 
	 * @param dataset
	 * @param titles
	 * @param dateValues
	 * @param yValues
	 * @param scale
	 */
	protected void addTimeSeries(XYMultipleSeriesDataset dataset, List<Date[]> dateValues,
	     List<long[]>days, List<double[]> yValues, int scale) {
	   for (int i = 0; i < dateValues.size(); i++) {
	     TimeSeries series = new TimeSeries();
	     Date[] dateV = dateValues.get(i);
	     double[] yV = yValues.get(i);
	     int seriesLength = dateV.length;
	     if (days != null) {
	       long[] dayV = days.get(i);
		   for (int j = 0; j < seriesLength; j++) {
		     series.add(dateV[j], dayV[j], yV[j]);
		   }
	     } else {
	       for (int k = 0; k < seriesLength; k++) {
		     series.add(dateV[k], yV[k]);
	       }
	     }
		 dataset.addSeries(series);    
	   }
	}
	  
	/**
	 * Sets a few of the series renderer settings.
	 * 
	 * @param renderer the renderer to set the properties to
	 * @param title the chart title
	 * @param xTitle the title for the X axis
	 * @param yTitle the title for the Y axis
	 * @param xMin the minimum value on the X axis
	 * @param xMax the maximum value on the X axis
	 * @param yMin the minimum value on the Y axis
	 * @param yMax the maximum value on the Y axis
	 * @param axesColor the axes color
	 * @param labelsColor the labels color
	 */
	protected void setChartSettings(XYMultipleSeriesRenderer renderer, double xMin, double xMax,
			double yMin, double yMax, int axesColor, int labelsColor) {
	  renderer.setXAxisMin(xMin);
	  renderer.setXAxisMax(xMax);
	  renderer.setYAxisMin(yMin);
	  renderer.setYAxisMax(yMax);
	  renderer.setAxesColor(axesColor);
	  renderer.setLabelsColor(labelsColor);
	}
	  
	/**
	 * Builds an XY multiple series renderer.
	 * 
	 * @param colors the series rendering colors
	 * @param styles the series point styles
	 * @return the XY multiple series renderers
	 */
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
	  XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	  setRenderer(renderer, colors, styles);
	  return renderer;
	}

	/**
	 * 
	 * @param renderer
	 * @param colors
	 * @param styles
	 */
	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
	  int length = colors.length;
	  for (int i = 0; i < length; i++) {
	    XYSeriesRenderer r = new XYSeriesRenderer();
	    r.setColor(colors[i]);
	    r.setPointStyle(styles[i]);
	    renderer.addSeriesRenderer(r);
	  }
	}
	
}
