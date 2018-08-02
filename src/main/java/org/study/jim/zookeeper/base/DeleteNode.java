package org.study.jim.zookeeper.base;

import org.apache.zookeeper.*;
import org.study.jim.zookeeper.AddressConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class DeleteNode implements Watcher {
    private static CountDownLatch semaphore = new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(AddressConstant.ZK_ADDR,3000,new DeleteNode());
            semaphore.await();
            zooKeeper.delete("/jim-async",0);
            zooKeeper.delete("/jim-temp", 0, new AsyncCallback.VoidCallback() {
                @Override
                public void processResult(int resultCode, String path, Object ctx) {
                    System.out.println("响应码："+resultCode);
                    System.out.println("接口参数传入的节点路径："+path);
                    System.out.println("接口参数传入的context"+ctx);
                }
            },"this is content");
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
}
