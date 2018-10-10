package www.bkz.calendar.calendar.data;

public class Day {
    /**
     * 当前月
     */
    public boolean isCurrentMonth;

    public CalendarDate date;

    /**
     * 行
     */
    public int row;
    /**
     * 竖
     */
    public int col;

    public Day(boolean isCurrentMonth, CalendarDate date, int row, int col) {
        this.isCurrentMonth = isCurrentMonth;
        this.date = date;
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return date.toString();
    }
}
