package org.monroe.team.android.box.ui.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.util.Property;
import android.view.View;

public final class ViewObjectAnimatorFactories {

    private ViewObjectAnimatorFactories() {}

    public static final class FloatObjectViewAnimator extends ViewAnimatorFactorySupport<Float> {

        public FloatObjectViewAnimator(DurationProvider<Float> durationProvider, TimeInterpolator interpolator) {
            super(durationProvider, interpolator);
        }

        @Override
        protected Animator createInstance(final View view, final Float startValue, Float endValue, final ViewAnimatorFactory.ValueSetter<Float> setter) {
            return ObjectAnimator.ofFloat(view, new Property<View, Float>(Float.class, "") {
                private Float value = startValue;
                @Override
                public Float get(View object) {
                    return value;
                }

                @Override
                public void set(View object, Float value) {
                    this.value = value;
                    setter.setValue(view, value);
                }
            }, startValue, endValue);
        }
    }


    public static final class IntObjectViewAnimator extends ViewAnimatorFactorySupport<Integer> {

        public IntObjectViewAnimator(DurationProvider<Integer> durationProvider, TimeInterpolator interpolator) {
            super(durationProvider, interpolator);
        }

        @Override
        protected Animator createInstance(final View view, final Integer startValue, Integer endValue, final ViewAnimatorFactory.ValueSetter<Integer> setter) {
            return ObjectAnimator.ofInt(view, new Property<View, Integer>(Integer.class, "") {
                private Integer value = startValue;

                @Override
                public Integer get(View object) {
                    return value;
                }

                @Override
                public void set(View object, Integer value) {
                    this.value = value;
                    setter.setValue(view, value);
                }
            }, startValue, endValue);
        }
    }
}
