package com.mascara.ioc_container.loader;

import com.mascara.ioc_container.annotation.Autowire;
import com.mascara.ioc_container.annotation.Component;
import com.mascara.ioc_container.annotation.PostCostruct;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class ContextLoader {
    private static ContextLoader INSTANCE = null;
    private final Map<String, Object> nameToInstance = new HashMap<>();

    public static ContextLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContextLoader();
        }
        return INSTANCE;
    }

    private ContextLoader() {

    }

    public synchronized void load(String scanPackage) {
        Reflections reflections = new Reflections(scanPackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);

        initiateInstance(classes);
        for (var entry : nameToInstance.entrySet()) {
            var instance = entry.getValue();
            injectFieldValue(instance);
            invokePostInitiate(instance);
        }
        executeRunner();
    }

    private void initiateInstance(Set<Class<?>> classes) {
        for (var clazz : classes) {
            try {
                var c = Class.forName(clazz.getName());
                var instance = c.getDeclaredConstructor().newInstance();
                nameToInstance.put(clazz.getName(), instance);
            } catch (Exception e) {
                throw new RuntimeException("Cannot initialize new instance for " + clazz.getName());
            }
        }
    }

    private void injectFieldValue(final Object instance) {
        var fields = instance.getClass().getDeclaredFields();
        Arrays.stream(fields)
                .filter(field -> Arrays.stream(field.getDeclaredAnnotations())
                        .anyMatch(a -> a.annotationType() == Autowire.class))
                .forEach(field -> {
                    var value = nameToInstance.get(field.getType().getName());
                    field.setAccessible(true);
                    try {
                        field.set(instance, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Cannot inject dependency " + field.getClass().getName()
                                + " to " + instance.getClass().getName());
                    }
                });
    }

    private void executeRunner() {
        List<Object> runners = nameToInstance.values()
                .stream()
                .filter(Runner.class::isInstance)
                .toList();
        if (runners.isEmpty()) {
            return;
        }
        if (runners.size() > 1) {
            throw new RuntimeException("Cannot have more than 1 runner class");
        }
        ((Runner) runners.get(0)).run();
    }


    private void invokePostInitiate(Object instance) {
        List<Method> postMethods = Arrays.stream(instance.getClass().getDeclaredMethods())
                .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                        .anyMatch(a -> a.annotationType() == PostCostruct.class))
                .toList();
        if (postMethods.isEmpty()) {
            return;
        }

        if (postMethods.size() > 1) {
            throw new RuntimeException("Cannot have more than one post initiate method");
        }
        try {
            var method = postMethods.get(0);
            method.setAccessible(true);
            method.invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        return (T) nameToInstance.get(clazz.getName());
    }
}
