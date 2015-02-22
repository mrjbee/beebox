package org.monroe.team.android.box.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SerializationMap <Key extends Serializable,Value extends Serializable> {

    private final String fileName;
    private final Context context;
    private Map<Key,Value> map;

    public SerializationMap(String fileName, Context context) {
        this.fileName = fileName;
        this.context = context;
    }

    public synchronized Value get(Key key){
        checkRestoringDone();
        return map.get(key);
    }

    public synchronized Value put(Key key, Value value){
        checkRestoringDone();
        Value oldValue = map.put(key, value);
        persistAll();
        return oldValue;
    }

    public synchronized boolean has(Key key){
        checkRestoringDone();
        return map.containsKey(key);
    }

    public synchronized Collection<Value> values(){
        checkRestoringDone();
        return map.values();
    }

    public synchronized Set<Key> keys(){
        checkRestoringDone();
        return map.keySet();
    }

    private void checkRestoringDone() {
        if(map == null) restoreAll();
    }

    public synchronized void restoreAll() {
        File file = context.getFileStreamPath(fileName);
        if(file == null || !file.exists()) {
            map = new HashMap<>();
            return;
        }
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object readObject = is.readObject();

            if (readObject != null) {
                map = (Map<Key, Value>) readObject;
            } else {
                throw new RuntimeException("Empty object read");
            }
        } catch (InvalidClassException ice){
           if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {}
           }
           if (!file.delete()) {
               restoreAll();
           }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {}
            }
        }

    }

    public synchronized void persistAll() {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {}
            }
        }
    }

    public Value remove(Key key) {
        checkRestoringDone();
        Value value = map.remove(key);
        persistAll();
        return value;
    }
}
