package org.study.jim.zookeeper.base;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.study.jim.zookeeper.AddressConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class UpdateNode implements Watcher {
    private static CountDownLatch semaphore = new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(AddressConstant.ZK_ADDR,3000,new UpdateNode());
            semaphore.await();
            String path = "/jim-sync-update";
            //创建
            String  syncPath =  zooKeeper.create(path,"{username:1}".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            System.err.println(syncPath);
            //更新
            zooKeeper.getData(path,true,null);
            // -1 参数，zookeeper会根据该值进行当前节点最新版本基础上的更新
            Stat stat  =  zooKeeper.setData(path,"{username:2}".getBytes(),-1);
            System.err.println(stat.getMzxid()+"|"+stat.getVersion());
            // 返回当前节点的最新版本号，然后将版本作为参数进行更新
            Stat stat2 = zooKeeper.setData(path,"{username:3}".getBytes(),stat.getVersion());
            // stat版本已过期，更新报错
//            zooKeeper.setData(path,"2".getBytes(),stat.getVersion());
            System.err.println(stat2.getMzxid()+"|"+stat2.getVersion());
            zooKeeper.delete(path,-1);
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
