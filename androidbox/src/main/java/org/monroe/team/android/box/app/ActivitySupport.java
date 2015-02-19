package org.monroe.team.android.box.app;

import android.app.Application;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.monroe.team.android.box.utils.DisplayUtils;

public abstract class ActivitySupport <AppType extends Application> extends android.app.Activity{

    private boolean noAnimation = false;



    final public void crunch_requestNoAnimation(){noAnimation = true;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (noAnimation) overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        if (noAnimation) overridePendingTransition(0, 0);
        super.onPause();
    }

    public <ViewType extends View> ViewType view(int resourceId, Class<ViewType> viewType){
        return (ViewType) findViewById(resourceId);
    }

    public View view(int resourceId){
        return findViewById(resourceId);
    }

    public TextView view_text(int resourceId){
        return view(resourceId,TextView.class);
    }

    public CheckBox view_check(int resourceId){
        return view(resourceId,CheckBox.class);
    }
    public Switch view_switch(int resourceId){
        return view(resourceId,Switch.class);
    }
    public ListView view_list(int resourceId){
        return view(resourceId,ListView.class);
    }
    public Button view_button(int resourceId){
        return view(resourceId, Button.class);
    }


    public AppType application(){
        return (AppType) getApplication();
    }

    public void runLastOnUiThread(final Runnable runnable, final long... waitBeforeRun){
        new Thread(){
            @Override
            public void run() {
                if (waitBeforeRun.length > 0) {
                    try {
                        Thread.sleep(waitBeforeRun[0]);
                    } catch (InterruptedException e) {}
                }
                runOnUiThread(runnable);
            }
        }.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("not_first_run", true);
    }

    final public boolean isFirstRun(Bundle state){
        if (state == null) return true;
        return !state.getBoolean("not_first_run", false);
    }

    final public boolean isLandscape(Class rBool){
       return DisplayUtils.isLandscape(getResources(), rBool);
    }


    public <ResultType> ResultType getFromIntent(String key, ResultType defValue) {
        if (getIntent() == null || getIntent().getExtras() == null) return defValue;
        Object obj = getIntent().getExtras().get(key);
        return (obj == null) ? defValue: (ResultType) obj;
    }

}
