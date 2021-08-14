package com.Context;

import lombok.Getter;
import lombok.Setter;

public class BeanDefinition {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String scope;
    @Getter
    @Setter
    private Class clazz;
}
