package com.njpalmin.womencalendar.chart;

import java.io.Serializable;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;

import com.njpalmin.womencalendar.chart.XYMultipleSeriesRenderer.Orientation;

/**
 * An abstract class to be implemented by the chart rendering classes.
 */
public abstract class AbstractChart implements Serializable {

  /**
   * The graphical representation of the chart.
   * 
   * @param canvas the canvas to paint to
   * @param x the top left x value of the view to draw to
   * @param y the top left y value of the view to draw to
   * @param width the width of the view to draw to
   * @param height the height of the view to draw to
   * @param paint the paint
   */
  public abstract void draw(Canvas canvas, int x, int y, int width, int height, Paint paint);

  /**
   * Draws the chart background.
   * 
   * @param renderer the chart renderer
   * @param canvas the canvas to paint to
   * @param x the top left x value of the view to draw to
   * @param y the top left y value of the view to draw to
   * @param width the width of the view to draw to
   * @param height the height of the view to draw to
   * @param paint the paint used for drawing
   * @param newColor if a new color is to be used
   * @param color the color to be used
   */
  protected void drawBackground(DefaultRenderer renderer, Canvas canvas, int x, int y, int width,
      int height, Paint paint, boolean newColor, int color) {
    if (renderer.isApplyBackgroundColor() || newColor) {
      if (newColor) {
        paint.setColor(color);
      } else {
        paint.setColor(renderer.getBackgroundColor());
      }
      paint.setStyle(Style.FILL);
      canvas.drawRect(x, y, x + width, y + height, paint);
    }
  }

  /**
   * Calculates if the current width exceeds the total width.
   * 
   * @param currentWidth the current width
   * @param renderer the renderer
   * @param right the right side pixel value
   * @param width the total width
   * @return if the current width exceeds the total width
   */
  protected boolean getExceed(float currentWidth, DefaultRenderer renderer, int right, int width) {
    boolean exceed = currentWidth > right;
    if (isVertical(renderer)) {
      exceed = currentWidth > width;
    }
    return exceed;
  }

  /**
   * Checks if the current chart is rendered as vertical.
   * 
   * @param renderer the renderer
   * @return if the chart is rendered as a vertical one
   */
  protected boolean isVertical(DefaultRenderer renderer) {
    return renderer instanceof XYMultipleSeriesRenderer
        && ((XYMultipleSeriesRenderer) renderer).getOrientation() == Orientation.VERTICAL;
  }

  /**
   * The graphical representation of a path.
   * 
   * @param canvas the canvas to paint to
   * @param points the points that are contained in the path to paint
   * @param paint the paint to be used for painting
   * @param circular if the path ends with the start point
   */
  protected void drawPath(Canvas canvas, float[] points, Paint paint, boolean circular) {
    Path path = new Path();
    path.moveTo(points[0], points[1]);
    for (int i = 2; i < points.length; i += 2) {
      path.lineTo(points[i], points[i + 1]);
    }
    if (circular) {
      path.lineTo(points[0], points[1]);
    }
    canvas.drawPath(path, paint);
  }

  /**
   * Calculates the best text to fit into the available space.
   * 
   * @param text the entire text
   * @param width the width to fit the text into
   * @param paint the paint
   * @return the text to fit into the space
   */
  private String getFitText(String text, float width, Paint paint) {
    String newText = text;
    int length = text.length();
    int diff = 0;
    while (paint.measureText(newText) > width && diff < length) {
      diff++;
      newText = text.substring(0, length - diff) + "...";
    }
    if (diff == length) {
      newText = "...";
    }
    return newText;
  }

  /**
   * Draws a text label.
   * 
   * @param canvas the canvas
   * @param labelText the label text
   * @param renderer the renderer
   * @param prevLabelsBounds the previous rendered label bounds
   * @param centerX the round chart center on X axis
   * @param centerY the round chart center on Y axis
   * @param shortRadius the short radius for the round chart
   * @param longRadius the long radius for the round chart
   * @param currentAngle the current angle
   * @param angle the label extra angle
   * @param left the left side
   * @param right the right side
   * @param paint the paint
   */
  protected void drawLabel(Canvas canvas, String labelText, DefaultRenderer renderer,
      List<RectF> prevLabelsBounds, int centerX, int centerY, float shortRadius, float longRadius,
      float currentAngle, float angle, int left, int right, Paint paint) {
    if (renderer.isShowLabels()) {
      paint.setColor(renderer.getLabelsColor());
      double rAngle = Math.toRadians(90 - (currentAngle + angle / 2));
      double sinValue = Math.sin(rAngle);
      double cosValue = Math.cos(rAngle);
      int x1 = Math.round(centerX + (float) (shortRadius * sinValue));
      int y1 = Math.round(centerY + (float) (shortRadius * cosValue));
      int x2 = Math.round(centerX + (float) (longRadius * sinValue));
      int y2 = Math.round(centerY + (float) (longRadius * cosValue));

      float size = renderer.getLabelsTextSize();
      float extra = Math.max(size / 2, 10);
      paint.setTextAlign(Align.LEFT);
      if (x1 > x2) {
        extra = -extra;
        paint.setTextAlign(Align.RIGHT);
      }
      float xLabel = x2 + extra;
      float yLabel = y2;
      float width = right - xLabel;
      if (x1 > x2) {
        width = xLabel - left;
      }
      labelText = getFitText(labelText, width, paint);
      float widthLabel = paint.measureText(labelText);
      boolean okBounds = false;
      while (!okBounds) {
        boolean intersects = false;
        int length = prevLabelsBounds.size();
        for (int j = 0; j < length && !intersects; j++) {
          RectF prevLabelBounds = prevLabelsBounds.get(j);
          if (prevLabelBounds.intersects(xLabel, yLabel, xLabel + widthLabel, yLabel + size)) {
            intersects = true;
            yLabel = Math.max(yLabel, prevLabelBounds.bottom);
          }
        }
        okBounds = !intersects;
      }

      y2 = (int) (yLabel - size / 2);
      canvas.drawLine(x1, y1, x2, y2, paint);
      canvas.drawLine(x2, y2, x2 + extra, y2, paint);
      canvas.drawText(labelText, xLabel, yLabel, paint);
      prevLabelsBounds.add(new RectF(xLabel, yLabel, xLabel + widthLabel, yLabel + size));
    }
  }

  /**
   * Given screen coordinates, returns the series and point indexes of a chart
   * element. If there is no chart element (line, point, bar, etc) at those
   * coordinates, null is returned.
   * 
   * @param screenPoint
   * @return
   */
  public SeriesSelection getSeriesAndPointForScreenCoordinate(Point screenPoint) {
    return null;
  }

}
