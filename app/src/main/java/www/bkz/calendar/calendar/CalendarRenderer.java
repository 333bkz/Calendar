package www.bkz.calendar.calendar;


import www.bkz.calendar.calendar.data.CalendarDate;
import www.bkz.calendar.calendar.data.Day;
import www.bkz.calendar.calendar.data.Week;
import www.bkz.calendar.calendar.util.DateUtils;

import static www.bkz.calendar.calendar.Calendar.ROW;
import static www.bkz.calendar.calendar.Calendar.VERTICAL;

public class CalendarRenderer {

    public void initDate(Week[] weeks, CalendarDate currentDate) {
        //上个月的天数
        int lastMonthDays = DateUtils.getMonthDays(currentDate.year, currentDate.month - 1);
        //当前月的天数
        int monthDays = DateUtils.getMonthDays(currentDate.year, currentDate.month);
        //当前月第一天在其周的位置
        int firstDayPosition = DateUtils.getFirstDayWeekPosition(
                currentDate.year,
                currentDate.month);
        int day = 0;
        for (int row = 0; row < ROW; row++) {
            day = fillWeek(weeks, currentDate, lastMonthDays, monthDays, firstDayPosition, day, row);
        }
    }

    private int fillWeek(Week[] weeks,
                         CalendarDate currentDate,
                         int lastMonthDays,
                         int currentMonthDays,
                         int firstDayWeek,
                         int day,
                         int row) {
        for (int vertical = 0; vertical < VERTICAL; vertical++) {
            int position = vertical + row * VERTICAL;// 单元格位置
            if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {
                day++;
                fillCurrentMonthDate(weeks, currentDate, day, row, vertical);
            } else if (position < firstDayWeek) {
                initLastMonth(weeks, currentDate, lastMonthDays, firstDayWeek, row, vertical, position);
            } else if (position >= firstDayWeek + currentMonthDays) {
                initNextMonth(weeks, currentDate, currentMonthDays, firstDayWeek, row, vertical, position);
            }
//            } else {
//                if (weeks[row] == null) {
//                    weeks[row] = new Week();
//                }
//            }
        }
        return day;
    }

    private void fillCurrentMonthDate(Week[] weeks,
                                      CalendarDate currentDate,
                                      int day,
                                      int row,
                                      int vertical) {
        CalendarDate date = currentDate.modifyDay(day);

        if (weeks[row] == null) {
            weeks[row] = new Week();
        }

        if (weeks[row].days[vertical] != null) {
            weeks[row].days[vertical].date = date;
            weeks[row].days[vertical].isCurrentMonth = true;
        } else {
            weeks[row].days[vertical] = new Day(true, date, row, vertical);
        }
    }

    private void initNextMonth(Week[] weeks,
                               CalendarDate currentDate,
                               int currentMonthDays,
                               int firstDayWeek,
                               int row,
                               int vertical,
                               int position) {
        CalendarDate date = new CalendarDate(
                currentDate.year,
                currentDate.month + 1,
                position - firstDayWeek - currentMonthDays + 1);
        if (weeks[row] == null) {
            weeks[row] = new Week();
        }
        if (weeks[row].days[vertical] != null) {
            weeks[row].days[vertical].date = date;
            weeks[row].days[vertical].isCurrentMonth = false;
        } else {
            weeks[row].days[vertical] = new Day(false, date, row, vertical);
        }
        //当下一个月的天数大于七时，说明该月有六周
//        if(position - firstDayWeek - currentMonthDays + 1 >= 7) { //当下一个月的天数大于七时，说明该月有六周
//        }
    }

    private void initLastMonth(Week[] weeks,
                               CalendarDate currentDate,
                               int lastMonthDays,
                               int firstDayWeek,
                               int row, int vertical, int position) {
        CalendarDate date = new CalendarDate(
                currentDate.year,
                currentDate.month - 1,
                lastMonthDays - (firstDayWeek - position - 1));
        if (weeks[row] == null) {
            weeks[row] = new Week();
        }
        if (weeks[row].days[vertical] != null) {
            weeks[row].days[vertical].date = date;
            weeks[row].days[vertical].isCurrentMonth = false;
        } else {
            weeks[row].days[vertical] = new Day(false, date, row, vertical);
        }
    }
}
