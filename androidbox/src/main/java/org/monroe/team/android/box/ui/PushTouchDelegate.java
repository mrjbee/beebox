package org.monroe.team.android.box.ui;

import android.view.MotionEvent;

public class PushTouchDelegate {

    private float pushThreshold = 200;

    private final PushDelegateOwner owner;
    private float startY = 0;
    private boolean overPushAllowed = false;
    private boolean overPushStarted = false;
    private PushListener pushListener;


    public float getPushThreshold() {
        return pushThreshold;
    }

    public void setPushThreshold(float pushThreshold) {
        this.pushThreshold = pushThreshold;
    }

    public PushTouchDelegate(PushDelegateOwner owner) {
        this.owner = owner;
    }

    PushListener getPushListener() {
        return pushListener;
    }

    void setPushListener(PushListener pushListener) {
        this.pushListener = pushListener;
    }

    boolean pushProcessing(MotionEvent event) {

        if (pushListener == null) return false;

        float y = event.getY();
        float overPush = y - startY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                if (overPushAllowed) {

                    if (!overPushStarted && pushThreshold < overPush) {
                        overPushStarted = true;
                        pushListener.onOverPushStart(event.getRawX(), event.getRawY());
                    }

                    if (overPushStarted){
                        pushListener.onOverPush(event.getRawX(), event.getRawY(), overPush - pushThreshold);
                    }
                    if (overPushStarted){
                        return true;
                    }
                }
            }
            break;
            case MotionEvent.ACTION_DOWN: {
                startY = y;
                overPushStarted = false;
                overPushAllowed = owner.isScrollTopReached();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (overPushStarted) {
                    pushListener.onOverPushStop(event.getRawX(), event.getRawY(), overPush - pushThreshold);
                }
                overPushAllowed = false;
                overPushStarted = false;
                break;
            }

        }
        return false;
    }

    public static interface PushListener{
        void onOverPushStart(float x, float y);
        void onOverPush(float x, float y, float overPushValue);
        void onOverPushStop(float x, float y, float overPushValue);
    }

    static interface PushDelegateOwner {
        boolean isScrollTopReached();
    }
}
