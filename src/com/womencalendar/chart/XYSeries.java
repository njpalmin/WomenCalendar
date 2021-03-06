package com.womencalendar.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An XY series encapsulates values for XY charts like line, time, area,
 * scatter... charts.
 */
public class XYSeries implements Serializable {
  /** A list to contain the values for the X axis. */
  private List<Double> mX = new ArrayList<Double>();
  /** A list to contain the values for the Y axis. */
  private List<Double> mY = new ArrayList<Double>();
  /** The minimum value for the X axis. */
  private double mMinX = MathHelper.NULL_VALUE;
  /** The maximum value for the X axis. */
  private double mMaxX = -MathHelper.NULL_VALUE;
  /** The minimum value for the Y axis. */
  private double mMinY = MathHelper.NULL_VALUE;
  /** The maximum value for the Y axis. */
  private double mMaxY = -MathHelper.NULL_VALUE;
  /** The scale number for this series. */
  private int mScaleNumber;
  /** . */
  private float xPixelsPerUnit;
  
  /**
   * Builds a new XY series.
   * 
   * @param title the series title.
   */
  public XYSeries() {
    this(0);
  }

  /**
   * Builds a new XY series.
   * 
   * @param title the series title.
   * @param scaleNumber the series scale number
   */
  public XYSeries(int scaleNumber) {
    mScaleNumber = scaleNumber;
    initRange();
  }

  public int getScaleNumber() {
    return mScaleNumber;
  }

  /**
   * Initializes the range for both axes.
   */
  private void initRange() {
    mMinX = MathHelper.NULL_VALUE;
    mMaxX = -MathHelper.NULL_VALUE;
    mMinY = MathHelper.NULL_VALUE;
    mMaxY = -MathHelper.NULL_VALUE;
    int length = getItemCount();
    for (int k = 0; k < length; k++) {
      double x = getX(k);
      double y = getY(k);
      updateRange(x, y);
    }
  }

  /**
   * Updates the range on both axes.
   * 
   * @param x the new x value
   * @param y the new y value
   */
  private void updateRange(double x, double y) {
    mMinX = Math.min(mMinX, x);
    mMaxX = Math.max(mMaxX, x);
    mMinY = Math.min(mMinY, y);
    mMaxY = Math.max(mMaxY, y);
  }

  /**
   * Adds a new value to the series.
   * 
   * @param x the value for the X axis
   * @param y the value for the Y axis
   */
  public synchronized void add(double x, double y) {
    mX.add(x);
    mY.add(y);
    updateRange(x, y);
  }
  
  /**
   * Removes an existing value from the series.
   * 
   * @param index the index in the series of the value to remove
   */
  public synchronized void remove(int index) {
    double removedX = mX.remove(index);
    double removedY = mY.remove(index);
    if (removedX == mMinX || removedX == mMaxX || removedY == mMinY || removedY == mMaxY) {
      initRange();
    }
  }

  /**
   * Removes all the existing values from the series.
   */
  public synchronized void clear() {
    mX.clear();
    mY.clear();
    initRange();
  }

  /**
   * Returns the X axis value at the specified index.
   * 
   * @param index the index
   * @return the X value
   */
  public synchronized double getX(int index) {
    return mX.get(index);
  }

  /**
   * Returns the Y axis value at the specified index.
   * 
   * @param index the index
   * @return the Y value
   */
  public synchronized double getY(int index) {
    return mY.get(index);
  }

  /**
   * Returns the series item count.
   * 
   * @return the series item count
   */
  public synchronized int getItemCount() {
    return mX.size();
  }

  /**
   * Returns the minimum value on the X axis.
   * 
   * @return the X axis minimum value
   */
  public double getMinX() {
    return mMinX;
  }

  /**
   * Returns the minimum value on the Y axis.
   * 
   * @return the Y axis minimum value
   */
  public double getMinY() {
    return mMinY;
  }

  /**
   * Returns the maximum value on the X axis.
   * 
   * @return the X axis maximum value
   */
  public double getMaxX() {
    return mMaxX;
  }

  /**
   * Returns the maximum value on the Y axis.
   * 
   * @return the Y axis maximum value
   */
  public double getMaxY() {
    return mMaxY;
  }
  
  /**
   * 
   */
  public void setXPixelsPerUnit(float value) {
	xPixelsPerUnit = value;
  }
  
  /**
   * 
   * @return
   */
  public float getXPixelsPerUnit() {
	return xPixelsPerUnit;
  }
}
