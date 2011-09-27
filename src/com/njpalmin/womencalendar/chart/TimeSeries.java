package com.njpalmin.womencalendar.chart;

import java.util.Date;

/**
 * A series for the date / time charts.
 */
public class TimeSeries extends XYSeries {
  public static final long DAY = 24 * 60 * 60 * 1000;

  /**
   * Adds a new value to the series.
   * 
   * @param x the date / time value for the X axis
   * @param y the value for the Y axis
   */
  public void add(Date x, double y) {
    super.add(x.getTime() / DAY + 1, y);
  }
  
  /**
   * 
   * @param x
   * @param numOfDays
   * @param y
   */
  public void add(Date x, long numOfDays, double y) {
	long days = x.getTime() / DAY + 1;
	for (int i = 0; i < numOfDays; i++)
		super.add(days + i, y);
  }
}
