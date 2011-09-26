package com.njpalmin.womencalendar.chart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

/**
 * The line chart rendering class.
 */
public class LineChart extends XYChart {
  /** The constant to identify this chart type. */
  public static final String TYPE = "Line";
  /** The scatter chart to be used to draw the data points. */
  private ScatterChart pointsChart;

  LineChart() {
  }

  /**
   * Builds a new line chart instance.
   * 
   * @param dataset the multiple series dataset
   * @param renderer the multiple series renderer
   */
  public LineChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
    super(dataset, renderer);
    pointsChart = new ScatterChart(dataset, renderer);
  }

  /**
   * Sets the series and the renderer.
   * 
   * @param dataset the series dataset
   * @param renderer the series renderer
   */
  protected void setDatasetRenderer(XYMultipleSeriesDataset dataset,
      XYMultipleSeriesRenderer renderer) {
    super.setDatasetRenderer(dataset, renderer);
    pointsChart = new ScatterChart(dataset, renderer);
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
    int length = points.length;
    XYSeriesRenderer renderer = (XYSeriesRenderer) seriesRenderer;
    float lineWidth = paint.getStrokeWidth();
    paint.setStrokeWidth(renderer.getLineWidth());
    if (renderer.isFillBelowLine()) {
      paint.setColor(renderer.getFillBelowLineColor());
      int pLength = points.length;
      float[] fillPoints = new float[pLength + 4];
      System.arraycopy(points, 0, fillPoints, 0, length);
      fillPoints[0] = points[0] + 1;
      fillPoints[length] = fillPoints[length - 2];
      fillPoints[length + 1] = yAxisValue;
      fillPoints[length + 2] = fillPoints[0];
      fillPoints[length + 3] = fillPoints[length + 1];
      paint.setStyle(Style.FILL);
      drawPath(canvas, fillPoints, paint, true);
    }
    paint.setColor(seriesRenderer.getColor());
    paint.setStyle(Style.STROKE);
    drawPath(canvas, points, paint, false);
    paint.setStrokeWidth(lineWidth);
  }

  @Override
  protected RectF[] clickableAreasForPoints(float[] points, float yAxisValue, int seriesIndex) {
    int length = points.length;
    RectF[] ret = new RectF[length / 2];
    for (int i = 0; i < length; i += 2) {
      int selectableBuffer = mRenderer.getSelectableBuffer();
      ret[i / 2] = new RectF(points[i] - selectableBuffer, points[i + 1] - selectableBuffer,
          points[i] + selectableBuffer, points[i + 1] + selectableBuffer);
    }
    return ret;
  }

  /**
   * Returns if the chart should display the points as a certain shape.
   * 
   * @param renderer the series renderer
   */
  public boolean isRenderPoints(SimpleSeriesRenderer renderer) {
    return ((XYSeriesRenderer) renderer).getPointStyle() != PointStyle.POINT;
  }

  /**
   * Returns the scatter chart to be used for drawing the data points.
   * 
   * @return the data points scatter chart
   */
  public ScatterChart getPointsChart() {
    return pointsChart;
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
    int length = xLabels.size();
    int[] margins = mRenderer.getMargins();
    boolean showLabels = mRenderer.isShowLabels();
    boolean showGrid = mRenderer.isShowGrid();
    Calendar calendar = Calendar.getInstance();
    int dayOfMonth;
    float textSize = paint.getTextSize();
    SimpleDateFormat dayFormat = null;
    SimpleDateFormat monthFormat = null;
    try {
      dayFormat = new SimpleDateFormat("d");
      monthFormat = new SimpleDateFormat("MM-yyyy");
    } catch (Exception e) {
      // do nothing here
    }
    for (int i = 0; i < length; i++) {
      double label = xLabels.get(i);
      float xLabel = (float) (left + xPixelsPerUnit * (label - minX));
      calendar.setTimeInMillis((long)(label * TimeSeries.DAY));
      dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
      if (showGrid) {
        paint.setColor(mRenderer.getGridColor());
        paint.setStrokeWidth((float)(0.5));
        if ((dayOfMonth == 1) || (dayOfMonth % 5== 0)) {
          paint.setColor(Color.BLACK);
          paint.setStrokeWidth(1);
        }
        canvas.drawLine(xLabel, bottom + margins[2], xLabel, top, paint);
      }
      if (showLabels && ((dayOfMonth == 1) || (dayOfMonth % 5 == 0))) {
        paint.setColor(mRenderer.getLabelsColor());
        drawText(canvas, dayFormat.format(calendar.getTime()), xLabel, bottom + margins[1] - mRenderer.getLabelsTextSize() / 3,
            paint, mRenderer.getXLabelsAngle());
        if (dayOfMonth == 1) {
          paint.setTextSize(textSize + 1);
          drawText(canvas, monthFormat.format(calendar.getTime()), xLabel + mRenderer.getLabelsTextSize() / 3,
        		  top + mRenderer.getLabelsTextSize() * 4 / 3, paint, mRenderer.getXLabelsAngle());
          paint.setTextSize(textSize);
        }
      }
    }
    drawXTextLabels(xTextLabelLocations, canvas, paint, showLabels, left, top, bottom, xPixelsPerUnit, minX, maxX);
  }
}
