package org.monroe.team.android.box.data;

import android.content.Context;

import org.monroe.team.corebox.log.L;
import org.monroe.team.corebox.services.BackgroundTaskManager;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public abstract class Data<DataType> {

    private enum STATE{
        VALID, INVALID, FETCHING
    }

    protected final org.monroe.team.corebox.app.Model model;
    private long lastFetchTaskRunId = 0;

    private BackgroundTaskManager.BackgroundTask<DataType> fetchDataTask;
    private DataType data;
    private STATE dataState = STATE.INVALID;
    private List<FetchObserver<DataType>> awaitingObservers = new ArrayList<>();
    private List<DataChangeObserver<DataType>> dataChangeObserverList = new ArrayList<>();

    public Data(org.monroe.team.corebox.app.Model model) {
        this.model = model;
    }

    @Deprecated
    public Data(Class<DataType> dataClass, org.monroe.team.corebox.app.Model model) {
        this.model = model;
    }

    @Deprecated
    public DataChangeObserver<DataType> getDataChangeObserver() {
        return dataChangeObserverList.get(0);
    }

    public boolean removeDataChangeObserver(DataChangeObserver<DataType> observer) {
        return dataChangeObserverList.remove(observer);
    }

    public void addDataChangeObserver(DataChangeObserver<DataType> observer) {
        dataChangeObserverList.add(observer);
    }

    @Deprecated
    public void setDataChangeObserver(DataChangeObserver<DataType> dataChangeObserver) {
        addDataChangeObserver(dataChangeObserver);
    }

    public DataType getData() {
        return data;
    }

    public boolean isValid() {
        return dataState == STATE.VALID;
    }

    protected abstract DataType provideData();


    final public DataType fetch() throws FetchException{
        SynchronousFetchAdapter<DataType> synchronousFetchAdapter = new SynchronousFetchAdapter<DataType>();
        fetch(true, synchronousFetchAdapter);
        return synchronousFetchAdapter.get();
    }

    //updateRequired - allows to return old value if exists even if it marks as invalid
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
            Lists.each(dataChangeObserverList, new Closure<DataChangeObserver<DataType>, Void>() {
                @Override
                public Void execute(DataChangeObserver<DataType> arg) {
                    arg.onDataInvalid();
                    return null;
                }
            });
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
        Lists.each(dataChangeObserverList, new Closure<DataChangeObserver<DataType>, Void>() {
            @Override
            public Void execute(DataChangeObserver<DataType> arg) {
                arg.onData(data);
                return null;
            }
        });
        final List<FetchObserver<DataType>> copy = copyAndClear();

        model.getResponseHandler().post(new Runnable() {
            @Override
            public void run() {
                for (FetchObserver<DataType> dataTypeFetchObserver : copy) {
                    dataTypeFetchObserver.onFetch(data);
                }
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
        dataState = STATE.INVALID;
        fetchDataTask = null;
        final List<FetchObserver<DataType>> observersCopy = copyAndClear();
        model.getResponseHandler().post(new Runnable() {
            @Override
            public void run() {
                updateOnError(observersCopy, times, e);
            }
        });
    }

    private List<FetchObserver<DataType>> copyAndClear() {
        final List<FetchObserver<DataType>> observersCopy = new ArrayList<>(this.awaitingObservers);
        awaitingObservers.clear();
        return observersCopy;
    }

    protected void updateOnError(List<FetchObserver<DataType>> observers, int times, Exception e) {
        for (FetchObserver<DataType> observer : observers) {
            observer.onError(new ExceptionFetchError(e));
        }
    }

    public interface DataChangeObserver<Data>{
        public void onDataInvalid();
        public void onData(Data data);
    }

    public interface FetchObserver<Data> {
        public void onFetch(Data data);
        public void onError(FetchError fetchError);
    }

    public static interface FetchError {
        public String message();
        Throwable originalException();
    }

    public static class DefaultFetchError implements FetchError {

        public final String msg;

        public DefaultFetchError(String msg) {
            this.msg = msg;
        }

        public String message(){
            return msg;
        }

        @Override
        public Throwable originalException() {
            return new IllegalStateException(message());
        }
    }


    public static class ExceptionFetchError implements FetchError {

        public final Throwable cause;

        public ExceptionFetchError(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public String message() {
            return cause.getMessage() + ":"+ cause.getClass();
        }

        @Override
        public Throwable originalException() {
            return cause;
        }
    }

    public static class FetchException extends Exception{

        public final FetchError error;

        public FetchException(FetchError error) {
            super(error.message());
            this.error = error;
        }

        public FetchException(ExceptionFetchError error) {
            super(error.cause);
            this.error = error;
        }
    }

    private class SynchronousFetchAdapter<DataType> implements FetchObserver<DataType> {

        FetchError INTERRUPTED = new DefaultFetchError("Fetching interrupted");

        private DataType result;
        private FetchError error;

        @Override
        public synchronized void onFetch(DataType data) {
            result = data;
            this.notify();
        }

        @Override
        public synchronized void onError(FetchError fetchError) {
            error = fetchError;
            this.notify();
        }

        public DataType get() throws FetchException{
            synchronized (this){
                if (result != null){
                    return result;
                }
                if (error != null){
                    throw new FetchException(error);
                }

                try {
                    this.wait();
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
