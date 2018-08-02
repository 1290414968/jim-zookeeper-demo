package org.study.jim.zookeeper.base;

import org.apache.zookeeper.*;
import org.study.jim.zookeeper.AddressConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class CreateNode implements Watcher {
    private static CountDownLatch semaphore = new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(AddressConstant.ZK_ADDR,3000,new CreateNode());
            semaphore.await();
            //同步创建
            String  syncPath =  zooKeeper.create("/jim-sync","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            System.err.println(syncPath);
            syncPath =  zooKeeper.create("/jim-sync-temp","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
            System.err.println(syncPath);
            //异步创建
            zooKeeper.create("/jim-async","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT,new MyStringCallback(),"this is context");
            Thread.sleep(Integer.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("receive event"+watchedEvent);
        if(Event.KeeperState.SyncConnected == watchedEvent.getState()){
            semaphore.countDown();
        }
    }
    static class MyStringCallback implements AsyncCallback.StringCallback{
        @Override
        public void processResult(int resultCode, String path, Object ctx, String rpath) {
            System.out.println("响应码："+resultCode);
            System.out.println("接口参数传入的节点路径："+path);
            System.out.println("接口参数传入的context"+ctx);
            System.out.println("服务端完整节点名称"+rpath);
        }
    }
}
