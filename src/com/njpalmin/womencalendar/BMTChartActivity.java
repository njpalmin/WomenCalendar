package com.njpalmin.womencalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.njpalmin.womencalendar.chart.BarChart;
import com.njpalmin.womencalendar.chart.CombinedXYChart;
import com.njpalmin.womencalendar.chart.GraphicalView;
import com.njpalmin.womencalendar.chart.LineChart;
import com.njpalmin.womencalendar.chart.PointStyle;
import com.njpalmin.womencalendar.chart.TimeSeries;
import com.njpalmin.womencalendar.chart.XYMultipleSeriesDataset;
import com.njpalmin.womencalendar.chart.XYMultipleSeriesRenderer;
import com.njpalmin.womencalendar.chart.XYSeries;
import com.njpalmin.womencalendar.chart.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.widget.LinearLayout;

/**
 * An activity that encapsulates a graphical view of the chart.
 */
public class BMTChartActivity extends Activity {
  /** The encapsulated graphical view. */
  private GraphicalView mView;
  XYMultipleSeriesDataset dataset = null;
  XYMultipleSeriesRenderer renderer = null;
  String[] types = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.bmtchart_main);
    
    initDebugCombineData();
    CombinedXYChart bmtChart = new CombinedXYChart(dataset, renderer, types);
    mView = new GraphicalView(this, bmtChart);
    LinearLayout chartLayout = (LinearLayout)findViewById(R.id.chart_layout);
    chartLayout.addView(mView);
  }
  
  private void initDebugCombineData() {
	    String[] titles = new String[] { "Crete Air Temperature" };
	    List<Date[]> dates = new ArrayList<Date[]>();
	    for (int i = 0; i < titles.length; i++) {
	      dates.add(new Date[12]);
	      dates.get(i)[0] = new Date(108, 9, 1);
	      dates.get(i)[1] = new Date(108, 9, 8);
	      dates.get(i)[2] = new Date(108, 9, 15);
	      dates.get(i)[3] = new Date(108, 9, 22);
	      dates.get(i)[4] = new Date(108, 9, 29);
	      dates.get(i)[5] = new Date(108, 10, 5);
	      dates.get(i)[6] = new Date(108, 10, 12);
	      dates.get(i)[7] = new Date(108, 10, 19);
	      dates.get(i)[8] = new Date(108, 10, 26);
	      dates.get(i)[9] = new Date(108, 11, 3);
	      dates.get(i)[10] = new Date(108, 11, 10);
	      dates.get(i)[11] = new Date(108, 11, 17);
	    }
	    List<double[]> values = new ArrayList<double[]>();
	    values.add(new double[] { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 });
	    int[] colors = new int[] { Color.GREEN };
	    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
	    renderer = buildRenderer(colors, styles);
	    renderer.setPointSize(5.5f);
	    int length = renderer.getSeriesRendererCount();

	    for (int i = 0; i < length; i++) {
	      XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
	      r.setLineWidth(2);
	      r.setFillPoints(true);
	    }
	    setChartSettings(renderer, "Weather data", "Month", "Temperature",
	    		dates.get(0)[0].getTime()/TimeSeries.DAY, 
	    		dates.get(0)[5].getTime()/TimeSeries.DAY,
	    		0, 40, Color.LTGRAY, Color.DKGRAY);

	    renderer.setXLabels(36);
	    renderer.setYLabels(10);
	    renderer.setShowGrid(true);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);  
		renderer.setApplyBackgroundColor(true);
	    renderer.setBackgroundColor(Color.WHITE);
	    renderer.setGridColor(Color.GRAY);
	    renderer.setLabelsTextSize(10);
	    renderer.setMargins(new int[] { 0, 30, 30, 0 });
	    renderer.setMarginsColor(Color.rgb(200, 174, 224));
	    renderer.setPanLimits(new double[] { 0, Integer.MAX_VALUE, 0, 40 });

	    TimeSeries waterSeries = new TimeSeries("Water Temperature");
	    waterSeries.add(dates.get(0)[0], 40);
	    waterSeries.add(dates.get(0)[1], 40);
	    waterSeries.add(dates.get(0)[2], 40);
	    waterSeries.add(dates.get(0)[3], 40);
	    waterSeries.add(dates.get(0)[4], 40);
	    waterSeries.add(dates.get(0)[5], 40);
	    waterSeries.add(dates.get(0)[6], 40);
	    waterSeries.add(dates.get(0)[7], 40);
	    waterSeries.add(dates.get(0)[8], 40);
	    waterSeries.add(dates.get(0)[9], 40);
	    waterSeries.add(dates.get(0)[10], 40);
	    waterSeries.add(dates.get(0)[11], 40);
	    renderer.setBarSpacing(0.1);
	    XYSeriesRenderer waterRenderer = new XYSeriesRenderer();
	    waterRenderer.setColor(Color.argb(250, 0, 210, 250));

	    dataset = buildDateset(titles, dates, values);
	    dataset.addSeries(0, waterSeries);
	    renderer.addSeriesRenderer(0, waterRenderer);
	    waterRenderer.setDisplayChartValues(true);
	    waterRenderer.setChartValuesTextSize(10);
	    
	    types = new String[] { BarChart.TYPE, LineChart.TYPE };
  }
  
  /**
   * Builds an XY multiple dataset using the provided values.
   * 
   * @param titles the series titles
   * @param xValues the values for the X axis
   * @param yValues the values for the Y axis
   * @return the XY multiple dataset
   */
  private XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
      List<double[]> yValues) {
    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    addXYSeries(dataset, titles, xValues, yValues, 0);
    return dataset;
  }

  /**
   * Builds an XY multiple dataset using the provided values.
   * 
   * @param titles the series titles
   * @param xValues the values for the X axis
   * @param yValues the values for the Y axis
   * @return the XY multiple dataset
   */
  private XYMultipleSeriesDataset buildDateset(String[] titles, List<Date[]> dateValues,
      List<double[]> yValues) {
    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    addTimeSeries(dataset, titles, dateValues, yValues, 0);
    return dataset;
  }
  
  private void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
      List<double[]> yValues, int scale) {
    int length = titles.length;
    for (int i = 0; i < length; i++) {
      XYSeries series = new XYSeries(titles[i], scale);
      double[] xV = xValues.get(i);
      double[] yV = yValues.get(i);
      int seriesLength = xV.length;
      for (int k = 0; k < seriesLength; k++) {
        series.add(xV[k], yV[k]);
      }
      dataset.addSeries(series);
    }
  }
  
  private void addTimeSeries(XYMultipleSeriesDataset dataset, String[] titles, List<Date[]> dateValues,
	      List<double[]> yValues, int scale) {
	    int length = titles.length;
	    for (int i = 0; i < length; i++) {
	      TimeSeries series = new TimeSeries(titles[i]);
	      Date[] dateV = dateValues.get(i);
	      double[] yV = yValues.get(i);
	      int seriesLength = dateV.length;
	      for (int k = 0; k < seriesLength; k++) {
	        series.add(dateV[k], yV[k]);
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
  protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
      String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
      int labelsColor) {
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
  private XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    setRenderer(renderer, colors, styles);
    return renderer;
  }

  private void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
    renderer.setAxisTitleTextSize(16);
    renderer.setLabelsTextSize(15);
    renderer.setPointSize(5f);
    renderer.setMargins(new int[] { 0, 30, 20, 0 });
    renderer.setMarginsColor(Color.rgb(200, 174, 224));
    renderer.setGridColor(Color.GRAY);
    renderer.setApplyBackgroundColor(true);
    renderer.setBackgroundColor(Color.WHITE);
    int length = colors.length;
    for (int i = 0; i < length; i++) {
      XYSeriesRenderer r = new XYSeriesRenderer();
      r.setColor(colors[i]);
      r.setPointStyle(styles[i]);
      renderer.addSeriesRenderer(r);
    }
  }
}

