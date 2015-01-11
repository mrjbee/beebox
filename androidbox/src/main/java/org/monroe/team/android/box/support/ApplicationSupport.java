package org.monroe.team.android.box.support;

import android.app.Application;

public abstract class ApplicationSupport <Model> extends Application{

    private Model model;

    @Override
    public void onCreate() {
        super.onCreate();
        model();
    }

    final public Model model() {
        if (model == null){
            model = createModel();
        }
        return model;
    }

    abstract protected Model createModel();
}
