package org.study.jim.zookeeper.base;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.study.jim.zookeeper.AddressConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class NewSessionWithSessionIdAndPwd implements Watcher {
    private static CountDownLatch semaphore = new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(AddressConstant.ZK_ADDR,3000,new NewSessionWithSessionIdAndPwd());
            long sessionId =  zooKeeper.getSessionId();
            byte[]  pwd = zooKeeper.getSessionPasswd();
            semaphore.await();
            //非法id和password
            zooKeeper = new ZooKeeper(AddressConstant.ZK_ADDR,3000,new NewSessionWithSessionIdAndPwd(),1l,"0".getBytes());
            //sessionId和password构造 -> 复用第一次会话，维持第一次会话的有效性
            zooKeeper = new ZooKeeper(AddressConstant.ZK_ADDR,3000,new NewSessionWithSessionIdAndPwd(),sessionId,pwd);
            Thread.sleep(Integer.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
