package org.monroe.team.android.box.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class PushToListView extends ListView implements PushTouchDelegate.PushDelegateOwner{


    public PushToListView(Context context) {
        super(context);
    }

    public PushToListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PushToListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PushToListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        return getFirstVisiblePosition() == 0;
    }

}
