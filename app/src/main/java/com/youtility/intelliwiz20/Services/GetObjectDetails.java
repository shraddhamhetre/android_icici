package com.youtility.intelliwiz20.Services;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class GetObjectDetails {

    private GetObjectDetails() {}

    public static Map<String, Object> getFieldNamesAndValues(final Object obj, boolean publicOnly)
            /*throws IllegalArgumentException,IllegalAccessException*/
    {
        Class<? extends Object> c1 = obj.getClass();
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = c1.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String name = fields[i].getName();
            if (publicOnly) {
                if(Modifier.isPublic(fields[i].getModifiers())) {
                    Object value = null;
                    try {
                        value = fields[i].get(obj);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    map.put(name, value);
                }
            }
            else {
                fields[i].setAccessible(true);
                Object value = null;
                try {
                    value = fields[i].get(obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * @param args
     */
}