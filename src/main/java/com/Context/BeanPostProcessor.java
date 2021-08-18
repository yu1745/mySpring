package com.Context;

public interface BeanPostProcessor {
    Object before(String name,Object object);
    Object after(String name,Object object);
}
