package org.monroe.team.android.box.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public abstract class Json<KeyType>{

    public abstract boolean isArray();
    public abstract boolean isObject();
    public abstract boolean exists(KeyType key);

    final public JsonArray asArray(){
        return (JsonArray) this;
    }

    final public JsonObject asObject(){
        return (JsonObject) this;
    }

    protected abstract Object get(KeyType key);

    final public boolean isArray(KeyType key){
       Object value = get(key);
       return value != null && value instanceof JSONArray;
    }

    final public boolean isObject(KeyType key){
        Object value = get(key);
        return value != null && value instanceof JSONObject;
    }

    public final boolean is(KeyType key, Class valueClass){
        Object value = get(key);
        return value != null && valueClass.isInstance(value);
    }

    final public JsonArray asArray(KeyType key){
        return new JsonArray((JSONArray) get(key));
    }

    final public JsonObject asObject(KeyType key){
        return new JsonObject((JSONObject) get(key));
    }

    final public String asString(KeyType key){
        return value(key, String.class);
    }

    final public <ValueType> ValueType value(KeyType key, Class<ValueType> valueClass){
        return (ValueType) get(key);
    }

    final public <ValueType> ValueType value(KeyType key, ValueType defaultValue){
        ValueType answer = (ValueType) get(key);
        if (answer == null) answer = defaultValue;
        return answer;
    }

    public static Json createFromString(String jsonString) throws JSONException {
        jsonString = jsonString.trim();
        if (jsonString.isEmpty()) return null;

        Json answer;
        //TODO: Replace to tokinezier
        if (jsonString.startsWith("{")){
            //object
            JSONObject object = new JSONObject(jsonString);
            answer = new JsonObject(object);
        }else if(jsonString.startsWith("[")){
            //array
            JSONArray array = new JSONArray(jsonString);
            answer = new JsonArray(array);
        } else {
            throw new JSONException("Invalid start of string = "+jsonString);
        }
        return answer;
    }

    public static Json createFromObject(Object jsonObject) {
        if (jsonObject instanceof JSONObject){
            return new JsonObject((JSONObject) jsonObject);
        }else if (jsonObject instanceof JSONArray){
            return new JsonArray((JSONArray) jsonObject);
        }
        throw new IllegalArgumentException("Should be a JSONArray or JSONObject, but got "+jsonObject.getClass().getName());
    }

    public abstract String toJsonString();

    public static class JsonArray extends Json<Integer>{

        private final JSONArray nativeObject;

        private JsonArray(JSONArray nativeObject) {
            this.nativeObject = nativeObject;
        }

        @Override
        public boolean isArray() {
            return true;
        }

        @Override
        public boolean isObject() {
            return false;
        }

        @Override
        public boolean exists(Integer key) {
            return nativeObject.opt(key) != null;
        }

        @Override
        protected Object get(Integer key) {
            return nativeObject.opt(key);
        }

        @Override
        public String toJsonString() {
            return nativeObject.toString();
        }

        public int size(){
            return nativeObject.length();
        }
    }

    public static class JsonObject extends Json<String>{

        private final JSONObject object;

        public JsonObject(JSONObject object) {
            this.object = object;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isObject() {
            return true;
        }

        @Override
        public boolean exists(String key) {
            return object.opt(key) != null;
        }

        @Override
        protected Object get(String key) {
            return object.opt(key);
        }

        @Override
        public String toJsonString() {
            return object.toString();
        }

        public Iterator<String> keys(){
            return object.keys();
        }
    }
}
