package org.monroe.team.android.box.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JsonBuilder {

    public static Json build(JsonBuilder builder){
        return Json.createFromObject(builder.buildJson());
    }

    protected abstract java.lang.Object buildJson();

    public static Object object() {
        return new Object();
    }

    public static Array array() {
        return new Array();
    }

    public static class Array extends JsonBuilder {

        private List<java.lang.Object> valuesList = new ArrayList<>();

        public Array add(java.lang.Object value) {
            valuesList.add(value);
            return this;
        }

        @Override
        protected java.lang.Object buildJson() {
            JSONArray array = new JSONArray();
            for (java.lang.Object value : valuesList) {
                if (value instanceof JsonBuilder){
                    JsonBuilder builder = (JsonBuilder) value;
                    value = builder.buildJson();
                }
                array.put(value);
            }
            return array;
        }
    }

    public static class Object extends JsonBuilder{

        private final Map<String, java.lang.Object> fieldsMap = new HashMap<>();

        public Object field(String fieldName, java.lang.Object fieldValue) {
            fieldsMap.put(fieldName, fieldValue);
            return this;
        }

        @Override
        protected java.lang.Object buildJson() {
            JSONObject object = new JSONObject();
            for (Map.Entry<String, java.lang.Object> entry:fieldsMap.entrySet()){
                java.lang.Object value = entry.getValue();
                if (value instanceof JsonBuilder){
                    value = ((JsonBuilder)value).buildJson();
                }
                try {
                    object.put(entry.getKey(), value);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            return object;
        }
    }
}
