package org.monroe.team.android.box.ui.animation.apperrance;

import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import org.monroe.team.android.box.ui.animation.ViewAnimatorFactory;
import org.monroe.team.android.box.ui.animation.ViewAnimatorFactorySupport;
import org.monroe.team.android.box.ui.animation.ViewObjectAnimatorFactories;

public final class AppearanceControllerBuilder<TypeValue> {

    private final View animationView;
    private int visibilityOnHide = View.VISIBLE;

    private final TypeBuilder<TypeValue> typeBuilder;
    private ViewAnimatorFactory<TypeValue> showAnimationFactory;
    private ViewAnimatorFactory<TypeValue>  hideAnimationFactory;

    private AppearanceControllerBuilder(View animationView, TypeBuilder<TypeValue> typeBuilder) {
        this.animationView = animationView;
        this.typeBuilder = typeBuilder;
    }

    public static AppearanceController combine(AppearanceControllerBuilder<?> ... appearanceControllerBuilders){
        AppearanceController[] appearanceControllers = new AppearanceController[appearanceControllerBuilders.length];
        for (int i = 0; i < appearanceControllerBuilders.length; i++) {
            appearanceControllers[i]=appearanceControllerBuilders[i].build();
        }
        return CombinedAppearanceController.combine(appearanceControllers);
    }

    public static <PropertyType> AppearanceControllerBuilder<PropertyType> animateAppearance(View view, TypeBuilder<PropertyType> property){
        return new AppearanceControllerBuilder<PropertyType>(view, property);
    }

    public AppearanceControllerBuilder<TypeValue> hideAndGone(){
        visibilityOnHide = View.GONE;
        return this;
    }

    public AppearanceControllerBuilder<TypeValue> hideAndInvisible(){
        visibilityOnHide = View.INVISIBLE;
        return this;
    }

    public AppearanceControllerBuilder<TypeValue> showAnimation(ViewAnimatorFactorySupport.DurationProvider<? super TypeValue> duration){
        showAnimationFactory = createAnimator( duration, TimeInterpreterBuilder.NO_OP);
        return this;
    }

    public AppearanceControllerBuilder<TypeValue> hideAnimation(ViewAnimatorFactorySupport.DurationProvider<? super TypeValue> duration){
        hideAnimationFactory = createAnimator(duration, TimeInterpreterBuilder.NO_OP);
        return this;
    }

    public AppearanceControllerBuilder<TypeValue> showAnimation(ViewAnimatorFactorySupport.DurationProvider<? super TypeValue> duration, TimeInterpreterBuilder builder){
        showAnimationFactory = createAnimator(duration, builder);
        return this;
    }

    public AppearanceControllerBuilder<TypeValue> hideAnimation(ViewAnimatorFactorySupport.DurationProvider<? super TypeValue> duration, TimeInterpreterBuilder builder){
        hideAnimationFactory = createAnimator(duration, builder);
        return this;
    }

    public static <TypeValue> ViewAnimatorFactorySupport.DurationProvider<TypeValue> duration_constant(final long ms){
        return new ViewAnimatorFactorySupport.DurationProvider<TypeValue>() {
            @Override
            public long duration(TypeValue fromValue, TypeValue toValue) {
                return ms;
            }
        };
    }

    public static ViewAnimatorFactorySupport.DurationProvider<Float> duration_auto_fint(){
        return new ViewAnimatorFactorySupport.DurationProvider<Float>() {
            @Override
            public long duration(Float fromValue, Float toValue) {
                long ms = (long) Math.abs(fromValue - toValue);
                ms = msLimitsCheck(ms);
                return ms;
            }
        };
    }

    public static ViewAnimatorFactorySupport.DurationProvider<Float> autoFloat(){
        return new ViewAnimatorFactorySupport.DurationProvider<Float>() {
            @Override
            public long duration(Float fromValue, Float toValue) {
                long ms = (long) (Math.abs(fromValue - toValue) * 1000);
                ms = msLimitsCheck(ms);
                return ms;
            }
        };
    }

    public static TypeBuilder<Float> xSlide(final float showValue, final float toValue){
        return new TypeBuilder<Float>() {
            @Override
            public DefaultAppearanceController.ValueGetter<Float> buildValueGetter() {
                return new DefaultAppearanceController.ValueGetter<Float>() {
                    @Override
                    public Float getShowValue() {
                        return showValue;
                    }

                    @Override
                    public Float getHideValue() {
                        return toValue;
                    }

                    @Override
                    public Float getCurrentValue(View view) {
                        return view.getTranslationX();
                    }
                };
            }

            @Override
            public TypedValueSetter<Float> buildValueSetter() {
                return new TypedValueSetter<Float>(Float.class) {
                    @Override
                    public void setValue(View view, Float value) {
                        view.setTranslationX(value);
                    }
                };
            }
        };
    }

