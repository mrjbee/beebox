package org.monroe.team.android.box.manager;

import android.content.SharedPreferences;

import org.monroe.team.android.box.Closure;

public class SettingManager {


    private final SharedPreferences preferences;

    public SettingManager(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public <Type> void set(SettingItem<Type> item, Type value){
        item.set(preferences,value);
    }

    public <Type> Type get(SettingItem<Type> item){
       return item.getOrDefault(preferences);
    }

    public <Type> void unset(SettingItem<Type> item){
        set(item,null);
    }

    public <Type> boolean has(SettingItem<Type> item){
      return item.get(preferences) != null;
    }

    public <Type> Type getAndSet(SettingItem<Type> item, Type value){
        Type answer = item.getOrDefault(preferences);
        if (answer != value){
            set(item,value);
        }
        return answer;
    }

    public <ItemType, AnswerType> AnswerType getAs(SettingItem<ItemType> setting, Closure<ItemType,AnswerType> convert) {
        return convert.execute(setting.getOrDefault(preferences));
    }

    public static class SettingItem <Type>{

        private final String id;
        private final Type defaultValue;
        private final Class<Type> valueClass;

        public SettingItem(String id, Class<Type> typeClass, Type defaultValue) {
            this.id = id;
            this.defaultValue = defaultValue;
            this.valueClass = typeClass;
        }

        private void set(SharedPreferences preferences, Type value){
            SharedPreferences.Editor editor = preferences.edit();
            if (value == null) {
                editor.remove(id).commit();
                return;
            }
            if (valueClass == String.class){
                editor.putString(id, (String) value);
            }else if (valueClass == Integer.class){
                editor.putInt(id, (Integer) value);
            } else if (valueClass == Float.class){
                editor.putFloat(id, (Float) value);
            }else if (valueClass == Boolean.class){
                editor.putBoolean(id, (Boolean) value);
            } else if (valueClass == Long.class){
                editor.putLong(id, (Long) value);
            } else {
                throw new IllegalStateException();
            }
            editor.commit();
        }

        private Type get(SharedPreferences preferences){
            if (!preferences.contains(id)) return null;
            if (valueClass == String.class){
                return (Type) preferences.getString(id,"");
            }else if (valueClass == Integer.class){
                return (Type)(Integer) preferences.getInt(id,0);
            } else if (valueClass == Float.class){
                return (Type)(Float) preferences.getFloat(id, 0);
            }else if (valueClass == Boolean.class){
                return (Type)(Boolean) preferences.getBoolean(id, false);
            } else if (valueClass == Long.class){
                return (Type) (Long)preferences.getLong(id, 0l);
            } else {
                throw new IllegalStateException();
            }
        }

        private Type getOrDefault(SharedPreferences preferences){
            Type answer = get(preferences);
            return answer == null ? defaultValue:answer;
        }

    }

    public static class Flag extends SettingItem<Boolean> {
        public Flag(String id, boolean defaultValue) {
            super(id, Boolean.class, defaultValue);
        }

    }



}
