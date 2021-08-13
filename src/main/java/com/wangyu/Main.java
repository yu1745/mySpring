package com.wangyu;

import com.Context.MyApplicationContext;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var context = new MyApplicationContext(ApplicationConfig.class);
        new CountDownLatch(1).await();
    }
}
