package com.Context;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class MyApplicationContext {
    @Setter
    @Getter
    private Class config;
    private Map<String, Bean> singletonBeans;
    private Map<String, BeanDefinition> beanDefinitionMap;

    public MyApplicationContext(Class config) {
        this.config = config;
        singletonBeans = new ConcurrentHashMap<>();
//        beanDefinitionMap = new ConcurrentHashMap();
        var componentScan = (ComponentScan) config.getDeclaredAnnotation(ComponentScan.class);
        var path = componentScan.value();
        System.out.println(path);
        var classLoader = this.getClass().getClassLoader();
        var url = classLoader.getResource(path.replaceAll("\\.", "/"));
        var file = new File(url.getFile());
        var list = Arrays.stream(file.list()).parallel()
                .filter((f) -> f.endsWith(".class"))
                .map((f) -> path + "." + f.substring(0, f.lastIndexOf(".")))
                .collect(Collectors.toList());
        beanDefinitionMap = list.stream().map(s -> {
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
                    definition.setScope(c.getAnnotation(Scope.class).value());
                    if (definition.getScope() == null)
                        definition.setScope("singleton");
                    return definition;
                }));
    }

    public Object getBean(String s) {
        if (beanDefinitionMap.containsKey(s)) {
            var beanDefinition = beanDefinitionMap.get(s);
            if(beanDefinition.getScope().equals("singleton")){
                return singletonBeans.get(s);
            }
            return null;
        } else
            throw new NullPointerException();
    }
}
