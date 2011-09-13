package com.njpalmin.womencalendar;

import android.util.MonthDisplayHelper;

public class DayOfMonthCursor extends MonthDisplayHelper {

    private int mRow;
    private int mColumn;
    
    /**
     * @param year The initial year.
     * @param month The initial month.
     * @param dayOfMonth The initial dayOfMonth.
     * @param weekStartDay What dayOfMonth of the week the week should start,
     *   in terms of {@link java.util.Calendar} constants such as
     *   {@link java.util.Calendar#SUNDAY}.
     */
    public DayOfMonthCursor(int year, int month, int dayOfMonth, int weekStartDay) {
        super(year, month, weekStartDay);
        mRow = getRowOf(dayOfMonth);
        mColumn = getColumnOf(dayOfMonth);
    }


    public int getSelectedRow() {
        return mRow;
    }

    public int getSelectedColumn() {
        return mColumn;
    }

    public void setSelectedRowColumn(int row, int col) {
        mRow = row;
        mColumn = col;
    }

    public int getSelectedDayOfMonth() {
        return getDayAt(mRow, mColumn);
    }

    /**
     * @return 0 if the selection is in the current month, otherwise -1 or +1
     * depending on whether the selection is in the first or last row.
     */
    public int getSelectedMonthOffset() {
        if (isWithinCurrentMonth(mRow, mColumn)) {
            return 0;
        }
        if (mRow == 0) {
            return -1;
        }
        return 1;
    }

    public void setSelectedDayOfMonth(int dayOfMonth) {
        mRow = getRowOf(dayOfMonth);
        mColumn = getColumnOf(dayOfMonth);
    }

    public boolean isSelected(int row, int column) {
        return (mRow == row) && (mColumn == column);
    }

    /**
     * Move up one box, potentially flipping to the previous month.
     * @return Whether the month was flipped to the previous month
     *   due to the move.
     */
    public boolean up() {
        if (isWithinCurrentMonth(mRow - 1, mColumn)) {
            // within current month, just move up
            mRow--;
            return false;
        }
        // flip back to previous month, same column, first position within month
        previousMonth();
        mRow = 5;
        while(!isWithinCurrentMonth(mRow, mColumn)) {
            mRow--;
        }
        return true;
    }

    /**
     * Move down one box, potentially flipping to the next month.
     * @return Whether the month was flipped to the next month
     *   due to the move.
     */
    public boolean down() {
        if (isWithinCurrentMonth(mRow + 1, mColumn)) {
            // within current month, just move down
            mRow++;
            return false;
        }
        // flip to next month, same column, first position within month
        nextMonth();
        mRow = 0;
        while (!isWithinCurrentMonth(mRow, mColumn)) {
            mRow++;
        }
        return true;
    }

    /**
     * Move left one box, potentially flipping to the previous month.
     * @return Whether the month was flipped to the previous month
     *   due to the move.
     */
    public boolean left() {
        if (mColumn == 0) {
            mRow--;
            mColumn = 6;
        } else {
            mColumn--;
        }

        if (isWithinCurrentMonth(mRow, mColumn)) {
            return false;
        }

        // need to flip to last day of previous month
        previousMonth();
        int lastDay = getNumberOfDaysInMonth();
        mRow = getRowOf(lastDay);
        mColumn = getColumnOf(lastDay);
        return true;
    }

    /**
     * Move right one box, potentially flipping to the next month.
     * @return Whether the month was flipped to the next month
     *   due to the move.
     */
    public boolean right() {
        if (mColumn == 6) {
            mRow++;
            mColumn = 0;
        } else {
            mColumn++;
        }

        if (isWithinCurrentMonth(mRow, mColumn)) {
            return false;
        }

        // need to flip to first day of next month
        nextMonth();
        mRow = 0;
        mColumn = 0;
        while (!isWithinCurrentMonth(mRow, mColumn)) {
            mColumn++;
        }
        return true;
    }
   
}
