package org.monroe.team.android.box.app.ui.animation.apperrance;

import android.animation.Animator;
import android.view.View;

import org.monroe.team.android.box.app.ui.animation.ViewAnimatorFactory;
import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;

public class DefaultAppearanceController implements AppearanceController {

    protected final View animatedView;
    private final ValueGetter valueGetter;
    private final ViewAnimatorFactory.ValueSetter valueSetter;
    private final ViewAnimatorFactory showAnimatorFactory;
    private final ViewAnimatorFactory hideViewAnimatorFactory;
    private final int visibilityOnHide;

    private Animator currentAnimator;
    private Boolean stateShowing = null;

    public <AnimatedValueType> DefaultAppearanceController(View animatedView,
                                       ValueGetter<AnimatedValueType> valueGetter,
                                       ViewAnimatorFactory.ValueSetter<AnimatedValueType> valueSetter,
                                       ViewAnimatorFactory showViewAnimatorFactory,
                                       ViewAnimatorFactory hideViewAnimatorFactory,
                                       int visibilityOnHide) {
        this.animatedView = animatedView;

        this.valueGetter = valueGetter;
        this.valueSetter = valueSetter;
        this.showAnimatorFactory = showViewAnimatorFactory;
        this.hideViewAnimatorFactory = hideViewAnimatorFactory;
        this.visibilityOnHide = visibilityOnHide;
    }

    @Override
    public void show() {
        showAndCustomize(null);
    }

    @Override
    public void hide() {
        hideAndCustomize(null);
    }

    @Override
    public void showAndCustomize(AnimatorCustomization customization) {
        if (Boolean.TRUE.equals(stateShowing))return;
        cancelCurrentAnimator();
        stateShowing = Boolean.TRUE;
        if (isAlreadyShow()){
            showWithoutAnimation();
            return;
        }
        currentAnimator = showAnimatorFactory.create(
                animatedView,
                valueGetter.getCurrentValue(animatedView),
                valueGetter.getShowValue(),
                valueSetter);
        currentAnimator.addListener(new AnimatorListenerSupport(){
            @Override
            public void onAnimationStart(Animator animation) {
                animatedView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showWithoutAnimation();
            }
        });
        if (customization != null) {
            customization.customize(currentAnimator);
        }
        currentAnimator.start();
    }

    public boolean isAlreadyShow() {
        return valueGetter.getCurrentValue(animatedView) == valueGetter.getShowValue();
    }

    @Override
    public void hideAndCustomize(AnimatorCustomization customization) {
        if (Boolean.FALSE.equals(stateShowing))return;
        cancelCurrentAnimator();
        stateShowing = Boolean.FALSE;

        cancelCurrentAnimator();
        if (isAlreadyHide()){
            hideWithoutAnimation();
            return;
        }
        currentAnimator = hideViewAnimatorFactory.create(
                animatedView,
                valueGetter.getCurrentValue(animatedView),
                valueGetter.getHideValue(),
                valueSetter);
        currentAnimator.addListener(new AnimatorListenerSupport(){
            @Override
            public void onAnimationEnd(Animator animation) {
                hideWithoutAnimation();
            }
        });
        if (customization != null) {
            customization.customize(currentAnimator);
        }
        currentAnimator.start();
    }

    public boolean isAlreadyHide() {
        return valueGetter.getCurrentValue(animatedView) == valueGetter.getHideValue();
    }

    @Override
    public void showWithoutAnimation() {
        cancelCurrentAnimator();
        valueSetter.setValue(animatedView, valueGetter.getShowValue());
        animatedView.setVisibility(View.VISIBLE);
        stateShowing = null;
    }

    @Override
    public void hideWithoutAnimation() {
        cancelCurrentAnimator();
        valueSetter.setValue(animatedView, valueGetter.getHideValue());
        animatedView.setVisibility(visibilityOnHide);
        stateShowing = null;
    }

    @Override
    public void cancel() {
        cancelCurrentAnimator();
    }

    @Override
    public long durationShow() {
        if (isAlreadyShow()) return 0;
        return showAnimatorFactory.duration(valueGetter.getCurrentValue(animatedView), valueGetter.getShowValue());
    }

    @Override
    public long durationHide() {
        if (isAlreadyHide()) return 0;
        return hideViewAnimatorFactory.duration(valueGetter.getCurrentValue(animatedView), valueGetter.getHideValue());
    }

    private void cancelCurrentAnimator() {
        if (currentAnimator != null){
            currentAnimator.cancel();
        }
    }

    public static interface ValueGetter<ValueType> {
        public ValueType getShowValue();
        public ValueType getHideValue();
        public ValueType getCurrentValue(View view);
    }
}
