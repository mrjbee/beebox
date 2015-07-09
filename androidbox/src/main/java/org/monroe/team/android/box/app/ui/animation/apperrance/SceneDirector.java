package org.monroe.team.android.box.app.ui.animation.apperrance;

import android.animation.Animator;

import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;

import java.util.ArrayList;
import java.util.List;

public final class SceneDirector {

    private ActionScene headScene;
    private ActionScene tailScene;

    public SceneDirector() {}

    public static SceneDirector scenario(){
        return new SceneDirector();
    }

    public SceneDirector then(){
        installNewScene();
        return this;
    }

    public SceneDirector show(AppearanceController... ac) {
        return show(0, ac);
    }

    public SceneDirector show(long delay, AppearanceController... ac){
        for (AppearanceController controller : ac) {
            show(controller, delay);
        }
        return this;
    }

    public SceneDirector hide(AppearanceController... ac) {
        return hide(0, ac);
    }

    public SceneDirector hide(long delay, AppearanceController... ac){
        for (AppearanceController controller : ac) {
            hide(controller, delay);
        }
        return this;
    }

    public SceneDirector hide(AppearanceController ac, long delay){
        HideAction hideAction = new HideAction(ac, delay);
        installAction(hideAction);
        return this;
    }

    public SceneDirector show(AppearanceController ac, long delay){
        ShowAction showAction = new ShowAction(ac, delay);
        installAction(showAction);
        return this;
    }

    public SceneDirector action_wait(final long ms){
        installAction(new Action() {
            @Override
            public void executeAnd(Runnable postAction) {

            }

            @Override
            public long duration() {
                return ms;
            }
        });
        return this;
    }


    public SceneDirector action_hide_without_animation(final AppearanceController... ac){
        return action(new Runnable() {
            @Override
            public void run() {
                for (AppearanceController controller : ac) {
                    controller.hideWithoutAnimation();
                }
            }
        });
    }

    public SceneDirector action_show_without_animation(final AppearanceController... ac){
        return action(new Runnable() {
            @Override
            public void run() {
                for (AppearanceController controller : ac) {
                    controller.showWithoutAnimation();
                }
            }
        });
    }

    public SceneDirector action(final Runnable action) {
        installAction(new Action() {
            @Override
            public void executeAnd(Runnable postAction) {
                action.run();
                if (postAction != null) {
                    postAction.run();
                }
            }

            @Override
            public long duration() {
                return 0;
            }
        });
        return this;
    }


    private void installNewScene() {
        if (!initiateHeadAndTailIfNeeded()){
            if (tailScene.isEmpty()){
                throw new IllegalStateException("Empty scene");
            }
            ActionScene scene = new ActionScene();
            tailScene.nextScene = scene;
            tailScene = scene;
        }
    }

    private void installAction(Action action) {
        initiateHeadAndTailIfNeeded();
        tailScene.actions.add(action);
    }

    private boolean initiateHeadAndTailIfNeeded() {
        if (tailScene == null){
            tailScene = new ActionScene();
            if (headScene == null){
                headScene = tailScene;
            }
            return true;
        }
        else return false;
    }

    public void play() {
        headScene.act();
    }

    private static final class ActionScene {

        private final List<Action> actions = new ArrayList<>();
        protected ActionScene nextScene;

        private void act(){
            if (nextScene == null){
                for (Action action : actions) {
                    action.executeAnd(null);
                }
            } else {
                long maxDuration = -1;
                Action maxDurationAction = null;
                for (Action action : actions) {
                    long duration = action.duration();
                    if (maxDuration < duration){
                        maxDuration = duration;
                        maxDurationAction = action;
                    }
                }

                for (Action action : actions) {
                    if (action != maxDurationAction){
                        action.executeAnd(null);
                    }else{
                        action.executeAnd(new Runnable() {
                            @Override
                            public void run() {
                                nextScene.act();
                            }
                        });
                    }
                }

            }
        }

        public boolean isEmpty() {
            return actions.isEmpty();
        }
    }

    private static final class ShowAction extends AbstractAction{

        private ShowAction(AppearanceController appearanceController, long startDelay) {
            super(appearanceController, startDelay);
        }

        private ShowAction(AppearanceController appearanceController) {
            super(appearanceController);
        }

        @Override
        public void executeAnd(Runnable runnable) {
            if (duration() == 0){
                appearanceController.show();
                if (runnable != null) {
                    runnable.run();
                }
            }else {
                appearanceController.showAndCustomize(animatorCustomization(runnable));
            }
        }

        @Override
        public long duration() {
            long animationDuration = appearanceController.durationShow();
            if (animationDuration == 0) return 0;
            return startDelay + animationDuration;
        }
    }

    private static final class HideAction extends AbstractAction{

        private HideAction(AppearanceController appearanceController, long startDelay) {
            super(appearanceController, startDelay);
        }

        private HideAction(AppearanceController appearanceController) {
            super(appearanceController);
        }

        @Override
        public void executeAnd(Runnable runnable) {
            if (duration() == 0){
                appearanceController.hide();
                if (runnable != null) {
                    runnable.run();
                }
            }else {
                appearanceController.hideAndCustomize(animatorCustomization(runnable));
            }
        }

        @Override
        public long duration() {
            long animationDuration = appearanceController.durationHide();
            if (animationDuration == 0) return 0;
            return startDelay + animationDuration;
        }
    }

    private static abstract class AbstractAction implements Action{

        protected final AppearanceController appearanceController;
        protected final long startDelay;

        public AbstractAction(AppearanceController appearanceController, long startDelay) {
            this.appearanceController = appearanceController;
            this.startDelay = Math.max(0,startDelay);
        }

        public AbstractAction(AppearanceController appearanceController) {
            this(appearanceController, 0);
        }

        final protected AppearanceController.AnimatorCustomization animatorCustomization(final Runnable runnable){
            return new AppearanceController.AnimatorCustomization() {
                @Override
                public void customize(Animator animator) {
                    if (startDelay > 0){
                        animator.setStartDelay(startDelay);
                    }
                    if (runnable != null){
                        animator.addListener(new AnimatorListenerSupport(){
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                runnable.run();
                            }
                        });
                    }
                }
            };
        }
    }

    private interface Action{
        public void executeAnd(Runnable runnable);
        public long duration();
    }

}
