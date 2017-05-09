package com.stage3dev.verticalpager;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Uses a combination of a PageTransformer and swapping X & Y coordinates
 * of touch events to create the illusion of a vertically scrolling ViewPager.
 * <p>
 * Requires API 11+
 */
public class VerticalViewPager extends ViewPager implements NestedScrollingParent {

    private final NestedScrollingParentHelper parentHelper;

    public VerticalViewPager(Context context) {
        super(context);
        parentHelper = new NestedScrollingParentHelper(this);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        parentHelper = new NestedScrollingParentHelper(this);
        init();
    }

    private void init() {
        // The majority of the magic happens here
        setPageTransformer(false, new VerticalStackTransformer());
        // The easiest way to get rid of the overscroll drawing that happens on the left and right
        setOverScrollMode(OVER_SCROLL_NEVER);

    }

    public static class VerticalStackTransformer implements PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            //Suppress the default horizontal slide
            view.setTranslationX(view.getWidth() * -position);

            if (position <= 0) {
                view.setTranslationY(0f);
            } else {
                view.setTranslationY(position * view.getHeight());
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev); // return touch coordinates to original reference frame for any child views
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }

    /**
     * @return {@code false} since a vertical view pager can never be scrolled horizontally
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }

    /**
     * @return {@code true} iff a normal view pager would support horizontal scrolling at this time
     */
    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollHorizontally(direction);
    }


    /////////////////////////////////////////////////////
    // Nested scrolling handling
    ////////////////////////////////////////////////////

    private int xscroll = 0;

    private void acquireDrag() {
        if (!isFakeDragging()) {
//            Log.d(TAG, "beginFakeDrag()");
            xscroll = 0;
            beginFakeDrag();
        }
    }

    private void releaseDrag() {
        if (isFakeDragging()) {
//            Log.d(TAG, "endFakeDrag()");
            endFakeDrag();
            xscroll = 0;
        }
    }

    private void applyFakeDrag(int df) {
        if (this.canScrollVertically(df)) {
            acquireDrag();

            // have to swap sign to scroll vertically in the correct direction
            // Divide by 2 to slow down and smooth the fake drag
            fakeDragBy((-df / 2));
            xscroll += df;
        } else {
            releaseDrag();
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onStopNestedScroll(View child) {
        releaseDrag();
        parentHelper.onStopNestedScroll(child);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        if (isFakeDragging() && this.canScrollVertically(dy)) {
            //We may want to undo some of the fake dragging if the user switched drag directions
            if (!((xscroll == 0 && dy == 0) || (dy > 0 && xscroll > 0) || (dy < 0 && xscroll < 0))) {
                //The user has is scrolling in the opposite direction of a fake drag we have applied
                //Undo the fake drag before letting the nested scroll view take over again

                int amountToConsume;
                if (Math.abs(dy) <= Math.abs(xscroll)) {
                    //The difference is less than we have fake dragged so we can apply the whole thing
                    amountToConsume = dy;

                } else {
                    //The difference is more than we have fake dragged, only apply what we have done
                    amountToConsume = -xscroll;
                }

                applyFakeDrag(amountToConsume);
                consumed[1] = amountToConsume;
            }
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed != 0) {
            applyFakeDrag(dyUnconsumed);
        }
    }


    /*
        Delegated these methods to the the NestedScrollingParentHelper
     */

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        parentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public int getNestedScrollAxes() {
        return parentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }
}