package org.study.jim.zookeeper.lock;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class CuratorApiLockDemo {
    private String CURATOR_ROOT = "/curator-jim-locks";
    private void lock(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework framework =  CuratorFrameworkFactory.newClient("47.52.240.168",retryPolicy);
        framework.start();
        InterProcessMutex mutex = new InterProcessMutex(framework,CURATOR_ROOT);
        try {
            mutex.acquire();
            System.out.println(Thread.currentThread().getName()+"->"+"获取锁成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for(int i=0;i<5;i++){
            new Thread(()->{
                try {
                    countDownLatch.await();
                    CuratorApiLockDemo demo = new CuratorApiLockDemo();
                    demo.lock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
            countDownLatch.countDown();
        }
        System.in.read();
    }
}
