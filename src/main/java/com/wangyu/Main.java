package com.wangyu;

import com.Context.MyApplicationContext;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var context = new MyApplicationContext(ApplicationConfig.class);
        var userService  = context.getBean("userService");
        System.out.println(userService);
        ((UserService)userService).test();
        System.out.println(((UserService) userService).getBeanName());
        new CountDownLatch(1).await();
    }
}
