package org.monroe.team.android.box.ui.animation;

import android.animation.Animator;
import android.view.View;

public interface ViewAnimatorFactory<ValueType> {

    Animator create(View view, ValueType startValue, ValueType endValue, ValueSetter<ValueType> setter);

    public static interface ValueSetter<ValueType> {
        public void setValue(View view, ValueType value);
    }
}
