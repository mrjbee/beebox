package org.monroe.team.android.box.app;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.BackgroundTaskManager;
import org.monroe.team.corebox.uc.UserCase;
import org.monroe.team.corebox.uc.UserCaseSupport;

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

    final public void debug_exception(Throwable e) {
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
                if (e instanceof UserCaseSupport.FailExecutionException){
                    observer.onFail(((UserCaseSupport.FailExecutionException) e).errorCode);
                }else {
                    exceptionHandler(e);
                }
            }
        });
    }

    protected void exceptionHandler(Throwable e) {
        debug_exception(e);
    }

    public static interface ValueAdapter<ValueType1,ValueType2>{
        public ValueType2 adapt(ValueType1 value);
    }

    public static interface ValueObserver<ValueType>{
        public void onSuccess(ValueType value);
        public void onFail(int errorCode);
    }

}
