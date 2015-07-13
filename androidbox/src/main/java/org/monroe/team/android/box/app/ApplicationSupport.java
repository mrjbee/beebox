package org.monroe.team.android.box.app;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.BackgroundTaskManager;
import org.monroe.team.corebox.uc.UserCase;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ApplicationSupport <ModelType extends Model> extends Application{

    private ModelType model;

    @Override
    public void onCreate() {
        super.onCreate();
        model();
        onPostCreate();
    }

    protected void onPostCreate() {}

    final public ModelType model() {
        if (model == null){
            model = createModel();
        }
        return model;
    }

    abstract protected ModelType createModel();

    public void processException(Throwable e) {
        String msg = e.getClass().getSimpleName()+":"+e.getMessage();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        Log.e("ANDROID_BOX","",e);
    }


    public String getSettingAsString(SettingManager.SettingItem<?> item) {
        Object itemValue = model().usingService(SettingManager.class).get(item);
        if (itemValue == null){
            return "";
        }else {
            return itemValue.toString();
        }
    }


    public <SettingType> SettingType getSetting(SettingManager.SettingItem<SettingType> setting){
        return model().usingService(SettingManager.class).get(setting);
    }

    public <SettingType> void setSetting(SettingManager.SettingItem<SettingType> settingItem, SettingType settingValue) {
        model().usingService(SettingManager.class).set(settingItem, settingValue);
    }

    public PeriodicalAction preparePeriodicalAction(Runnable runnable){
        return new PeriodicalAction(this, runnable) {
            @Override
            protected void error(Exception e) {
                processException(e);
            }
        };
    }

    public <RequestType,ResponseType, ValueType> BackgroundTaskManager.BackgroundTask<ResponseType> fetchValue(
            Class<? extends UserCase<RequestType,ResponseType>> ucId,
            final RequestType request, final ValueAdapter<ResponseType, ValueType> adapter, final ValueObserver<ValueType> observer) {
        return model().execute(ucId,request,new Model.BackgroundResultCallback<ResponseType>() {
            @Override
            public void onResult(ResponseType response) {
                observer.onSuccess(adapter.adapt(response));
            }

            @Override
            public void onFails(Throwable e) {
               observer.onFail(e);
            }
        });
    }

    public void processException(Activity activity, Throwable exception) {
        processException(exception);
    }

    public static interface ValueAdapter<ValueType1,ValueType2>{
        public ValueType2 adapt(ValueType1 value);
    }

    public static class NoOpValueAdapter<ValueType> implements ValueAdapter<ValueType,ValueType> {

        @Override
        public ValueType adapt(ValueType value) {
            return value;
        }
    }

    public static interface ValueObserver<ValueType>{
        public void onSuccess(ValueType value);
        public void onFail(Throwable exception);
    }

    public static abstract class PeriodicalAction {

        private final ApplicationSupport mApp;
        private final Runnable mAction;
        private Timer mTimer = null;

        public PeriodicalAction(ApplicationSupport app, Runnable action) {
            this.mApp = app;
            this.mAction = action;
        }

        public synchronized void start(long startupDelay, long period){
            if (mTimer != null) throw new IllegalStateException("Timer already running");
            stop();
            mTimer = new Timer(true);
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    time();
                }
            }, startupDelay, period);
        }

        private synchronized void time() {
            if (mTimer == null) return;
            mApp.doOnResponseThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mAction.run();
                    } catch (Exception e) {
                        error(e);
                    }
                }
            });

        }

        protected abstract void error(Exception e);

        public synchronized void stop(){
            if (mTimer == null) return;
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    private void doOnResponseThread(Runnable runnable) {
        model().getResponseHandler().post(runnable);
    }


}
