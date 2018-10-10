package www.bkz.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import www.bkz.calendar.calendar.Calendar;
import www.bkz.calendar.calendar.CalendarPager;
import www.bkz.calendar.calendar.data.CalendarDate;

public class MainActivity extends AppCompatActivity {
    TextView currentPageDateTv, selectedDateTv, currentDateTv;
    CalendarPager calendarPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentPageDateTv = findViewById(R.id.currentPageDate);
        selectedDateTv = findViewById(R.id.selectedDate);
        currentDateTv = findViewById(R.id.currentDate);
        calendarPager = findViewById(R.id.calendarPager);

        calendarPager.setPageTurningListener(new CalendarPager.PageTurningListener() {
            @Override
            public void onPageTurning(int orientation, CalendarDate currentPageDate) {
                currentPageDateTv.setText(currentPageDate.year + "-" + currentPageDate.month);
            }
        });

        calendarPager.setOnSelectDateListener(new Calendar.OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate selectedDate) {
                selectedDateTv.setText(selectedDate.toString());
            }
        });

        CalendarDate calendarDate = new CalendarDate();
        currentDateTv.setText(calendarDate.toString());
        currentPageDateTv.setText(calendarDate.year + "-" + calendarDate.month);
    }
}
