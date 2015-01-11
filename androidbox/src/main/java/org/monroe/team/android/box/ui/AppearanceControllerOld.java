package org.monroe.team.android.box.ui;

import android.animation.Animator;

import org.monroe.team.android.box.Closure;

public interface AppearanceControllerOld {


    public void show(Closure<Animator,Void> customization);
    public void hide(Closure<Animator,Void> customization);
    public void showWithoutAnimation();
    public void hideWithoutAnimation();

    public static class AnimatorListenerAdapter implements Animator.AnimatorListener{

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
