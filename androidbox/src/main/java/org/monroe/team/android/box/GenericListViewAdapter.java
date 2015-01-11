package org.monroe.team.android.box;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class GenericListViewAdapter<TData, TViewHolder extends GenericListViewAdapter.ViewHolder<TData>> extends ArrayAdapter<TData> {

    private int layoutId;
    private final ViewHolderFactory<TViewHolder> holderFactory;

    public GenericListViewAdapter(Context context, int layoutId, ViewHolderFactory<TViewHolder> holderFactory) {
        super(context, layoutId);
        this.layoutId = layoutId;
        this.holderFactory = holderFactory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, null);
            viewHolder = holderFactory.create(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TViewHolder) convertView.getTag();
            viewHolder.cleanup();
        }
        viewHolder.update(getItem(position),position);
        return convertView;
    }


    public static interface ViewHolderFactory<TViewHolder extends ViewHolder<?>>{
        TViewHolder create(View convertView);
    }

    public static interface ViewHolder<TData>{
        public void update(TData data, int position);
        public void cleanup();
    }

    public static abstract class GenericViewHolderFactory<TData> implements ViewHolderFactory<GenericViewHolder<TData>>{
        @Override
        final public GenericViewHolder<TData> create(View convertView) {
            GenericViewHolder<TData> answer = construct();
            answer.initialize(convertView);
            return answer;
        }

        public abstract GenericViewHolder<TData> construct();

    }



    public static abstract class GenericViewHolder<TData> implements ViewHolder<TData> {

        protected View parentView;

        private void initialize(View view){
            parentView = view;
            discoverUI();
        }


        final public <TView> TView _view(int id, Class<TView> viewType){
            return (TView) parentView.findViewById(id);
        }

        public abstract void discoverUI();

        @Override
        public abstract void update(TData data, int position);

        @Override
        public void cleanup() {}
    }

}
