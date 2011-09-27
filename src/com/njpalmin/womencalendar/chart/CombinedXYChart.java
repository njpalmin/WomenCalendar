package com.njpalmin.womencalendar.chart;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.njpalmin.womencalendar.chart.XYMultipleSeriesRenderer.Orientation;

/**
 * The combined XY chart rendering class.
 */
public class CombinedXYChart extends XYChart {
  /** The constant to identify this chart type. */
  private static final String TYPE = "Combined";
  /** The embedded XY charts. */
  private XYChart[] mCharts;
  /** The supported charts for being combined. */
  private Class[] xyChartTypes = new Class[] { LineChart.class, BarChart.class };
  /** .*/
  private int defSeriesIndex = 0;

  /**
   * Builds a new combined XY chart instance.
   * 
   * @param dataset the multiple series dataset
   * @param renderer the multiple series renderer
   * @param types the XY chart types
   */
  public CombinedXYChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer,
      String[] types) {
    super(dataset, renderer);
    int length = types.length;
    mCharts = new XYChart[length];
    for (int i = 0; i < length; i++) {
      try {
        mCharts[i] = getXYChart(types[i]);
      } catch (Exception e) {
        // ignore
      }
      if (mCharts[i] == null) {
        throw new IllegalArgumentException("Unknown chart type " + types[i]);
      } else {
        XYMultipleSeriesDataset newDataset = new XYMultipleSeriesDataset();
        newDataset.addSeries(dataset.getSeriesAt(i));
        mCharts[i].setDatasetRenderer(newDataset, renderer);
      }
    }
  }

  /**
   * Returns a chart instance based on the provided type.
   * 
   * @param type the chart type
   * @return an instance of a chart implementation
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  private XYChart getXYChart(String type) throws IllegalAccessException, InstantiationException {
    XYChart chart = null;
    int length = xyChartTypes.length;
    for (int i = 0; i < length && chart == null; i++) {
      XYChart newChart = (XYChart) xyChartTypes[i].newInstance();
      if (type.equals(newChart.getChartType())) {
        chart = newChart;
      }
    }
    return chart;
  }

  /**
   * The graphical representation of a series.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesRenderer the series renderer
   * @param yAxisValue the minimum value of the y axis
   * @param seriesIndex the index of the series currently being drawn
   */
  public void drawSeries(Canvas canvas, Paint paint, float[] points,
      SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
    mCharts[seriesIndex].setScreenR(getScreenR());
    mCharts[seriesIndex].setCalcRange(getCalcRange(mDataset.getSeriesAt(seriesIndex)
        .getScaleNumber()), 0);
    mCharts[seriesIndex].drawSeries(canvas, paint, points, seriesRenderer, yAxisValue, 0);
  }

  @Override
  protected RectF[] clickableAreasForPoints(float[] points, float yAxisValue, int seriesIndex) {
    return mCharts[seriesIndex].clickableAreasForPoints(points, yAxisValue, 0);
  }

  @Override
  protected void drawSeries(XYSeries series, Canvas canvas, Paint paint, List<Float> pointsList,
      SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, Orientation or) {
    mCharts[seriesIndex].setScreenR(getScreenR());
    mCharts[seriesIndex].setCalcRange(getCalcRange(mDataset.getSeriesAt(seriesIndex)
        .getScaleNumber()), 0);
    mCharts[seriesIndex].drawSeries(series, canvas, paint, pointsList, seriesRenderer, yAxisValue,
        0, or);
  }

  /**
   * Returns the chart type identifier.
   * 
   * @return the chart type
   */
  public String getChartType() {
    return TYPE;
  }
  
  /**
   * The graphical representation of the labels on the X axis.
   * 
   * @param xLabels the X labels values
   * @param xTextLabelLocations the X text label locations
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param left the left value of the labels area
   * @param top the top value of the labels area
   * @param bottom the bottom value of the labels area
   * @param xPixelsPerUnit the amount of pixels per one unit in the chart labels
   * @param minX the minimum value on the X axis in the chart
   * @param maxX the maximum value on the X axis in the chart
   */
  protected void drawXLabels(List<Double> xLabels, Double[] xTextLabelLocations, Canvas canvas,
      Paint paint, int left, int top, int bottom, double xPixelsPerUnit, double minX, double maxX) {
	  mCharts[defSeriesIndex].drawXLabels(xLabels, xTextLabelLocations, canvas, paint, left, top, bottom, xPixelsPerUnit, minX, maxX);
  }
  
  public void setDefSeriesIndex(int index) {
	  defSeriesIndex = index;
  }
}
