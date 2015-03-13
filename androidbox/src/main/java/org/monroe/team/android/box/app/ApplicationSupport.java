package org.monroe.team.android.box.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.monroe.team.android.box.event.Event;
import org.monroe.team.android.box.event.GenericEvent;
import org.monroe.team.corebox.services.BackgroundTaskManager;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class ApplicationSupport <Model> extends Application{

    private Model model;

    @Override
    public void onCreate() {
        super.onCreate();
        model();
        onPostCreate();
    }

    protected void onPostCreate() {}

    final public Model model() {
        if (model == null){
            model = createModel();
        }
        return model;
    }

    abstract protected Model createModel();

    final public void debug_exception(Throwable e) {
        String msg = e.getClass().getSimpleName()+":"+e.getMessage();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        Log.e("ANDROID_BOX","",e);
    }


}
