package org.monroe.team.android.box.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;

public abstract class ActivitySupport <AppType extends ApplicationSupport> extends android.app.Activity{

    private boolean noAnimation = false;
    private Lifecycle lifecycleState = Lifecycle.Created;
    private boolean firstRun;

    final public void crunch_requestNoAnimation(){noAnimation = true;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firstRun = isFirstRun(savedInstanceState);
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

    final public boolean isFirstRun() {
        return firstRun;
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

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        final View view = getWindow().getDecorView().findViewById(android.R.id.content);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                onLayout(view);
                if (Build.VERSION_CODES.JELLY_BEAN > Build.VERSION.SDK_INT){
                    removeListenerOld(view,this);
                }else {
                    removeListener(view, this);
                }
            }
        });
    }

    private void removeListenerOld(View view, ViewTreeObserver.OnGlobalLayoutListener toRemove) {
        view.getViewTreeObserver().removeGlobalOnLayoutListener(toRemove);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void removeListener(View view, ViewTreeObserver.OnGlobalLayoutListener toRemove) {
        view.getViewTreeObserver().removeOnGlobalLayoutListener(toRemove);
    }

    private void onLayout(View view) {
        onActivitySize(view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    protected void onActivitySize(int width, int height) {
    }

    public void forceCloseWithErrorCode(Throwable exception) {
        application().processException(this, exception);
    }

    public enum Lifecycle {
        Created, Started, Resumed, Paused, Stopped, Destroyed
    }


    public <ValueType> ApplicationSupport.ValueObserver<ValueType> observe(final OnValue<ValueType> onValue){
      return new ApplicationSupport.ValueObserver<ValueType>() {
          @Override
          public void onSuccess(ValueType value) {
                onValue.action(value);
          }

          @Override
          public void onFail(Throwable exception) {
            forceCloseWithErrorCode(exception);
          }
      };
    }

    public <ValueType> Data.FetchObserver<ValueType> observe_data(final OnValue<ValueType> onValue){
        return new Data.FetchObserver<ValueType>() {
            @Override
            public void onFetch(ValueType valueType) {
                onValue.action(valueType);
            }

            @Override
            public void onError(Data.FetchError fetchError) {
                handle_fetchError(fetchError);
            }
        };
    }

    public void handle_fetchError(Data.FetchError fetchError) {
        if (fetchError instanceof Data.ExceptionFetchError){
           forceCloseWithErrorCode(((Data.ExceptionFetchError) fetchError).cause);
        }else{
            forceCloseWithErrorCode(new IllegalStateException(fetchError.message()));
        }
    }


    public static interface OnValue<ValueType>{
        public void action(ValueType valueType);
    }
}
