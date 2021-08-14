package com.Context;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class MyApplicationContext {
    @Setter
    @Getter
    private Class config;
    private Map<String, Object> singletonBeans;
    private Map<String, BeanDefinition> beanDefinitionMap;

    public MyApplicationContext(Class config) {
        this.config = config;
        singletonBeans = new ConcurrentHashMap<>();
        scan(config);
        init();
    }

    private void scan(Class config) {
        var componentScan = (ComponentScan) config.getDeclaredAnnotation(ComponentScan.class);
        var path = componentScan.value();
        System.out.println(path);
        var classLoader = this.getClass().getClassLoader();
        var url = classLoader.getResource(path.replaceAll("\\.", "/"));
        var file = new File(url.getFile());
        var classList = Arrays.stream(file.list()).parallel()
                .filter((f) -> f.endsWith(".class"))
                .map((f) -> path + "." + f.substring(0, f.lastIndexOf(".")))
                .collect(Collectors.toList());
        beanDefinitionMap = classList.stream().map(s -> {
                    try {
                        return classLoader.loadClass(s);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .filter(c -> c.isAnnotationPresent(Component.class))
                .collect(Collectors.toMap(c -> c.getAnnotation(Component.class).value(), c -> {
                    var definition = new BeanDefinition();
                    definition.setName(c.getAnnotation(Component.class).value());
                    definition.setScope(c.getAnnotation(Scope.class).value());
                    if (definition.getScope() == null)
                        definition.setScope("singleton");
                    definition.setClazz(c);
                    return definition;
                }));
    }

    public Object getBean(String s) {
        if (beanDefinitionMap.containsKey(s)) {
            var beanDefinition = beanDefinitionMap.get(s);
            if (beanDefinition.getScope().equals("singleton")) {
                return singletonBeans.get(s);
            }
            return createBean(beanDefinition);
        } else
            throw new NullPointerException();
    }

    private void init() {
        beanDefinitionMap.values().stream()
                .filter(d -> "singleton".equals(d.getScope()))
                .forEach(d -> singletonBeans.put(d.getName(),createBean(d)));
    }

    private Object createBean(BeanDefinition definition) {
        try {
            final var clazz = definition.getClazz();
            var instance = clazz.getDeclaredConstructor().newInstance();
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(f->f.isAnnotationPresent(Autowired.class))
                    .forEach(f->{
                        try {
                            f.setAccessible(true);
                            f.set(instance,getBean(f.getName()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
            if(instance instanceof BeanNameAware){
                ((BeanNameAware)instance).setBeanName(definition.getName());
            }
            return instance;
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
