package org.monroe.team.android.box.data;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public abstract class PersistRangeDataProvider<TypeKey, TypeData> {

    private Map<String, Data<TypeData>> keyTypeDataHashMap = new HashMap<>();

    final synchronized public Data<TypeData> getOrCreate(TypeKey forKey){
        String key = convertToStringKey(forKey);
        Data<TypeData> data = keyTypeDataHashMap.get(key);
        if (data == null){
            //build data
            Data<TypeData> answer = buildDataAndPutInMap(forKey, key);
            return answer;
        } else {
            return data;
        }
    }


    final synchronized public Data<TypeData> get(TypeKey forKey){
        String key = convertToStringKey(forKey);
        Data<TypeData> data = keyTypeDataHashMap.get(key);
        if (data == null){
            return null;
        } else {
            return data;
        }
    }

    final synchronized public Data<TypeData> remove(TypeKey forKey){
        String key = convertToStringKey(forKey);
        return keyTypeDataHashMap.remove(key);
    }

    private Data<TypeData> buildDataAndPutInMap(TypeKey forKey, String key) {
        Data<TypeData> answer = buildData(forKey);
        keyTypeDataHashMap.put(key,answer);
        return answer;
    }

    protected abstract Data<TypeData> buildData(TypeKey key);
    protected abstract String convertToStringKey(TypeKey key);

    public synchronized void invalidateAll() {
        for (Map.Entry<String, Data<TypeData>> stringDataEntry : keyTypeDataHashMap.entrySet()) {
            stringDataEntry.getValue().invalidate();
        }
    }
}
