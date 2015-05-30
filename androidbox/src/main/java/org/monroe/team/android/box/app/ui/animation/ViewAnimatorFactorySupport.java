package org.monroe.team.android.box.app.ui.animation;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.view.View;

public abstract class ViewAnimatorFactorySupport<ValueType> implements ViewAnimatorFactory<ValueType> {

    private final DurationProvider<ValueType> durationProvider;
    private final TimeInterpolator interpolator;

    protected ViewAnimatorFactorySupport(DurationProvider<ValueType> durationProvider, TimeInterpolator interpolator) {
        this.durationProvider = durationProvider;
        this.interpolator = interpolator;
    }

    @Override
    public Animator create(View view, ValueType startValue, ValueType endValue, ValueSetter<ValueType> setter) {
        Animator animator = createInstance(view,startValue,endValue,setter);
        animator.setDuration(durationProvider.duration(startValue, endValue));
        if(interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        return animator;
    }

    @Override
    public long duration(ValueType startValue, ValueType endValue) {
        return durationProvider.duration(startValue, endValue);
    }

    protected abstract Animator createInstance(View view, ValueType startValue, ValueType endValue, ValueSetter<ValueType> setter);

}
