package org.monroe.team.android.box.ui.animation.apperrance;

final public class CombinedAppearanceController implements AppearanceController{

    private AppearanceController[] controllers;

    private CombinedAppearanceController(AppearanceController[] controllers) {
        this.controllers = controllers;
    }

    public static AppearanceController combine(AppearanceController... controllers){
        return new CombinedAppearanceController(controllers);
    }

    @Override
    public void show() {
        for (AppearanceController controller : controllers) {
            controller.show();
        }
    }

    @Override
    public void hide() {
        for (AppearanceController controller : controllers) {
            controller.hide();
        }
    }

    @Override
    public void showAndCustomize(AnimatorCustomization customization) {
        for (AppearanceController controller : controllers) {
            controller.showAndCustomize(customization);
        }
    }

    @Override
    public void hideAndCustomize(AnimatorCustomization customization) {
        for (AppearanceController controller : controllers) {
            controller.hideAndCustomize(customization);
        }
    }

    @Override
    public void showWithoutAnimation() {
        for (AppearanceController controller : controllers) {
            controller.showWithoutAnimation();
        }
    }

    @Override
    public void hideWithoutAnimation() {
        for (AppearanceController controller : controllers) {
            controller.hideWithoutAnimation();
        }
    }

    @Override
    public void cancel() {
        for (AppearanceController controller : controllers) {
            controller.cancel();
        }
    }
}
