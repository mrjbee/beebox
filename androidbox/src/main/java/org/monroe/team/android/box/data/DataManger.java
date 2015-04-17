package org.monroe.team.android.box.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class DataManger {

    private final Map<Class,Data> dataProviderMap;

    public DataManger() {
        this.dataProviderMap = new HashMap<>();
        construct();
    }

    protected abstract void construct();

    final protected  <DataType extends Serializable> void put(Class<DataType> dataClass, DataProvider<DataType> dataProvider){
        dataProviderMap.put(dataClass, dataProvider);
    }

    final public <DataType extends Serializable> DataType fetch(Class<DataType> dataClass) throws DataProvider.FetchException {
        return (DataType) dataProviderMap.get(dataClass).fetch();
    }

    final public <DataType extends Serializable> void invalidate(Class<DataType> dataClass){
        getDataProvider(dataClass).invalidate();
    }

    private <DataType extends Serializable> Data<DataType> getDataProvider(Class<DataType> dataClass) {
        if (dataProviderMap.get(dataClass) == null)throw new NullPointerException("No data provider for "+dataClass.getName());
        return dataProviderMap.get(dataClass);
    }

}
