package com.lg.eventbus;

import io.reactivex.functions.Consumer;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestSub {
    public static void main(String[] a) throws InterruptedException, IOException {
//        EventBus.getInstance().subscribe("type1")
//                .subscribe(new Consumer<String>() {
//
//                    @Override
//                    public void accept(String msg) throws Exception {
//                        System.out.println(msg);
//                    }
//                });

        EventBus.getInstance().subscribe("type1", message -> {
            try {
                Thread.sleep(2000);
            } catch (Exception e){
                e.printStackTrace();
            }
            System.out.println(message);
        });

        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(6666666);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
