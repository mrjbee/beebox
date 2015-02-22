package org.monroe.team.android.box.app;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.monroe.team.android.box.utils.DisplayUtils;

public abstract class ActivitySupport <AppType extends Application> extends android.app.Activity{

    private boolean noAnimation = false;
    private Lifecycle lifecycleState = Lifecycle.Created;

    final public void crunch_requestNoAnimation(){noAnimation = true;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (noAnimation) overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        updateState(Lifecycle.Created);
    }

    private void updateState(Lifecycle state) {
        lifecycleState = state;
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateState(Lifecycle.Started);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateState(Lifecycle.Resumed);
    }

    @Override
    protected void onPause() {
        if (noAnimation) overridePendingTransition(0, 0);
        super.onPause();
        updateState(Lifecycle.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateState(Lifecycle.Stopped);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateState(Lifecycle.Destroyed);
    }

    final public Lifecycle getLifecycleState() {
        return lifecycleState;
    }

    final public boolean before(Lifecycle state) {
        return  (lifecycleState.ordinal() < state.ordinal());
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

    public enum Lifecycle {
        Created, Started, Resumed, Paused, Stopped, Destroyed
    }
}
