package com.njpalmin.womencalendar.chart;

import java.util.Date;

/**
 * A series for the date / time charts.
 */
public class TimeSeries extends XYSeries {

  public static final long DAY = 24 * 60 * 60 * 1000;

  /**
   * Builds a new date / time series.
   * 
   * @param title the series title
   */
  public TimeSeries(String title) {
    super(title);
  }

  /**
   * Adds a new value to the series.
   * 
   * @param x the date / time value for the X axis
   * @param y the value for the Y axis
   */
  public synchronized void add(Date x, double y) {
    super.add(x.getTime() / DAY + 1, y);
  }
  
  public static int daysBetweenDates(Date date1, Date date2) {
	  long time1 = date1.getTime();
	  long time2 = date2.getTime();
	  
	  return (int)(Math.abs(time1 - time2) % DAY);
  }
}
