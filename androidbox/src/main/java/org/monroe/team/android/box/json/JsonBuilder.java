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

    protected abstract Object buildJson();

    public static ObjectBuilder object() {
        return new ObjectBuilder();
    }

    public static ArrayBuilder array() {
        return new ArrayBuilder();
    }

    public static class ArrayBuilder extends JsonBuilder {

        private List<Object> valuesList = new ArrayList<>();

        public ArrayBuilder add(Object value) {
            valuesList.add(value);
            return this;
        }

        @Override
        protected Object buildJson() {
            JSONArray array = new JSONArray();
            for (Object value : valuesList) {
                if (value instanceof JsonBuilder){
                    JsonBuilder builder = (JsonBuilder) value;
                    value = builder.buildJson();
                }
                array.put(value);
            }
            return array;
        }
    }

    public static class ObjectBuilder extends JsonBuilder{

        private final Map<String,Object> fieldsMap = new HashMap<>();

        public ObjectBuilder field(String fieldName, Object fieldValue) {
            fieldsMap.put(fieldName, fieldValue);
            return this;
        }

        @Override
        protected Object buildJson() {
            JSONObject object = new JSONObject();
            for (Map.Entry<String,Object> entry:fieldsMap.entrySet()){
                Object value = entry.getValue();
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
