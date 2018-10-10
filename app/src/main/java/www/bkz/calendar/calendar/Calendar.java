package www.bkz.calendar.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import www.bkz.calendar.calendar.data.CalendarDate;
import www.bkz.calendar.calendar.data.Day;
import www.bkz.calendar.calendar.data.Week;
import www.bkz.calendar.calendar.util.ScreenUtils;

public class Calendar extends View {
    public static final int ROW = 6;//6行
    public static final int VERTICAL = 7;//7竖

    protected Week[] currentPageWeeks;//当前页面数据
    protected CalendarDate seedDate;//当前日期
    protected CalendarDate currentPageDate;//当前页面日期
    protected CalendarDate selectedDate;//选中的日期

    private int cellWidth, cellHeight;//item 宽 高
    protected float touchSlop;
    //屏幕宽
    private int screenWidth;
    protected CalendarRenderer renderer;//日期计算类
    private DayView dayView;
    private OnSelectDateListener itemSelectListener;

    public Calendar(Context context) {
        this(context, null);
    }

    public Calendar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Calendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnSelectDateListener(OnSelectDateListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    protected void init(Context context) {
        currentPageWeeks = new Week[ROW];
        seedDate = new CalendarDate();
        currentPageDate = seedDate;

        //表示滑动的时候，手的移动要大于这个距离才开始移动控件。
        //如果小于这个距离就不触发移动控件，
        //如viewpager就是用这个距离来判断用户是否翻页
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        dayView = new DayView(context, seedDate);
        renderer = new CalendarRenderer();
        screenWidth = ScreenUtils.getScreenWidth(context);
    }

    //不使用
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        renderer.initDate(currentPageWeeks, currentPageDate);
        drawAllItem(canvas, currentPageWeeks);
    }

    //遍历weeks
    protected void drawAllItem(Canvas canvas, Week[] weeks) {
        for (int row = 0; row < ROW; row++) {//6行
            if (weeks[row] != null) {
                for (int col = 0; col < VERTICAL; col++) {//7竖
                    if (weeks[row].days[col] != null) {
                        drawDay(canvas, weeks[row].days[col]);
                    }
                }
            }
        }
    }

    private void drawDay(Canvas canvas, Day day) {
        dayView.draw(canvas, day, cellWidth, cellHeight);
    }

    private float posX = 0;
    private float posY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                posX = event.getX();
                posY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - posX;
                float disY = event.getY() - posY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (posX / cellWidth);
                    int row = (int) (posY / cellHeight);
                    onClickDate(col, row);
                }
                break;
        }
        return true;
    }

    private void onClickDate(int col, int row) {
        if (currentPageWeeks[row] != null) {
            final Week curWeek = currentPageWeeks[row];
            if (curWeek.days[col] != null) {
                final Day day = curWeek.days[col];
                if (day.isCurrentMonth) {
                    if (selectedDate == null
                            || !day.date.equals(selectedDate)) {
                        selectedDate = day.date;//指向选中日期
                        dayView.setSelectedDate(selectedDate);
                        postInvalidate();//刷新界面
                        if (itemSelectListener != null)
                            itemSelectListener.onSelectDate(selectedDate);
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //固定宽高
        int width = getProperSize(screenWidth, widthMeasureSpec);
        int height = width - width / VERTICAL;
        setMeasuredDimension(width, height);

        cellHeight = height / ROW;
        cellWidth = width / VERTICAL;
    }

    private int getProperSize(int screenWidth, int measureSpec) {
        int properSize = screenWidth;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:// 没有指定大小，设置为默认大小
                properSize = screenWidth;
                break;
            case MeasureSpec.EXACTLY:// 固定大小，无需更改其大小
                properSize = size;
                break;
            case MeasureSpec.AT_MOST://此处该值可以取小于等于最大值的任意值，此处取最大值的1/4
                properSize = size;
                break;
        }
        return properSize;
    }

    public interface OnSelectDateListener {
        /**
         * 日期选择回调
         */
        void onSelectDate(CalendarDate selectedDate);
    }
}
