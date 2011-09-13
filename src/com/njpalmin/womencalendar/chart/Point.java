package com.njpalmin.womencalendar.chart;

import java.io.Serializable;

/**
 * A class to encapsulate the definition of a point.
 */
public final class Point implements Serializable {
  /** The X axis coordinate value. */
  private float mX;
  /** The Y axis coordinate value. */
  private float mY;
  
  public Point() {
  }
  
  public Point(float x, float y) {
    mX = x;
    mY = y;
  }
  
  public float getX() {
    return mX;
  }

  public float getY() {
    return mY;
  }
  
  public void setX(float x) {
    mX = x;
  }
  
  public void setY(float y) {
    mY = y;
  }
}
