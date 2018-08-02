package org.study.jim.zookeeper.curator;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.study.jim.zookeeper.AddressConstant;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
/**
 * 分布式锁：
 * 并发环境下数据的一致性，
 * InterProcessMutex的acquire()、release()
 */
public class LockDemo {
    public static void main(String[] args) {
        String path = "/curator_lock_demo";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,3000);
        CuratorFramework client =  CuratorFrameworkFactory.newClient(AddressConstant.ZK_ADDR,3000,3000,retryPolicy);
        client.start();
        final InterProcessMutex mutex = new InterProcessMutex(client,path);
        final CountDownLatch latch = new CountDownLatch(1);
        for(int i = 0;i<30;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                        mutex.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = simpleDateFormat.format(new Date());
                    System.out.println(orderNo);
                    try {
                        mutex.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        latch.countDown();
    }
}
