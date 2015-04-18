package org.monroe.team.android.box.app.ui;

import android.view.MotionEvent;
import android.view.View;

public abstract class SlideTouchGesture implements View.OnTouchListener{

    private final float slideLimit;
    private final Axis slideAxis;

    private float slideStartValue = -1;

    public SlideTouchGesture(float slideLimit, Axis slideAxis) {
        this.slideLimit = slideLimit;
        this.slideAxis = slideAxis;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!v.isEnabled() || v.getVisibility()!=View.VISIBLE) return false;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                captureInitialValue(event);
                notifyStartGesture(event);
            return true;

            case MotionEvent.ACTION_MOVE:
                float slideValue = calculateSlideValue(event);
                float fraction = Math.abs(slideValue / slideLimit);
                notifyGestureProgress(event, slideValue, fraction);
            return true;

            case MotionEvent.ACTION_UP:
                slideValue = calculateSlideValue(event);
                fraction = Math.abs(slideValue / slideLimit);
                notifyGestureProgress(event, slideValue, fraction);
                notifyGestureEnd(event, slideValue, fraction);
            return true;
        }
        return false;
    }

    private void notifyGestureEnd(MotionEvent event, float slideValue, float fraction) {
        if (fraction > applyFraction()){
            onApply(event.getX(), event.getY(), slideValue, fraction);
        } else {
            onCancel(event.getX(), event.getY(), slideValue, fraction);
        }
        onEnd(event.getX(), event.getY(), slideValue, fraction);
    }

    protected float applyFraction() {
        return 0.4f;
    }

    protected void onEnd(float x, float y, float slideValue, float fraction) {}

    private void notifyGestureProgress(MotionEvent event, float slideValue, float fraction) {
        onProgress(event.getX(), event.getY(), slideValue, fraction);
    }

    private float calculateSlideValue(MotionEvent event) {
        float answer = (slideStartValue - getCurrentValue(event));
        if (slideAxis.getDirectionSign() != 0) {
            answer = answer * slideAxis.getDirectionSign();
            if (answer < 0) {
                answer = 0;
            }
        }
        if (Math.abs(answer) > slideLimit) {
            answer = (answer > 0)? slideLimit : -slideLimit;
        }
        return answer;
    }

    private void notifyStartGesture(MotionEvent event) {
        onStart(event.getX(),event.getY());
    }

    private void captureInitialValue(MotionEvent event) {
        float value = getCurrentValue(event);
        slideStartValue = value;
    }

    private float getCurrentValue(MotionEvent event) {
        float value;
        if (slideAxis.isVertical()) {
            value = event.getRawY();
        }else {
            value = event.getRawX();
        }
        return value;
    }

    protected void onStart(float x, float y) {}
    protected void onProgress(float x, float y, float slideValue, float fraction){}
    protected void onApply(float x, float y, float slideValue, float fraction){}
    protected void onCancel(float x, float y, float slideValue, float fraction){}


    public static enum Axis{

        X (false,0), X_RIGHT(false, -1), Y_DOWN(true, -1), X_LEFT(false, 1), Y_UP(true, 1);

        private final boolean vertical;
        private final float sign;

        Axis(boolean vertical, float sign) {
            this.vertical = vertical;
            this.sign = sign;
        }

        public boolean isVertical() {
            return vertical;
        }

        public float getDirectionSign() {
            return sign;
        }
    }
}
