package org.monroe.team.android.box.app.ui.animation.apperrance;

import org.monroe.team.corebox.utils.Lists;

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
        for (int i=0; i<controllers.length-1;i++){
            controllers[i].show();
        }
        Lists.getLast(controllers).showAndCustomize(customization);
    }

    @Override
    public void hideAndCustomize(AnimatorCustomization customization) {
        for (int i=0; i<controllers.length-1;i++){
            controllers[i].hide();
        }
        Lists.getLast(controllers).hideAndCustomize(customization);
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
