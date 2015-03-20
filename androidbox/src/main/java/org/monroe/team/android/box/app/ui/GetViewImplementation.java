package org.monroe.team.android.box.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class GetViewImplementation<DataType, ViewHolderType extends GetViewImplementation.ViewHolder<DataType>> {

    private final Context context;
    private final ListAdapter owner;
    private final ViewHolderFactory<ViewHolderType> holderFactory;
    private final int layout;

    public GetViewImplementation(Context context, ListAdapter owner, ViewHolderFactory<ViewHolderType> holderFactory, int layout) {
        this.context = context;
        this.owner = owner;
        this.holderFactory = holderFactory;
        this.layout = layout;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderType viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder = holderFactory.create(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderType) convertView.getTag();
            viewHolder.cleanup();
        }
        viewHolder.update((DataType) owner.getItem(position),position);
        return convertView;
    }

    public static interface ViewHolder<TData>{
        public void update(TData data, int position);
        public void cleanup();
    }

    public static interface ViewHolderFactory<TViewHolder extends ViewHolder<?>>{
        TViewHolder create(View convertView);
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
