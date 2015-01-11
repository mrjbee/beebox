package org.monroe.team.android.box.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import org.monroe.team.android.box.Closure;

final public class AppearanceControllers {


    public static class AlphaAppearanceController implements AppearanceControllerOld {

        private final View view;
        private final LimitProvider<Float> limits;

        private ObjectAnimator animator;

        public AlphaAppearanceController(View view, LimitProvider<Float> limits) {
            this.view = view;
            this.limits = limits;
        }

        @Override
        public void show(Closure<Animator,Void> customization) {
            cancelCurrentAnimator();

            if (getCurrentValue() == getShowValue()){
                showWithoutAnimation();
                return;
            }

            float currentValue = getCurrentValue();
            animator = ObjectAnimator.ofFloat(view,new Property<View, Float>(Float.class, "slide_from_bottom_show") {

                float value = 0;

                @Override
                public Float get(View object) {
                    return value;
                }

                @Override
                public void set(View object, Float value) {
                    this.value = value;
                    object.setAlpha(this.value);
                }
            }, currentValue, getShowValue());
            animator.setDuration(calculateDuration(currentValue,getShowValue()));
            animator.setInterpolator(new OvershootInterpolator());
            animator.addListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    showWithoutAnimation();
                }
            });
            if (customization != null) customization.execute(animator);
            animator.start();
        }

        @Override
        public void hide(Closure<Animator, Void> customization) {
            cancelCurrentAnimator();

            if (getCurrentValue() == getHideValue()){
                hideWithoutAnimation();
                return;
            }

            float currentValue = getCurrentValue();
            animator = ObjectAnimator.ofFloat(view,new Property<View, Float>(Float.class, "slide_from_bottom_hide") {

                float value = 0;

                @Override
                public Float get(View object) {
                    return value;
                }

                @Override
                public void set(View object, Float value) {
                    this.value = value;
                    object.setAlpha(this.value);
                }
            }, currentValue, getHideValue());
            animator.setDuration(calculateDuration(currentValue, getHideValue()));
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    hideWithoutAnimation();
                }
            });

            if (customization != null) customization.execute(animator);
            animator.start();
        }

        private long calculateDuration(float currentValue, float showValue) {
            long duration =  (long) (Math.abs(currentValue - showValue));
            if (duration > 700){
                return 700;
            }
            return duration;
        }


        @Override
        public void showWithoutAnimation() {
            cancelCurrentAnimator();
            view.setTranslationY(getShowValue());
        }

        private void cancelCurrentAnimator() {
            if (animator != null){
                animator.cancel();
            }
        }

        @Override
        public void hideWithoutAnimation() {
            cancelCurrentAnimator();
            view.setTranslationY(getHideValue());
        }


        private float getCurrentValue() {
            return view.getAlpha();
        }

        private float getShowValue() {
            return limits.getLimitOnShow();
        }

        private float getHideValue() { return limits.getLimitOnHide(); }
    }

    public static class SlideVerticallyAppearanceController implements AppearanceControllerOld {

        private final View view;
        private final LimitProvider<Float> limits;

        private ObjectAnimator animator;
        private final boolean usingAlpha;

        public SlideVerticallyAppearanceController(View view, LimitProvider<Float> limits) {
            this(view,false, limits);
        }

        public SlideVerticallyAppearanceController(View view, boolean useAlpha, LimitProvider<Float> limits) {
            this.view = view;
            this.limits = limits;
            usingAlpha = useAlpha;
        }

        @Override
        public void show(Closure<Animator,Void> customization) {
            cancelCurrentAnimator();

            if (getCurrentValue() == getShowValue()){
                showWithoutAnimation();
                return;
            }

            float currentValue = getCurrentValue();
            animator = ObjectAnimator.ofFloat(view,new Property<View, Float>(Float.class, "slide_from_bottom_show") {

                float translateY = 0;

                @Override
                public Float get(View object) {
                    return translateY;
                }

                @Override
                public void set(View object, Float value) {
                    translateY = value;
                    object.setTranslationY(translateY);
                }
            }, currentValue, getShowValue());
            animator.setDuration(calculateDuration(currentValue,getShowValue()));
            animator.setInterpolator(new OvershootInterpolator());
            animator.addListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                    if (usingAlpha){
                        view.setAlpha(0);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    showWithoutAnimation();
                }
            });
            if (usingAlpha) {
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view.setAlpha(animation.getAnimatedFraction());
                    }
                });
            }
            if (customization != null) customization.execute(animator);
            animator.start();
        }

        @Override
        public void hide(Closure<Animator, Void> customization) {
            cancelCurrentAnimator();

            if (getCurrentValue() == getHideValue()){
                hideWithoutAnimation();
                return;
            }

            float currentValue = getCurrentValue();
            animator = ObjectAnimator.ofFloat(view,new Property<View, Float>(Float.class, "slide_from_bottom_hide") {

                float translateY = 0;

                @Override
                public Float get(View object) {
                    return translateY;
                }

                @Override
                public void set(View object, Float value) {
                    translateY = value;
                    object.setTranslationY(translateY);
                }
            }, currentValue, getHideValue());
            animator.setDuration(calculateDuration(currentValue, getHideValue()));
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    hideWithoutAnimation();
                }
            });
            if (usingAlpha) {
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view.setAlpha(1 - animation.getAnimatedFraction());
                    }
                });
            }
            if (customization != null) customization.execute(animator);
            animator.start();
        }

        private long calculateDuration(float currentValue, float showValue) {
            long duration =  (long) (Math.abs(currentValue - showValue));
            if (duration > 700){
                return 700;
            }
            return duration;
        }


        @Override
        public void showWithoutAnimation() {
            cancelCurrentAnimator();
            view.setTranslationY(getShowValue());
            view.setVisibility(View.VISIBLE);
        }

        private void cancelCurrentAnimator() {
            if (animator != null){
                animator.cancel();
            }
        }

        @Override
        public void hideWithoutAnimation() {
            cancelCurrentAnimator();
            view.setTranslationY(getHideValue());
            view.setVisibility(View.GONE);
        }


        private float getCurrentValue() {
            return view.getTranslationY();
        }

        private float getShowValue() {
            return limits.getLimitOnShow();
        }

        private float getHideValue() { return limits.getLimitOnHide(); }
    }


    public static class SlideHorizontallyAppearanceController implements AppearanceControllerOld {

        private final View view;
        private final LimitProvider<Float> limits;

        private ObjectAnimator animator;
        private final boolean usingAlpha;

        public SlideHorizontallyAppearanceController(View view, LimitProvider<Float> limits) {
            this(view,false, limits);
        }

        public SlideHorizontallyAppearanceController(View view, boolean useAlpha, LimitProvider<Float> limits) {
            this.view = view;
            this.limits = limits;
            usingAlpha = useAlpha;
        }

        @Override
        public void show(Closure<Animator,Void> customization) {
            cancelCurrentAnimator();

            if (getCurrentValue() == getShowValue()){
                showWithoutAnimation();
                return;
            }

            float currentValue = getCurrentValue();
            animator = ObjectAnimator.ofFloat(view,new Property<View, Float>(Float.class, "slide_from_bottom_show") {

                float translateX = 0;

                @Override
                public Float get(View object) {
                    return translateX;
                }

                @Override
                public void set(View object, Float value) {
                    translateX = value;
                    object.setTranslationX(translateX);
                }
            }, currentValue, getShowValue());
            animator.setDuration(calculateDuration(currentValue,getShowValue()));
            animator.setInterpolator(new OvershootInterpolator());
            animator.addListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                    if (usingAlpha){
                        view.setAlpha(0);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    showWithoutAnimation();
                }
            });
            if (usingAlpha) {
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view.setAlpha(animation.getAnimatedFraction());
                    }
                });
            }
            if (customization != null) customization.execute(animator);
            animator.start();
        }

        @Override
        public void hide(Closure<Animator, Void> customization) {
            cancelCurrentAnimator();

            if (getCurrentValue() == getHideValue()){
                hideWithoutAnimation();
                return;
            }

            float currentValue = getCurrentValue();
            animator = ObjectAnimator.ofFloat(view,new Property<View, Float>(Float.class, "slide_from_bottom_hide") {

                float translateX = 0;

                @Override
                public Float get(View object) {
                    return translateX;
                }

                @Override
                public void set(View object, Float value) {
                    translateX = value;
                    object.setTranslationX(translateX);
                }
            }, currentValue, getHideValue());
            animator.setDuration(calculateDuration(currentValue, getHideValue()));
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    hideWithoutAnimation();
                }
            });
            if (usingAlpha) {
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view.setAlpha(1 - animation.getAnimatedFraction());
                    }
                });
            }
            if (customization != null) customization.execute(animator);
            animator.start();
        }

        private long calculateDuration(float currentValue, float showValue) {
            long duration =  (long) (Math.abs(currentValue - showValue));
            if (duration > 700){
                return 700;
            }
            return duration;
        }


        @Override
        public void showWithoutAnimation() {
            cancelCurrentAnimator();
            view.setTranslationX(getShowValue());
            view.setVisibility(View.VISIBLE);
        }

        private void cancelCurrentAnimator() {
            if (animator != null){
                animator.cancel();
            }
        }

        @Override
        public void hideWithoutAnimation() {
            cancelCurrentAnimator();
            view.setTranslationX(getHideValue());
            view.setVisibility(View.GONE);
        }


        private float getCurrentValue() {
            return view.getTranslationX();
        }

        private float getShowValue() {
            return limits.getLimitOnShow();
        }

        private float getHideValue() { return limits.getLimitOnHide(); }
    }

    public interface LimitProvider<LimitType> {
        public LimitType getLimitOnShow();
        public LimitType getLimitOnHide();
    }


}