    public static TypeBuilder<Float> scale(final float showValue, final float toValue){
        return new TypeBuilder<Float>() {
            @Override
            public DefaultAppearanceController.ValueGetter<Float> buildValueGetter() {
                return new DefaultAppearanceController.ValueGetter<Float>() {
                    @Override
                    public Float getShowValue() {
                        return showValue;
                    }

                    @Override
                    public Float getHideValue() {
                        return toValue;
                    }

                    @Override
                    public Float getCurrentValue(View view) {
                        return view.getScaleX();
                    }
                };
            }

            @Override
            public TypedValueSetter<Float> buildValueSetter() {
                return new TypedValueSetter<Float>(Float.class) {
                    @Override
                    public void setValue(View view, Float value) {
                        view.setScaleX(value);
                        view.setScaleY(value);
                    }
                };
            }
        };
    }

    public static TypeBuilder<Float> rotate(final float showValue, final float toValue){
        return new TypeBuilder<Float>() {
            @Override
            public DefaultAppearanceController.ValueGetter<Float> buildValueGetter() {
                return new DefaultAppearanceController.ValueGetter<Float>() {
                    @Override
                    public Float getShowValue() {
                        return showValue;
                    }

                    @Override
                    public Float getHideValue() {
                        return toValue;
                    }

                    @Override
                    public Float getCurrentValue(View view) {
                        return view.getRotation();
                    }
                };
            }

            @Override
            public TypedValueSetter<Float> buildValueSetter() {
                return new TypedValueSetter<Float>(Float.class) {
                    @Override
                    public void setValue(View view, Float value) {
                        view.setRotation(value);
                    }
                };
            }
        };
    }


    public static TypeBuilder<Integer> heightSlide(final int showValue, final int hideValue){
        return new TypeBuilder<Integer>() {
            @Override
            public DefaultAppearanceController.ValueGetter<Integer> buildValueGetter() {
                return new DefaultAppearanceController.ValueGetter<Integer>() {
                    @Override
                    public Integer getShowValue() {
                        return showValue;
                    }

                    @Override
                    public Integer getHideValue() {
                        return hideValue;
                    }

                    @Override
                    public Integer getCurrentValue(View view) {
                        return view.getLayoutParams().height;
                    }
                };
            }

            @Override
            public TypedValueSetter<Integer> buildValueSetter() {
                return new TypedValueSetter<Integer>(Integer.class) {
                    @Override
                    public void setValue(View view, Integer value) {
                        view.getLayoutParams().height = value;
                        view.requestLayout();
                    }
                };
            }
        };
    }


    public static TypeBuilder<Integer> widthSlide(final int showValue, final int hideValue){
        return new TypeBuilder<Integer>() {
            @Override
            public DefaultAppearanceController.ValueGetter<Integer> buildValueGetter() {
                return new DefaultAppearanceController.ValueGetter<Integer>() {
                    @Override
                    public Integer getShowValue() {
                        return showValue;
                    }

                    @Override
                    public Integer getHideValue() {
                        return hideValue;
                    }

                    @Override
                    public Integer getCurrentValue(View view) {
                        return view.getLayoutParams().width;
                    }
                };
            }

            @Override
            public TypedValueSetter<Integer> buildValueSetter() {
                return new TypedValueSetter<Integer>(Integer.class) {
                    @Override
                    public void setValue(View view, Integer value) {
                        view.getLayoutParams().width = value;
                        view.requestLayout();
                    }
                };
            }
        };
    }
    public static TypeBuilder<Float> ySlide(final float showValue, final float hideValue){
        return new TypeBuilder<Float>() {
            @Override
            public DefaultAppearanceController.ValueGetter<Float> buildValueGetter() {
                return new DefaultAppearanceController.ValueGetter<Float>() {
                    @Override
                    public Float getShowValue() {
                        return showValue;
                    }

                    @Override
                    public Float getHideValue() {
                        return hideValue;
                    }

                    @Override
                    public Float getCurrentValue(View view) {
                        return view.getTranslationY();
                    }
                };
            }

            @Override
            public TypedValueSetter<Float> buildValueSetter() {
                return new TypedValueSetter<Float>(Float.class) {
                    @Override
                    public void setValue(View view, Float value) {
                        view.setTranslationY(value);
                    }
                };
            }
        };
    }

