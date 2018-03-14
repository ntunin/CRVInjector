package com.ntunin.cybervision.crvinjector;

import android.nfc.FormatException;

import com.ntunin.cybervision.errno.ERRNO;
import com.ntunin.cybervision.res.Res;
import com.ntunin.cybervision.res.ResMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import whereareyou.ntunin.com.whereareyou.R;

/**
 * Created by nikolay on 01.04.17.
 */

public class AndroidXMLInjector extends MapInjector {
    public AndroidXMLInjector() {
        ResMap<String, Object> beans = loadBeans();
        instances = loadItems(beans);
    }

    private ResMap<String, Object> loadBeans() {
        ResMap<String, Object> beans = new ResMap();
        String[] resBeans = Res.array(R.array.beans);

        for(int i = 0; i < resBeans.length; i++) {
            String definition = resBeans[i].trim();
            int index = definition.indexOf(":");
            String typeAndName = definition.substring(0, index).trim();
            String content = definition.substring(index + 1).trim();
            index = typeAndName.indexOf(" ");
            String type = typeAndName.substring(0, index).trim();
            String name = typeAndName.substring(index).trim();
            Object o = null;
            switch (type) {
                case "Injectable":
                    o = createObject(content, beans);
                    break;
                case "Map":
                    o = createMap(content, beans);
                    break;
                case "Integer":
                    o = createInteger(content);
                    break;
                case "String":
                    o = createString(content);
                    break;
                case "Array":
                    o = createArray(content, beans);
                    break;
            }
            beans.put(name, o);
        }
        return beans;
    }

    private Object createObject(String content, ResMap<String, Object> beans) {
        try {
            int i = content.indexOf("(");
            if(i != -1) {
                String argument = content.substring(i + 1, content.indexOf(')'));
                content = content.substring(0, i);
                beans.put("argument", beans.get(argument));
            }

            Class classOfInstance = Class.forName(content);
            Constructor constructor = classOfInstance.getConstructor();
            Injectable object = (Injectable) constructor.newInstance();
            object.init(beans);

            return object;


        } catch (ClassNotFoundException e) {
            ERRNO.write(R.string.class_not_fount);
        } catch (NoSuchMethodException e) {
            ERRNO.write(R.string.constructor_not_fount);
        } catch (IllegalAccessException e) {
            ERRNO.write(R.string.illegal_access);
        } catch (InstantiationException e) {
            ERRNO.write(R.string.could_not_create);
        } catch (InvocationTargetException e) {
            ERRNO.write(R.string.could_not_invoke);
        }
        return null;
    }

    private Object createMap(String content, Map<String, Object> beans) {
        content = content.substring(content.indexOf("{") + 1, content.indexOf("}")).trim();
        String[] items = content.split(",");
        ResMap<String, Object> result = new ResMap<>();
        if(items.length == 1 && items[0].length() == 0) return result;
        for(int i = 0; i < items.length; i++) {
            String[] nameAndValue = items[i].split(":");
            String valueString = nameAndValue[1].trim();
            Object o = null;
            try {
                o = Double.parseDouble(valueString);
            } catch (Exception e) {
                o = beans.get(valueString);
                if(o == null) {
                    o = valueString;
                }
            }
            result.put(nameAndValue[0].trim(), o);
        }
        return result;
    }

    private Object createArray(String content, Map<String, Object> beans) {
        content = content.substring(content.indexOf("[") + 1, content.indexOf("]")).trim();
        String[] items = content.split(",");
        List<Object> result = new LinkedList<>();
        if(items.length == 1 && items[0].length() == 0) return result;
        for(int i = 0; i < items.length; i++) {
            String name = items[i];
            result.add(beans.get(name.trim()));
        }
        return result;
    }

    private Object createInteger(String content) {
        return Integer.parseInt(content);
    }

    private Object createString(String content) {
        return content.trim();
    }

    private Map<String, Object> loadItems( Map<String, Object> beans) {
        Map<String, Object> result = new HashMap<>();
        String[] resItems= Res.array(R.array.injector_items);
        for(int i = 0; i < resItems.length; i++) {
            String[] nameAndValue = resItems[i].split(":");
            result.put(nameAndValue[0].trim(), beans.get(nameAndValue[1].trim()));
        }
        return result;
    }


}
