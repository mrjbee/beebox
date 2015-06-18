package org.monroe.team.android.box.data;

import android.content.Context;

import org.monroe.team.android.box.event.Event;
import org.monroe.team.android.box.event.GenericEvent;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.log.L;
import org.monroe.team.corebox.services.BackgroundTaskManager;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class DataProvider<DataType extends Serializable>  extends Data<DataType>{

    public static final GenericEvent<Class> INVALID_DATA = new GenericEvent<>("INVALID_DATA");
    protected final Context context;
    private final Event<DataType> dataChangeEvent;

    private enum STATE{
        VALID, INVALID, FETCHING
    }

    public DataProvider(Class<DataType> dataClass, Model model, Context context) {
        this(dataClass,model,context,null);
    }

    public DataProvider(final Class<DataType> dataClass, Model model, Context context, Event<DataType> dataChangeEvent) {
        super(model);
        this.context = context;
        this.dataChangeEvent = dataChangeEvent;
        setDataChangeObserver(new DataChangeObserver<DataType>() {
            @Override
            public void onDataInvalid() {
                INVALID_DATA.send(DataProvider.this.context, dataClass);
            }

            @Override
            public void onData(DataType data) {
                if (DataProvider.this.dataChangeEvent != null){
                    DataProvider.this.dataChangeEvent.send(DataProvider.this.context, data);
                }
            }
        });
    }
}
