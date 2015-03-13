package org.monroe.team.android.box.data;

import android.content.Context;

import org.monroe.team.android.box.event.Event;
import org.monroe.team.android.box.event.GenericEvent;
import org.monroe.team.corebox.services.BackgroundTaskManager;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class DataProvider<DataType extends Serializable> {

    public static final GenericEvent<Class> INVALID_DATA = new GenericEvent<>("INVALID_DATA");

    private enum STATE{
        VALID, INVALID, FETCHING
    }

    protected final org.monroe.team.corebox.app.Model model;
    protected final Context context;
    private final Event<DataType> dataChangeEvent;
    private final Class<DataType> dataClass;
    private long lastFetchTaskRunId = 0;

    private BackgroundTaskManager.BackgroundTask<DataType> fetchDataTask;
    private DataType data;
    private STATE dataState = STATE.INVALID;
    private List<FetchObserver<DataType>> awaitingObservers = new ArrayList<>();

    public DataProvider(Class<DataType> dataClass, org.monroe.team.corebox.app.Model model, Context context) {
        this(dataClass, model, context, null);
    }

    public DataProvider(Class<DataType> dataClass, org.monroe.team.corebox.app.Model model, Context context, Event<DataType> event) {
        this.model = model;
        this.context = context;
        dataChangeEvent = event;
        this.dataClass = dataClass;
    }

    protected abstract DataType provideData();


    final public DataType fetch() throws FetchException{
        SynchronousFetchAdapter<DataType> synchronousFetchAdapter = new SynchronousFetchAdapter<DataType>();
        fetch(false, synchronousFetchAdapter);
        return synchronousFetchAdapter.get();
    }

    final public synchronized void fetch(boolean updateRequired, final FetchObserver<DataType> fetchObserver){
        if (data == null || (updateRequired && STATE.VALID != dataState)){
            awaitingObservers.add(fetchObserver);
            if (STATE.INVALID == dataState){
                doFetch();
            }
        }else {
            model.getResponseHandler().post(new Runnable() {
                @Override
                public void run() {
                    fetchObserver.onFetch(data);
                }
            });
        }
    }

    final public synchronized void invalidate() {
        dataState = STATE.INVALID;
        if (fetchDataTask != null){
            fetchDataTask.cancel();
            doFetch();
        } else {
            INVALID_DATA.send(context, dataClass);
        }
    }

    final public synchronized void doFetch() {
        dataState = STATE.FETCHING;
        runFetching(0, ++lastFetchTaskRunId);
    }

    private synchronized void updateData(final DataType data, long runId) {

        if (runId != lastFetchTaskRunId) return;
        fetchDataTask = null;

        this.data = data;
        dataState = STATE.VALID;
        if (dataChangeEvent != null){
            dataChangeEvent.send(context, data);
        }
        model.getResponseHandler().post(new Runnable() {
            @Override
            public void run() {
                Lists.iterateAndRemove(awaitingObservers, new Closure<Iterator<FetchObserver<DataType>>, Boolean>() {
                    @Override
                    public Boolean execute(Iterator<FetchObserver<DataType>> arg) {
                        arg.next().onFetch(data);
                        arg.remove();
                        return true;
                    }
                });
            }
        });
    }

    protected void runFetching(final int times, final long runId) {
        fetchDataTask =
                model.usingService(BackgroundTaskManager.class).execute(new Callable<DataType>() {
                    @Override
                    public DataType call() throws Exception {
                        return provideData();
                    }
                }, new BackgroundTaskManager.TaskCompletionNotificationObserver<DataType>() {
                    @Override
                    public void onSuccess(DataType dataType) {
                        updateData(dataType, runId);
                    }

                    @Override
                    public void onFails(Exception e) {
                        updateError(runId, awaitingObservers, times, e);
                    }
                });
    }

    private synchronized void updateError(long runId, final List<FetchObserver<DataType>> observers, final int times, final Exception e){
        if (runId != lastFetchTaskRunId) return;
        model.getResponseHandler().post(new Runnable() {
            @Override
            public void run() {
                updateOnError(observers,times, e);
            }
        });
    }

    protected void updateOnError(List<FetchObserver<DataType>> observers, int times, Exception e) {
        Lists.iterateAndRemove(observers, new Closure<Iterator<FetchObserver<DataType>>, Boolean>() {
            @Override
            public Boolean execute(Iterator<FetchObserver<DataType>> arg) {
                arg.next().onError(FetchError.FAILED);
                arg.remove();
                return true;
            }
        });
    }


    public interface FetchObserver<Data> {
        public void onFetch(Data data);
        public void onError(FetchError fetchError);
    }

    public static class FetchError {
        public static FetchError FAILED = new FetchError();
    }

    public static class FetchException extends Exception{

        public final FetchError error;

        public FetchException(FetchError error) {
            this.error = error;
        }
    }

    private class SynchronousFetchAdapter<DataType> implements FetchObserver<DataType> {

        FetchError INTERRUPTED = new FetchError();

        private DataType result;
        private FetchError error;
        private Object monitor = new Object();

        @Override
        public synchronized void onFetch(DataType data) {
            result = data;
        }

        @Override
        public synchronized void onError(FetchError fetchError) {
            error = fetchError;
        }

        public synchronized DataType get() throws FetchException{
            if (result != null){
                return result;
            }
            if (error != null){
                throw new FetchException(error);
            }
            synchronized (monitor){
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    throw new FetchException(INTERRUPTED);
                }
            }
            if (result != null){
                return result;
            }else {
                throw new FetchException(error);
            }
        }

    }
}
