package org.monroe.team.android.box.utils;

import java.lang.reflect.Field;

public class ResourceUtils {

    public static Integer resourceID(Class resourceClass, String resourceName){
        try {
            Field field = resourceClass.getField(resourceName);
            return  (Integer) field.get(null);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
