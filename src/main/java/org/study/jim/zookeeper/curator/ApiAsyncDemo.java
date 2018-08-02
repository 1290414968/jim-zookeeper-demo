package org.study.jim.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.study.jim.zookeeper.AddressConstant;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiAsyncDemo {
    public static void main(String[] args) {
        String path = "/curator_async";
        String path2 = "/curator_async2";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,3000);
        CuratorFramework client = CuratorFrameworkFactory.newClient(AddressConstant.ZK_ADDR,2000,3000,retryPolicy);
        client.start();
        CountDownLatch downLatch = new CountDownLatch(2);
        ExecutorService executorService =  Executors.newFixedThreadPool(2);

        try {
            //异步创建使用主线程
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .inBackground(new BackgroundCallback() {
                        @Override
                        public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                            System.out.println("event code="+curatorEvent.getResultCode()+",type="+curatorEvent.getType());
                            System.out.println("Thread Name="+Thread.currentThread().getName());
                            downLatch.countDown();
                        }
                    })
                    .forPath(path);
            //异步创建使用线程池的某个线程
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .inBackground(new BackgroundCallback() {
                        @Override
                        public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                            System.out.println("event code="+curatorEvent.getResultCode()+",type="+curatorEvent.getType());
                            System.out.println("Thread Name="+Thread.currentThread().getName());
                            downLatch.countDown();
                        }
                    },executorService)
                    .forPath(path2);
            downLatch.await();
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
