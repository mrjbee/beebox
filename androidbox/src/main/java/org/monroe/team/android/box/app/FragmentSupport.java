package org.monroe.team.android.box.app;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;


public abstract class FragmentSupport <AppType extends ApplicationSupport> extends Fragment {

    private View fragment_panel;

    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment_panel = inflater.inflate(getLayoutId(), container, false);
        return fragment_panel;
    }

    final public ActivitySupport<AppType> activity(){
        return (ActivitySupport<AppType>) getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public View view(int resourceId) {
        return fragment_panel.findViewById(resourceId);
    }

    public <ViewType extends View> ViewType view(int resourceId, Class<ViewType> viewType) {
        return (ViewType) view(resourceId);
    }

    public TextView view_text(int resourceId) {
        return view(resourceId, TextView.class);
    }

    public CheckBox view_check(int resourceId) {
        return view(resourceId, CheckBox.class);
    }

    public Switch view_switch(int resourceId) {
        return view(resourceId, Switch.class);
    }

    public ListView view_list(int resourceId) {
        return view(resourceId, ListView.class);
    }

    public Button view_button(int resourceId) {
        return view(resourceId, Button.class);
    }


    public AppType application() {
        return (AppType) getActivity().getApplication();
    }

    public void runLastOnUiThread(final Runnable runnable, final long... waitBeforeRun) {
        new Thread() {
            @Override
            public void run() {
                if (waitBeforeRun.length > 0) {
                    try {
                        Thread.sleep(waitBeforeRun[0]);
                    } catch (InterruptedException e) {
                    }
                }
                getActivity().runOnUiThread(runnable);
            }
        }.start();
    }

    public <ResultType> ResultType getFromIntent(String key, ResultType defValue) {
        if (getActivity().getIntent() == null || getActivity().getIntent().getExtras() == null)
            return defValue;
        Object obj = getActivity().getIntent().getExtras().get(key);
        return (obj == null) ? defValue : (ResultType) obj;
    }

    protected abstract int getLayoutId();

    public View getFragmentView() {
        return fragment_panel;
    }
}
