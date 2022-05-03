package io.eeaters.example;

import org.junit.Test;

public class MetaExample {

    @Test
    public void test() {
        String name = MetaExample.class.getName();
        System.out.println("name = " + name);
        int i = name.lastIndexOf(".");
        String packageName = name.substring(0, i);
        String className = name.substring(i + 1);
        System.out.println("packageName = " + packageName);
        System.out.println("className = " + className);
    }
}
