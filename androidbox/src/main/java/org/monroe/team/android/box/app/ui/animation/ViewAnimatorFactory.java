package org.monroe.team.android.box.app.ui.animation;

import android.animation.Animator;
import android.view.View;

public interface ViewAnimatorFactory<ValueType> {

    Animator create(View view, ValueType startValue, ValueType endValue, ValueSetter<ValueType> setter);
    long duration(ValueType startValue, ValueType endValue);

    public static interface ValueSetter<ValueType> {
        public void setValue(View view, ValueType value);
    }


    public static interface DurationProvider<ValueType> {
        public long duration(ValueType fromValue, ValueType toValue);
    }
}
