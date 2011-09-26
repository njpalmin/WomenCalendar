package com.njpalmin.womencalendar.chart;

import java.util.ArrayList;
import java.util.List;

/**
 * The pan tool.
 */
public class Pan extends AbstractTool {
  /** The pan listeners. */
  private List<PanListener> mPanListeners = new ArrayList<PanListener>();

  /**
   * Builds and instance of the pan tool.
   * 
   * @param chart the XY chart
   */
  public Pan(AbstractChart chart) {
    super(chart);
  }

  /**
   * Apply the tool.
   * 
   * @param oldX the previous location on X axis
   * @param oldY the previous location on Y axis
   * @param newX the current location on X axis
   * @param newY the current location on the Y axis
   */
  public void apply(float oldX, float oldY, float newX, float newY) {
    if (mChart instanceof XYChart) {
      int scales = mRenderer.getScalesCount();
      double[] limits = mRenderer.getPanLimits();
      boolean limited = limits != null && limits.length == 4;
      XYChart chart = (XYChart) mChart;
      for (int i = 0; i < scales; i++) {
        double[] range = getRange(i);
        double[] calcRange = chart.getCalcRange(i);
        if (range[0] == range[1] && calcRange[0] == calcRange[1] || range[2] == range[3]
            && calcRange[2] == calcRange[3]) {
          return;
        }
        checkRange(range, i);

        double[] realPoint = chart.toRealPoint(oldX, oldY, i);
        double[] realPoint2 = chart.toRealPoint(newX, newY, i);
        double deltaX = realPoint[0] - realPoint2[0];
        double deltaY = realPoint[1] - realPoint2[1];
        if (mRenderer.isPanXEnabled()) {
          if (limited) {
            boolean notLimitedLeft = limits[0] <= range[0] + deltaX;
            boolean notLimitedRight = limits[1] >= range[1] + deltaX;
            if (notLimitedLeft && notLimitedRight) {
              setXRange(range[0] + deltaX, range[1] + deltaX, i);
            }
          } else {
            setXRange(range[0] + deltaX, range[1] + deltaX, i);
          }
        }
        if (mRenderer.isPanYEnabled()) {
          if (limited) {
            boolean notLimitedBottom = limits[2] <= range[2] + deltaY;
            boolean notLimitedUp = limits[3] < range[3] + deltaY;
            if (notLimitedBottom && !notLimitedUp) {
              setYRange(range[2] + deltaY, range[3] + deltaY, i);
            }
          } else {
            setYRange(range[2] + deltaY, range[3] + deltaY, i);
          }
        }
      }
    }
    
    notifyPanListeners();
  }

  /**
   * Notify the pan listeners about a pan.
   */
  private synchronized void notifyPanListeners() {
    for (PanListener listener : mPanListeners) {
      listener.panApplied();
    }
  }

  /**
   * Adds a new pan listener.
   * 
   * @param listener pan listener
   */
  public synchronized void addPanListener(PanListener listener) {
    mPanListeners.add(listener);
  }

  /**
   * Removes a pan listener.
   * 
   * @param listener pan listener
   */
  public synchronized void removePanListener(PanListener listener) {
    mPanListeners.add(listener);
  }

}
