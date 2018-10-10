package www.bkz.calendar.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;

import www.bkz.calendar.calendar.data.CalendarDate;
import www.bkz.calendar.calendar.data.Week;

public class CalendarPager extends Calendar {
    private Scroller mScroller;
    private int index;//坐标
    private int criticalWidth;//
    private int width;
    private PageTurningListener pageTurningListener;
    private boolean isScrollComplete = true;//是否完成滚动
    private Week[] lastPageWeeks;
    private Week[] nextPageWeeks;

    public void setPageTurningListener(PageTurningListener listener) {
        this.pageTurningListener = listener;
    }

    public CalendarPager(Context context) {
        super(context);
    }

    public CalendarPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mScroller = new Scroller(context);

        lastPageWeeks = new Week[ROW];
        nextPageWeeks = new Week[ROW];

        refreshDate();
    }

    private void refreshDate() {
        renderer.initDate(lastPageWeeks, currentPageDate.modifyMonth(-1));
        renderer.initDate(currentPageWeeks, currentPageDate);
        renderer.initDate(nextPageWeeks, currentPageDate.modifyMonth(1));
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            if (isScrollComplete) {
                isScrollComplete = false;
                requestLayout();
            }
        }
    }

    private int lastPointX;
    private int lastMoveX;
    private boolean isEvent;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                isEvent = false;
                lastPointX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(lastPointX - event.getX()) > touchSlop) {
                    isEvent = true;
                }
                if (isEvent) {
                    int totalMoveX = (int) (lastPointX - event.getX()) + lastMoveX;
                    smoothScrollTo(totalMoveX);
                    return true;//拦截
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isEvent) {
                    if (Math.abs(lastPointX - event.getX()) > touchSlop) {
                        int orientation = 0;
                        if (lastPointX > event.getX() &&
                                Math.abs(lastPointX - event.getX()) >= criticalWidth) {
                            index++;
                            orientation = 1;
                        } else if (lastPointX < event.getX() &&
                                Math.abs(lastPointX - event.getX()) >= criticalWidth) {
                            index--;
                            orientation = -1;
                        }

                        if (orientation != 0) {
                            currentPageDate = currentPageDate.modifyMonth(orientation);
                            refreshDate();
                            if (pageTurningListener != null)
                                pageTurningListener.onPageTurning(orientation, currentPageDate);
                        }

                        smoothScrollTo(width * index);
                        lastMoveX = width * index;
                        return true;//拦截
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        width = w;
        criticalWidth = (int) (1F / 3F * width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        draw(canvas, width * (index - 1), lastPageWeeks);
        draw(canvas, width * index, currentPageWeeks);
        draw(canvas, width * (index + 1), nextPageWeeks);
    }

    private void draw(Canvas canvas, int x, Week[] weeks) {
        canvas.save();
        canvas.translate(x, 0);
        super.drawAllItem(canvas, weeks);
        canvas.restore();
    }

    private void smoothScrollTo(int fx) {
        int dx = fx - mScroller.getFinalX();
        int dy = -mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        invalidate();
    }

    public void scroll(int orientation) {
        index += orientation;
        smoothScrollTo(width * index);
        lastMoveX = width * index;
    }

    public interface PageTurningListener {
        void onPageTurning(int orientation, CalendarDate currentPageDate);
    }
}
