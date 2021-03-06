package com.womencalendar.chart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

/**
 * The bar chart rendering class.
 */
public class BarChart extends XYChart {
  /** The constant to identify this chart type. */
  public static final String TYPE = "Bar";
  /** The chart type. */
  protected Type mType = Type.DEFAULT;

  /**
   * The bar chart type enum.
   */
  public enum Type {
    DEFAULT, STACKED;
  }

  BarChart() {
  }

  /**
   * Builds a new bar chart instance.
   * 
   * @param dataset the multiple series dataset
   * @param renderer the multiple series renderer
   * @param type the bar chart type
   */
  public BarChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer, Type type) {
    super(dataset, renderer);
    mType = type;
  }

  @Override
  protected RectF[] clickableAreasForPoints(float[] points, float yAxisValue, int seriesIndex) {
    int seriesNr = mDataset.getSeriesCount();
    int length = points.length;
    RectF[] ret = new RectF[length / 2];
    float halfDiffX = getHalfDiffX(points, length, seriesNr);
    for (int i = 0; i < length; i += 2) {
      float x = points[i];
      float y = points[i + 1];
      if (mType == Type.STACKED) {
        ret[i / 2] = new RectF(x - halfDiffX, y, x + halfDiffX, yAxisValue);
      } else {
        float startX = x - seriesNr * halfDiffX + seriesIndex * 2 * halfDiffX;
        ret[i / 2] = new RectF(startX, y, startX + 2 * halfDiffX, yAxisValue);
      }
    }
    return ret;
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
    int seriesNr = mDataset.getSeriesCount();
    int length = points.length;
    paint.setColor(seriesRenderer.getColor());
    paint.setStyle(Style.FILL);
    float halfDiffX = getHalfDiffX(points, length, seriesNr);
    for (int i = 0; i < length; i += 2) {
      float x = points[i];
      float y = points[i + 1];
      drawBar(canvas, x, yAxisValue, x, y, halfDiffX, seriesNr, seriesIndex, paint);
    }
    paint.setColor(seriesRenderer.getColor());
  }

  /**
   * Draws a bar.
   * @param canvas the canvas
   * @param xMin the X axis minimum
   * @param yMin the Y axis minimum
   * @param xMax the X axis maximum
   * @param yMax the Y axis maximum
   * @param halfDiffX half the size of a bar
   * @param seriesNr the total number of series
   * @param seriesIndex the current series index
   * @param paint the paint
   */
  protected void drawBar(Canvas canvas, float xMin, float yMin, float xMax, float yMax,
      float halfDiffX, int seriesNr, int seriesIndex, Paint paint) {
    int scale = mDataset.getSeriesAt(seriesIndex).getScaleNumber();
    if (mType == Type.STACKED) {
      drawBar(canvas, xMin - halfDiffX, yMax, xMax + halfDiffX, yMin, scale, seriesIndex, paint);
    } else {
      float startX = xMin - seriesNr * halfDiffX + seriesIndex * 2 * halfDiffX;
      drawBar(canvas, startX, yMax, startX + 2 * halfDiffX, yMin, scale, seriesIndex, paint);
    }
  }

  private void drawBar(Canvas canvas, float xMin, float yMin, float xMax, float yMax, int scale,
      int seriesIndex, Paint paint) {
    SimpleSeriesRenderer renderer = mRenderer.getSeriesRendererAt(seriesIndex);
    if (renderer.isGradientEnabled()) {
      float minY = (float) toScreenPoint(new double[] { 0, renderer.getGradientStopValue() }, scale)[1];
      float maxY = (float) toScreenPoint(new double[] { 0, renderer.getGradientStartValue() },
          scale)[1];
      float gradientMinY = Math.max(minY, yMin);
      float gradientMaxY = Math.min(maxY, yMax);
      int gradientMinColor = renderer.getGradientStopColor();
      int gradientMaxColor = renderer.getGradientStartColor();
      int gradientStartColor = gradientMaxColor;
      int gradientStopColor = gradientMinColor;

      if (yMin < minY) {
        paint.setColor(gradientMinColor);
        canvas.drawRect(Math.round(xMin), Math.round(yMin), Math.round(xMax),
            Math.round(gradientMinY), paint);
      } else {
        gradientStopColor = getGradientPartialColor(gradientMinColor, gradientMaxColor,
            (maxY - gradientMinY) / (maxY - minY));
      }
      if (yMax > maxY) {
        paint.setColor(gradientMaxColor);
        canvas.drawRect(Math.round(xMin), Math.round(gradientMaxY), Math.round(xMax),
            Math.round(yMax), paint);
      } else {
        gradientStartColor = getGradientPartialColor(gradientMaxColor, gradientMinColor,
            (gradientMaxY - minY) / (maxY - minY));
      }
      GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] {
          gradientStartColor, gradientStopColor });
      gradient.setBounds(Math.round(xMin), Math.round(gradientMinY), Math.round(xMax),
          Math.round(gradientMaxY));
      gradient.draw(canvas);
    } else {
      canvas
          .drawRect(Math.round(xMin), Math.round(yMin), Math.round(xMax), Math.round(yMax), paint);
    }
  }

  private int getGradientPartialColor(int minColor, int maxColor, float fraction) {
    int alpha = Math.round(fraction * Color.alpha(minColor) + (1 - fraction)
        * Color.alpha(maxColor));
    int r = Math.round(fraction * Color.red(minColor) + (1 - fraction) * Color.red(maxColor));
    int g = Math.round(fraction * Color.green(minColor) + (1 - fraction) * Color.green(maxColor));
    int b = Math.round(fraction * Color.blue(minColor) + (1 - fraction) * Color.blue((maxColor)));
    return Color.argb(alpha, r, g, b);
  }

  /**
   * The graphical representation of the series values as text.
   * 
   * @param canvas the canvas to paint to
   * @param series the series to be painted
   * @param renderer the series renderer
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesIndex the index of the series currently being drawn
   */
  protected void drawChartValuesText(Canvas canvas, XYSeries series, SimpleSeriesRenderer renderer,
      Paint paint, float[] points, int seriesIndex) {
    int seriesNr = mDataset.getSeriesCount();
    float halfDiffX = getHalfDiffX(points, points.length, seriesNr);
    for (int i = 0; i < points.length; i += 2) {
      int index = i / 2;
      if (series.getY(index) != MathHelper.NULL_VALUE) {
        float x = points[i];
        if (mType == Type.DEFAULT) {
          x += seriesIndex * 2 * halfDiffX - (seriesNr - 1.5f) * halfDiffX;
        }
        drawText(canvas, getLabel(series.getY(index)), x,
            points[i + 1] - renderer.getChartValuesSpacing(), paint, 0);
      }
    }
  }

  /**
   * Calculates and returns the half-distance in the graphical representation of
   * 2 consecutive points.
   * 
   * @param points the points
   * @param length the points length
   * @param seriesNr the series number
   * @return the calculated half-distance value
   */
  protected float getHalfDiffX(float[] points, int length, int seriesNr) { 
	return (float)(getDataset().getSeriesAt(0).getXPixelsPerUnit() / (2 * (1 + mRenderer.getBarSpacing())));
  }

  /**
   * Returns if the chart should display the null values.
   * 
   * @return if null values should be rendered
   */
  protected boolean isRenderNullValues() {
    return true;
  }

  /**
   * Returns the default axis minimum.
   * 
   * @return the default axis minimum
   */
  public double getDefaultMinimum() {
    return 0;
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
