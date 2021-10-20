package org.yaml.snakeyaml;

import org.yaml.snakeyaml.annotation.YamlClass;

import java.util.*;

public class SecClass {
    public static final ArrayList<Class> whiteList = new ArrayList();
    static {
        whiteList.add(CharSequence.class);
        whiteList.add(Map.class);
        whiteList.add(List.class);
        whiteList.add(Number.class);
        whiteList.add(Set.class);
    }

    public static Class forName(String name) throws ClassNotFoundException {
        return forName(name,false,Thread.currentThread().getContextClassLoader());
    }
    public static Class forName(String name, boolean initialize,
                                ClassLoader loader) throws ClassNotFoundException {
        Class type = Class.forName(name,initialize,loader);
        if (!type.equals(Object.class)){
            if (Object.class.isAssignableFrom(type)){
                if (type.getAnnotation(YamlClass.class)==null){
                    if (!type.isArray()){
                        boolean ok = false;
                        Iterator<Class> classIterator = whiteList.iterator();
                        while (classIterator.hasNext()){
                            Class whiteClass = classIterator.next();
                            if (whiteClass.isAssignableFrom(type)){
                                ok = true;
                                break;
                            }
                        }
                        if (!ok){
                            throw new ClassNotFoundException(name);
                        }
                    }
                }
            }
        }
        return type;
    }

}
