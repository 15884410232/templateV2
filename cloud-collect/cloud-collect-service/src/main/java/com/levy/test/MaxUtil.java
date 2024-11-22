package com.levy.test;

import java.util.concurrent.ArrayBlockingQueue;

public class MaxUtil {

    public static ArrayBlockingQueue<String> outMaxSize=new ArrayBlockingQueue<>(10000);

    public static ThreadLocal<String> urlList=new ThreadLocal<>();

}
