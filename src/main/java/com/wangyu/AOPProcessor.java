package com.wangyu;

import com.Context.BeanPostProcessor;
import com.Context.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

@Component("AOPProcessor")
public class AOPProcessor implements BeanPostProcessor {
    @Override
    public Object before(String name, Object object) {
        return object;
    }

    @Override
    public Object after(String name, Object object) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), object.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("AOPProcessor.invoke");
//                System.out.println("method = " + method + ", args = " + Arrays.deepToString(args));
                return method.invoke(object, args);
            }
        });
    }
}
