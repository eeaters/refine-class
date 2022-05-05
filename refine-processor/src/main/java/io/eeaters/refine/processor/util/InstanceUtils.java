package io.eeaters.refine.processor.util;

import java.lang.reflect.Constructor;

public class InstanceUtils {

    public static <T> T getInstance(Class<T> t) {
        String implClassName = t.getName() + "Impl";
        try {
            Class<?> aClass = Class.forName(implClassName);
            return (T) aClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T getInstance(Class<T> t, Throwable throwable) {
        String implClassName = t.getName() + "Impl";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> aClass = classLoader.loadClass(implClassName);
            Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(throwable.getClass());
            return (T) declaredConstructor.newInstance(throwable);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
