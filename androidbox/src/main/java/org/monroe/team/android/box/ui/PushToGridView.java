package org.monroe.team.android.box.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

public class PushToGridView extends GridView implements PushTouchDelegate.PushDelegateOwner{

    public PushToGridView(Context context) {
        super(context);
    }

    public PushToGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PushToGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PushToGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private final PushTouchDelegate delegate = new PushTouchDelegate(this);

    public PushTouchDelegate.PushListener getPushListener() {
        return delegate.getPushListener();
    }

    public void setPushListener(PushTouchDelegate.PushListener pushListener) {
        delegate.setPushListener(pushListener);
    }


    public float getPushThreshold() {
        return delegate.getPushThreshold();
    }

    public void setPushThreshold(float pushThreshold) {
        this.delegate.setPushThreshold(pushThreshold);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (delegate.pushProcessing(event)) return true;
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (delegate.pushProcessing(event)) return true;
        return super.onTouchEvent(event);
    }

    @Override
    public boolean isScrollTopReached() {
        View c = getChildAt(0);
        float scrollYPx = 0;
        if (c == null) scrollYPx =0;
        else {
            scrollYPx = -c.getTop() + getFirstVisiblePosition() * c.getHeight();
        }
        return scrollYPx == 0;
    }

}
