package org.monroe.team.android.box.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RelativeLayoutExt extends RelativeLayout{
    public RelativeLayoutExt(Context context) {
        super(context);
    }

    public RelativeLayoutExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayoutExt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
