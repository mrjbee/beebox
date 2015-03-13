package org.monroe.team.android.box.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class DataManger {

    private final Map<Class,DataProvider> dataProviderMap;

    public DataManger() {
        this.dataProviderMap = new HashMap<>();
        construct();
    }

    protected abstract void construct();

    final protected  <Data extends Serializable> void put(Class<Data> dataClass, DataProvider<Data> dataProvider){
        dataProviderMap.put(dataClass, dataProvider);
    }

    final public <Data extends Serializable> Data fetch(Class<Data> dataClass) throws DataProvider.FetchException {
        return (Data) dataProviderMap.get(dataClass).fetch();
    }

    final public <Data extends Serializable> void invalidate(Class<Data> dataClass){
        getDataProvider(dataClass).invalidate();
    }

    private <Data extends Serializable> DataProvider getDataProvider(Class<Data> dataClass) {
        if (dataProviderMap.get(dataClass) == null)throw new NullPointerException("No data provider for "+dataClass.getName());
        return dataProviderMap.get(dataClass);
    }

}
