package org.monroe.team.android.box.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.monroe.team.android.box.event.Event;
import org.monroe.team.android.box.services.AndroidServiceRegistry;
import org.monroe.team.android.box.services.EventMessenger;
import org.monroe.team.android.box.services.HttpManager;
import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.services.BackgroundTaskManager;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class AndroidModel extends Model {

    private BackgroundTaskManager backgroundTaskManager = new BackgroundTaskManager();
    private final android.os.Handler uiHandler = new android.os.Handler(Looper.getMainLooper());
    private final UCResponseHandler uiThreadResponseHandler = new UCResponseHandler() {
        @Override
        public void post(Runnable r) {
            uiHandler.post(r);
        }
    };

    public AndroidModel(String appName, Context context) {
        super(appName, new AndroidServiceRegistry(context));
        serviceRegistry.registrate(Context.class, context);
        serviceRegistry.registrate(BackgroundTaskManager.class,backgroundTaskManager);

        SharedPreferences sharedPreferences = getPreferencesForSettings(appName, context);
        SettingManager settingManager = new SettingManager(sharedPreferences);
        EventMessenger messenger = new EventMessenger(context);
        serviceRegistry.registrate(SettingManager.class, settingManager);
        serviceRegistry.registrate(EventMessenger.class, messenger);
        serviceRegistry.registrate(HttpManager.class, new HttpManager());
        constructor(appName, context, serviceRegistry);
    }

    protected SharedPreferences getPreferencesForSettings(String appName, Context context) {
        return context.getSharedPreferences(appName+"_Preferences", Context.MODE_PRIVATE);
    }

    protected void constructor(String appName, Context context, ServiceRegistry serviceRegistry){}


    @Override
    final public UCResponseHandler getResponseHandler() {
        return uiThreadResponseHandler;
    }

    final public void ui(Runnable runnable){
       uiThreadResponseHandler.post(runnable);
    }
}
