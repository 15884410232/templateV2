//package com.levy.collection.runner;
//
//import com.levy.dto.util.netty.RetryTask;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RetryRunner implements ApplicationRunner {
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        //启动子线程处理
//        Thread thread=new Thread(new RetryTask());
//        thread.start();
//    }
//}
