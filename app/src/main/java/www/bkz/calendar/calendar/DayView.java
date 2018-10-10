package www.bkz.calendar.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import www.bkz.calendar.R;
import www.bkz.calendar.calendar.data.CalendarDate;
import www.bkz.calendar.calendar.data.Day;

@SuppressLint("ViewConstructor")
@SuppressWarnings("FieldCanBeLocal")
public class DayView extends RelativeLayout {
    private final int layoutResource = R.layout.day_layout;
    private TextView dateTv;
    private View today_background, selected_background;
    private CalendarDate seedDate;
    private CalendarDate selectedDate;

    public DayView(Context context, CalendarDate seedDate) {
        super(context);
        View item = LayoutInflater.from(getContext()).inflate(layoutResource, this);
        item.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        item.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        dateTv = item.findViewById(R.id.day);
        today_background = item.findViewById(R.id.today_background);
        selected_background = item.findViewById(R.id.selected_background);
        this.seedDate = seedDate;
    }

    private void refreshContent(Day day, int w, int h) {
        measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());

        if (day != null
                && day.date != null) {
            dateTv.setText(String.valueOf(day.date.day));
            //当前页选中日期
            if (day.isCurrentMonth && day.date.equals(selectedDate)) {
                selected_background.setVisibility(VISIBLE);
            } else {
                selected_background.setVisibility(INVISIBLE);
            }
            //当前日期
            if (day.isCurrentMonth && day.date.equals(seedDate)) {
                today_background.setVisibility(VISIBLE);
            } else {
                today_background.setVisibility(INVISIBLE);
            }
            //当前页面月所属日期
            dateTv.setTextColor(day.isCurrentMonth ? 0xFF000000 : 0xAA888888);
        }
    }

    public void setSelectedDate(CalendarDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void draw(Canvas canvas, Day day, int w, int h) {
        refreshContent(day, w, h);
        int saveId = canvas.save();
        canvas.translate(day.col * w, day.row * h);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }

    private int getTranslateX(Canvas canvas, Day day) {
        int dx;
        int canvasWidth = canvas.getWidth() / 7;
        int viewWidth = getMeasuredWidth();
        int moveX = (canvasWidth - viewWidth) / 2;
        dx = day.col * canvasWidth + moveX;
        return dx;
    }
}
