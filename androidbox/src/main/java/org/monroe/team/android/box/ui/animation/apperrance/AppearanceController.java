package org.monroe.team.android.box.ui.animation.apperrance;

import android.animation.Animator;

public interface AppearanceController {

    public void show();
    public void hide();
    public void showAndCustomize(AnimatorCustomization customization);
    public void hideAndCustomize(AnimatorCustomization customization);

    public void showWithoutAnimation();
    public void hideWithoutAnimation();
    public void cancel();


    public static interface AnimatorCustomization {
        public void customize(Animator animator);
    }

}
