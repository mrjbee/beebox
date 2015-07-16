package org.monroe.team.android.box.app.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.monroe.team.corebox.utils.Closure;

import java.util.ArrayList;

public class GenericListViewAdapter<TData, TViewHolder extends GetViewImplementation.ViewHolder<TData>> extends ArrayAdapter<TData> {

    private int layoutId;
    private final GetViewImplementation<TData,TViewHolder> getView;
    private boolean mNotifyOnChangeClone = true;

    public GenericListViewAdapter(Context context, GetViewImplementation.ViewHolderFactory<TViewHolder> factory, int layoutId) {
        super(context, layoutId);
        this.layoutId = layoutId;
        this.getView = new GetViewImplementation<>(context, this, factory, layoutId);
    }

    public GenericListViewAdapter(Context context, int layoutId, GetViewImplementation<TData, TViewHolder> getView) {
        super(context, layoutId);
        this.layoutId = layoutId;
        this.getView = getView;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView.getView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position,convertView,parent);
    }

    public boolean isNotifyOnChange() {
        return mNotifyOnChangeClone;
    }

    @Override
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChangeClone = notifyOnChange;
        super.setNotifyOnChange(notifyOnChange);
    }

    public void  updateWithoutNotification(Closure<GenericListViewAdapter<TData, TViewHolder>, Void> update){
        boolean wasNotification = isNotifyOnChange();
        setNotifyOnChange(false);
        update.execute(this);
        setNotifyOnChange(wasNotification);
    }

}
