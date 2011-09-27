package com.njpalmin.womencalendar.chart;

import android.view.MotionEvent;

/**
 * The main handler of the touch events.
 */
public class TouchHandler implements ITouchHandler {
  /** The chart renderer. */
  private DefaultRenderer mRenderer;
  /** The old x coordinate. */
  private float oldX;
  /** The old y coordinate. */
  private float oldY;
  /** The pan tool. */
  private Pan mPan;
  /** The graphical view. */
  private GraphicalView graphicalView;

  /**
   * Creates a new graphical view.
   * 
   * @param view the graphical view
   * @param chart the chart to be drawn
   */
  public TouchHandler(GraphicalView view, AbstractChart chart) {
    graphicalView = view;
    mRenderer = ((XYChart) chart).getRenderer();
    if (mRenderer.isPanEnabled()) {
      mPan = new Pan(chart);
    }
  }

  /**
   * Handles the touch event.
   * 
   * @param event the touch event
   */
  public boolean handleTouch(MotionEvent event) {
    int action = event.getAction();
    if (mRenderer != null && action == MotionEvent.ACTION_MOVE) {
      if (oldX >= 0 || oldY >= 0) {
        float newX = event.getX(0);
        float newY = event.getY(0);
        if (mRenderer.isPanEnabled()) {
          mPan.apply(oldX, oldY, newX, newY);
        }
        oldX = newX;
        oldY = newY;
        graphicalView.repaint();
        return true;
      }
    } else if (action == MotionEvent.ACTION_DOWN) {
      oldX = event.getX(0);
      oldY = event.getY(0);
    } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
      oldX = 0;
      oldY = 0;
      if (action == MotionEvent.ACTION_POINTER_UP) {
        oldX = -1;
        oldY = -1;
      }
    }
    return !mRenderer.isClickEnabled();
  }

  /**
   * Adds a new pan listener.
   * 
   * @param listener pan listener
   */
  public void addPanListener(PanListener listener) {
    if (mPan != null) {
      mPan.addPanListener(listener);
    }
  }

  /**
   * Removes a pan listener.
   * 
   * @param listener pan listener
   */
  public void removePanListener(PanListener listener) {
    if (mPan != null) {
      mPan.removePanListener(listener);
    }
  }
}