package com.njpalmin.womencalendar.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * The view that encapsulates the graphical chart.
 */
public class GraphicalView extends View {
  /** The chart to be drawn. */
  private AbstractChart mChart;
  /** The chart renderer. */
  private DefaultRenderer mRenderer;
  /** The view bounds. */
  private Rect mRect = new Rect();
  /** The user interface thread handler. */
  private Handler mHandler;
  /** The zoom buttons rectangle. */
  /** The paint to be used when drawing the chart. */
  private Paint mPaint = new Paint();
  /** The touch handler. */
  private ITouchHandler mTouchHandler;
  /** The old x coordinate. */
  private float oldX;
  /** The old y coordinate. */
  private float oldY;

  /**
   * Creates a new graphical view.
   * 
   * @param context the context
   * @param chart the chart to be drawn
   */
  public GraphicalView(Context context, AbstractChart chart) {
    super(context);
    mChart = chart;
    mHandler = new Handler();
    mRenderer = ((XYChart) mChart).getRenderer();

    if (mRenderer instanceof XYMultipleSeriesRenderer
        && ((XYMultipleSeriesRenderer) mRenderer).getMarginsColor() == XYMultipleSeriesRenderer.NO_COLOR) {
      ((XYMultipleSeriesRenderer) mRenderer).setMarginsColor(mPaint.getColor());
    }
    
    mTouchHandler = new TouchHandler(this, mChart);
  }

  /**
   * Returns the current series selection object.
   * 
   * @return the series selection
   */
  public SeriesSelection getCurrentSeriesAndPoint() {
    return mChart.getSeriesAndPointForScreenCoordinate(new Point(oldX, oldY));
  }

  /**
   * Transforms the currently selected screen point to a real point.
   * 
   * @param scale the scale
   * @return the currently selected real point
   */
  public double[] toRealPoint(int scale) {
    if (mChart instanceof XYChart) {
      XYChart chart = (XYChart) mChart;
      return chart.toRealPoint(oldX, oldY, scale);
    }
    return null;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.getClipBounds(mRect);
    int top = mRect.top;
    int left = mRect.left;
    int width = mRect.width();
    int height = mRect.height();
    if (mRenderer.isInScroll()) {
      top = 0;
      left = 0;
      width = getMeasuredWidth();
      height = getMeasuredHeight();
    }
    mChart.draw(canvas, left, top, width, height, mPaint);
  }

  /**
   * Adds a new pan listener.
   * 
   * @param listener pan listener
   */
  public void addPanListener(PanListener listener) {
    mTouchHandler.addPanListener(listener);
  }

  /**
   * Removes a pan listener.
   * 
   * @param listener pan listener
   */
  public void removePanListener(PanListener listener) {
    mTouchHandler.removePanListener(listener);
  }
  
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      // save the x and y so they can be used in the click and long press
      // listeners
      oldX = event.getX();
      oldY = event.getY();
    }

    if (mRenderer != null && mRenderer.isPanEnabled()) {
      if (mTouchHandler.handleTouch(event)) {
        return true;
      }
    }
    return super.onTouchEvent(event);
  }

  /**
   * Schedule a view content repaint.
   */
  public void repaint() {
    mHandler.post(new Runnable() {
      public void run() {
        invalidate();
      }
    });
  }

  /**
   * Schedule a view content repaint, in the specified rectangle area.
   * 
   * @param left the left position of the area to be repainted
   * @param top the top position of the area to be repainted
   * @param right the right position of the area to be repainted
   * @param bottom the bottom position of the area to be repainted
   */
  public void repaint(final int left, final int top, final int right, final int bottom) {
    mHandler.post(new Runnable() {
      public void run() {
        invalidate(left, top, right, bottom);
      }
    });
  }

  /**
   * Saves the content of the graphical view to a bitmap.
   * 
   * @return the bitmap
   */
  public Bitmap toBitmap() {
    setDrawingCacheEnabled(false);
    if (!isDrawingCacheEnabled()) {
      setDrawingCacheEnabled(true);
    }
    if (mRenderer.isApplyBackgroundColor()) {
      setDrawingCacheBackgroundColor(mRenderer.getBackgroundColor());
    }
    setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    return getDrawingCache(true);
  }

}