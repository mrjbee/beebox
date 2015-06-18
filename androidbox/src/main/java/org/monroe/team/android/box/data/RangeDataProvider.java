package org.monroe.team.android.box.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class RangeDataProvider<TypeKey, TypeData> {

    private HashMap<String, WeakReference<Data<TypeData>>> keyTypeDataHashMap = new HashMap<>();

    final synchronized public Data<TypeData> getOrCreate(TypeKey forKey){
        String key = convertToStringKey(forKey);
        WeakReference<Data<TypeData>> weakReference = keyTypeDataHashMap.get(key);
        if (weakReference == null){
            //build data
            Data<TypeData> answer = buildDataAndPutInMap(forKey, key);
            return answer;
        } else {
            Data<TypeData> answer = weakReference.get();
            if (answer == null){
                answer = buildDataAndPutInMap(forKey, key);
            }
            return answer;
        }
    }


    final synchronized public Data<TypeData> get(TypeKey forKey){
        String key = convertToStringKey(forKey);
        WeakReference<Data<TypeData>> weakReference = keyTypeDataHashMap.get(key);
        if (weakReference == null){
           return null;
        } else {
            Data<TypeData> answer = weakReference.get();
            if (answer == null){
                return null;
            }
            return answer;
        }
    }

    private Data<TypeData> buildDataAndPutInMap(TypeKey forKey, String key) {
        Data<TypeData> answer = buildData(forKey);
        keyTypeDataHashMap.put(key, new WeakReference<Data<TypeData>>(answer));
        return answer;
    }

    protected abstract Data<TypeData> buildData(TypeKey key);
    protected abstract String convertToStringKey(TypeKey key);
}
