package org.monroe.team.android.box.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.monroe.team.android.box.uc.UserCase;
import org.monroe.team.android.box.uc.UserCaseSupport;
import org.monroe.team.corebox.CoreModel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public abstract class Model implements CoreModel{

    private final ServiceRegistry serviceRegistry;
    private BackgroundTaskManager backgroundTaskManager = new BackgroundTaskManager();
    public final Context context;
    private final Handler uiHandler = new android.os.Handler(Looper.getMainLooper());

    public Model(String appName, Context context) {
        this.context = context;
        serviceRegistry = new ServiceRegistry(context);
        serviceRegistry.registrate(BackgroundTaskManager.class,backgroundTaskManager);
        serviceRegistry.registrate(Context.class,context);
        serviceRegistry.registrate(Model.class, this);
        SharedPreferences sharedPreferences = getPreferencesForSettings(appName, context);
        SettingManager settingManager = new SettingManager(sharedPreferences);
        EventMessenger messenger = new EventMessenger(context);
        serviceRegistry.registrate(SettingManager.class, settingManager);
        serviceRegistry.registrate(EventMessenger.class, messenger);
        constructor(appName, context, serviceRegistry);
    }

    protected SharedPreferences getPreferencesForSettings(String appName, Context context) {
        return context.getSharedPreferences(appName+"_Preferences", Context.MODE_PRIVATE);
    }

    protected void constructor(String appName, Context context, ServiceRegistry serviceRegistry){}

    public <RequestType,ResponseType> BackgroundTaskManager.BackgroundTask<ResponseType> execute(
            Class<? extends UserCase<RequestType,ResponseType>> ucId,
            final RequestType request, final BackgroundResultCallback<ResponseType> callback){

        final UserCase<RequestType,ResponseType> uc = getUserCase(ucId);

        return backgroundTaskManager.execute(new Callable<ResponseType>() {
            @Override
            public ResponseType call() throws Exception {
                return uc.execute(request);
            }
        }, new BackgroundTaskManager.TaskCompletionNotificationObserver<ResponseType>() {
            @Override
            public void onSuccess(final ResponseType o) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(o);
                        callback.onDone();
                    }
                });
            }

            @Override
            public void onFails(final Exception e) {
                if (e instanceof ExecutionException){
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFails(e.getCause());
                            callback.onDone();
                        }
                    });
                } else {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCancel();
                            callback.onDone();
                        }
                    });
                }
            }
        });
    }

    public <RequestType,ResponseType> ResponseType execute(
            Class<? extends UserCase<RequestType,ResponseType>> ucId,
            final RequestType request){
        final UserCase<RequestType,ResponseType> uc = getUserCase(ucId);
        return uc.execute(request);
    }

    private <RequestType,ResponseType> UserCase<RequestType, ResponseType> getUserCase(Class<? extends UserCase<RequestType, ResponseType>> ucId) {
        if (!serviceRegistry.contains(ucId)){
            UserCase<RequestType, ResponseType> ucInstance;
            try {
                if (UserCaseSupport.class.isAssignableFrom(ucId)) {
                    ucInstance = ucId.getConstructor(ServiceRegistry.class).newInstance(serviceRegistry);
                } else {
                    ucInstance = ucId.newInstance();
                }
                serviceRegistry.registrate((Class<UserCase<RequestType, ResponseType>>) ucId,ucInstance);
            } catch (Exception e) {
                throw new RuntimeException("Error during creating uc = "+ucId, e);
            }
        }
        return serviceRegistry.get(ucId);
    }

    public <Type> Type usingService(Class<Type> serviceClass) {
        return serviceRegistry.get(serviceClass);
    }

    public static abstract class BackgroundResultCallback<ResponseType> {
        abstract public void onResult(ResponseType response);
        public void onCancel(){}
        public void onFails(Throwable e){}
        public void onDone(){}
    }
}
