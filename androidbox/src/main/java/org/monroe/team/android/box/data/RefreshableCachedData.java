package org.monroe.team.android.box.data;

import org.monroe.team.corebox.app.Model;

import java.util.Timer;
import java.util.TimerTask;

public class RefreshableCachedData<DataType> {

    private final CachedData<DataType> cachedData;
    private Timer refreshTimer;
    private DataObserver<DataType> observer;
    private final long refreshDelay;

    public RefreshableCachedData(CachedData<DataType> cachedData, long refreshDelay) {
        this.cachedData = cachedData;
        this.refreshDelay = refreshDelay;
    }

    public synchronized void activateRefreshing(){
        if (refreshTimer != null) return;
        refreshTimer = new Timer("cachedDataTimer_"+cachedData.dataClass.getName(), true);
        fetchAndSchedule(true);
    }

    private synchronized void fetchAndSchedule(boolean useCache) {
        if (isDeactivated()) return;
        cachedData.useCache = useCache;
        cachedData.invalidate();
        cachedData.fetch(true, new Data.FetchObserver<DataType>() {
            @Override
            public void onFetch(DataType dataType) {
                if (observer != null) {
                    observer.onData(dataType);
                }
                scheduleNextFetch();
            }

            @Override
            public void onError(Data.FetchError fetchError) {
                //TODO: think about error handling strategy
                scheduleNextFetch();
            }
        });
    }

    private boolean isDeactivated() {
        return refreshTimer == null;
    }

    private  synchronized void scheduleNextFetch() {
        if (!isDeactivated()) {
            refreshTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    fetchAndSchedule(false);
                }
            }, refreshDelay);
        }
    }

    public synchronized void deactivateRefreshing(){
        if (isDeactivated()) return;
        refreshTimer.cancel();
        refreshTimer.purge();
        refreshTimer = null;
    }


    public DataObserver<DataType> getObserver() {
        return observer;
    }

    public void setObserver(DataObserver<DataType> observer) {
        this.observer = observer;
    }

    public static interface DataObserver<DataType>{
        public void onData(DataType data);
    }

    public abstract static class CachedData<DataType> extends Data<DataType>{

        private boolean useCache = false;

        public CachedData(Class<DataType> dataClass, Model model) {
            super(dataClass, model);
        }

        @Override
        final protected DataType provideData() {
            if (useCache){
                DataType dataType = provideDataFromCache();
                if (dataType == null){
                    dataType = provideDataAndCache();
                }
                return dataType;
            }else{
                return provideDataAndCache();
            }
        }

        protected abstract DataType provideDataAndCache();
        protected abstract DataType provideDataFromCache();

    }
}
