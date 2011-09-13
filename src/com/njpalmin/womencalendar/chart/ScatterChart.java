package com.njpalmin.womencalendar.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

/**
 * The scatter chart rendering class.
 */
public class ScatterChart extends XYChart {
  /** The constant to identify this chart type. */
  public static final String TYPE = "Scatter";
  /** The default point shape size. */
  private static final float SIZE = 3;
  /** The legend shape width. */
  private static final int SHAPE_WIDTH = 10;
  /** The point shape size. */
  private float size = SIZE;

  ScatterChart() {
  }

  /**
   * Builds a new scatter chart instance.
   * 
   * @param dataset the multiple series dataset
   * @param renderer the multiple series renderer
   */
  public ScatterChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
    super(dataset, renderer);
    size = renderer.getPointSize();
  }

  /**
   * Set render for dataset.
   * 
   * @param dataset the multiple series dataset
   * @param renderer the multiple series renderer 
   */
  protected void setDatasetRenderer(XYMultipleSeriesDataset dataset,
      XYMultipleSeriesRenderer renderer) {
    super.setDatasetRenderer(dataset, renderer);
    size = renderer.getPointSize();
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
    XYSeriesRenderer renderer = (XYSeriesRenderer) seriesRenderer;
    paint.setColor(renderer.getColor());
    if (renderer.isFillPoints()) {
      paint.setStyle(Style.FILL);
    } else {
      paint.setStyle(Style.STROKE);
    }
    int length = points.length;
    switch (renderer.getPointStyle()) {
    case X:
      for (int i = 0; i < length; i += 2) {
        drawX(canvas, paint, points[i], points[i + 1]);
      }
      break;
    case CIRCLE:
      for (int i = 0; i < length; i += 2) {
        drawCircle(canvas, paint, points[i], points[i + 1]);
      }
      break;
    case TRIANGLE:
      float[] path = new float[6];
      for (int i = 0; i < length; i += 2) {
        drawTriangle(canvas, paint, path, points[i], points[i + 1]);
      }
      break;
    case SQUARE:
      for (int i = 0; i < length; i += 2) {
        drawSquare(canvas, paint, points[i], points[i + 1]);
      }
      break;
    case DIAMOND:
      path = new float[8];
      for (int i = 0; i < length; i += 2) {
        drawDiamond(canvas, paint, path, points[i], points[i + 1]);
      }
      break;
    case POINT:
      canvas.drawPoints(points, paint);
      break;
    }
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
   * The graphical representation of an X point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawX(Canvas canvas, Paint paint, float x, float y) {
    canvas.drawLine(x - size, y - size, x + size, y + size, paint);
    canvas.drawLine(x + size, y - size, x - size, y + size, paint);
  }

  /**
   * The graphical representation of a circle point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawCircle(Canvas canvas, Paint paint, float x, float y) {
    canvas.drawCircle(x, y, size, paint);
  }

  /**
   * The graphical representation of a triangle point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param path the triangle path
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawTriangle(Canvas canvas, Paint paint, float[] path, float x, float y) {
    path[0] = x;
    path[1] = y - size - size / 2;
    path[2] = x - size;
    path[3] = y + size;
    path[4] = x + size;
    path[5] = path[3];
    drawPath(canvas, path, paint, true);
  }

  /**
   * The graphical representation of a square point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawSquare(Canvas canvas, Paint paint, float x, float y) {
    canvas.drawRect(x - size, y - size, x + size, y + size, paint);
  }

  /**
   * The graphical representation of a diamond point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param path the diamond path
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawDiamond(Canvas canvas, Paint paint, float[] path, float x, float y) {
    path[0] = x;
    path[1] = y - size;
    path[2] = x - size;
    path[3] = y;
    path[4] = x;
    path[5] = y + size;
    path[6] = x + size;
    path[7] = y;
    drawPath(canvas, path, paint, true);
  }

  /**
   * Returns the chart type identifier.
   * 
   * @return the chart type
   */
  public String getChartType() {
    return TYPE;
  }

}