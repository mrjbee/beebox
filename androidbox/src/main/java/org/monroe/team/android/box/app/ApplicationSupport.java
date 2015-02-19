package org.monroe.team.android.box.app;

import android.app.Application;

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
}
