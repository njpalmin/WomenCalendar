package com.njpalmin.womencalendar.chart;

public class SeriesSelection {
  private int mSeriesIndex;

  private int mPointIndex;

  private double mXValue;

  private double mValue;

  public SeriesSelection(int seriesIndex, int pointIndex, double xValue, double value) {
    mSeriesIndex = seriesIndex;
    mPointIndex = pointIndex;
    mXValue = xValue;
    mValue = value;
  }

  public int getSeriesIndex() {
    return mSeriesIndex;
  }

  public int getPointIndex() {
    return mPointIndex;
  }

  public double getXValue() {
    return mXValue;
  }

  public double getValue() {
    return mValue;
  }
}