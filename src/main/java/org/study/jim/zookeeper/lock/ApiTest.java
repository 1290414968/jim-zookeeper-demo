package org.study.jim.zookeeper.lock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ApiTest {
    public static void main(String[] args) throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for(int i=0;i<5;i++){
            new Thread(()->{
                try {
                    countDownLatch.await();
                    OriginalApiLockDemo distributedLock=new OriginalApiLockDemo();
                    distributedLock.lock(); //获得锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
            countDownLatch.countDown();
        }
        System.in.read();
    }
}