    public static TypeBuilder<Float> alpha(final float showValue, final float hideValue){
        return new TypeBuilder<Float>() {
            @Override
            public DefaultAppearanceController.ValueGetter<Float> buildValueGetter() {
                return new DefaultAppearanceController.ValueGetter<Float>() {
                    @Override
                    public Float getShowValue() {
                        return showValue;
                    }

                    @Override
                    public Float getHideValue() {
                        return hideValue;
                    }

                    @Override
                    public Float getCurrentValue(View view) {
                        return view.getAlpha();
                    }
                };
            }

            @Override
            public TypedValueSetter<Float> buildValueSetter() {
                return new TypedValueSetter<Float>(Float.class) {
                    @Override
                    public void setValue(View view, Float value) {
                        view.setAlpha(value);
                    }
                };
            }
        };
    }

    public static TimeInterpreterBuilder interpreter_overshot(){
        return new TimeInterpreterBuilder() {
            @Override
            public TimeInterpolator build() {
                return new OvershootInterpolator();
            }
        };
    }

    public static TimeInterpreterBuilder interpreter_accelerate_decelerate(){
        return new TimeInterpreterBuilder() {
            @Override
            public TimeInterpolator build() {
                return new AccelerateDecelerateInterpolator();
            }
        };
    }


    public static TimeInterpreterBuilder interpreter_decelerate(final Float factor){
        return new TimeInterpreterBuilder() {
            @Override
            public TimeInterpolator build() {
                if (factor == null) return new DecelerateInterpolator();
                return new DecelerateInterpolator(factor);
            }
        };
    }

    public static TimeInterpreterBuilder interpreter_accelerate(final Float factor){
        return new TimeInterpreterBuilder() {
            @Override
            public TimeInterpolator build() {
                if (factor == null) return new AccelerateInterpolator();
                return new AccelerateInterpolator(factor);
            }
        };
    }

    private ViewAnimatorFactory<TypeValue> createAnimator(ViewAnimatorFactorySupport.DurationProvider<? super TypeValue> duration, TimeInterpreterBuilder builder) {
        ViewAnimatorFactory<TypeValue> animatorFactory;
        if (typeBuilder.buildValueSetter().typeClass == Float.class){
            animatorFactory =
                    (ViewAnimatorFactory<TypeValue>) new ViewObjectAnimatorFactories.FloatObjectViewAnimator(
                            (ViewAnimatorFactorySupport.DurationProvider<Float>) duration, builder.build());
        }else  if (typeBuilder.buildValueSetter().typeClass == Integer.class){
            animatorFactory =
                    (ViewAnimatorFactory<TypeValue>) new ViewObjectAnimatorFactories.IntObjectViewAnimator(
                            (ViewAnimatorFactorySupport.DurationProvider<Integer>) duration, builder.build());
        }else {
            throw new IllegalStateException("Unsupported yet");
        }
        return animatorFactory;
    }

    private static long msLimitsCheck(long ms) {
        if (ms > 500){
            ms = 500;
        } else if(ms < 200){
            ms = 200;
        }
        return ms;
    }


    public AppearanceController build(){

        if (hideAnimationFactory == null){
            hideAnimation(duration_constant(400));
        }

        if (showAnimationFactory == null){
            showAnimation(duration_constant(400));
        }

        return new DefaultAppearanceController(
                animationView,
                typeBuilder.buildValueGetter(),
                typeBuilder.buildValueSetter(),
                showAnimationFactory,
                hideAnimationFactory,
                visibilityOnHide);
    }


    public static interface TimeInterpreterBuilder{
        final static TimeInterpreterBuilder NO_OP = new TimeInterpreterBuilder() {
            @Override
            public TimeInterpolator build() {
                return null;
            }
        };
        TimeInterpolator build();
    }

    private static interface TypeBuilder<ValueType>{
        public DefaultAppearanceController.ValueGetter<ValueType> buildValueGetter();
        public TypedValueSetter<ValueType> buildValueSetter();
    }

    public static abstract class TypedValueSetter<Type> implements ViewAnimatorFactory.ValueSetter<Type>{
        private final Class<Type> typeClass;
        protected TypedValueSetter(Class<Type> typeClass) {
            this.typeClass = typeClass;
        }
    }
}
