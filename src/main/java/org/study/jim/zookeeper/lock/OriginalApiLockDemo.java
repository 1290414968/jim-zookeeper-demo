package org.study.jim.zookeeper.lock;

import com.sun.org.apache.bcel.internal.generic.FADD;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class OriginalApiLockDemo implements Watcher,Lock {
    /**
     * 分布式锁的创建逻辑：
     * 1）、构造方法去连接zookeeper服务器，并判断持久化根节点是否存在，不存在的话则创建
     * 2）、当有连接请求到zookeeper服务器时，则在该根节点下创建子临时节点
     * 3）、取出根节点下所有子节点，并取出最小的节点和请求的节点进行比较，如果一致，那么获取锁成功，
     * 否则，设置子节点的last节点为等待节点
     * 4）、当节点获取锁失败，则添加监听事件，监听上一节点是否释放锁，如果是否，则尝试获取锁
     *
     */
    private ZooKeeper zooKeeper;
    private String ROOT_LOCK = "/jim-locks";
    private String WAIT_LOCK;
    private  String CURRENT_LOCK;

    private CountDownLatch countDownLatch;
    public OriginalApiLockDemo(){
        try {
            zooKeeper = new ZooKeeper("47.52.240.168",3000,this);
            Stat stat =  zooKeeper.exists(ROOT_LOCK,false);
            if(stat==null){
                zooKeeper.create(ROOT_LOCK,"0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    public boolean tryLock() {
        try {
            CURRENT_LOCK = zooKeeper.create(ROOT_LOCK+"/","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+",尝试竞争锁");
            List<String> childrenList =  zooKeeper.getChildren(ROOT_LOCK,false);//获取根目录下所有的子节点，这里使用zookeeper的数据一致性进行同步
            SortedSet<String> sortedSet = new TreeSet<String>();
            for(String child:childrenList){
                sortedSet.add(ROOT_LOCK+"/"+child);
            }
            System.out.println(Thread.currentThread().getName()+"->"+sortedSet);
            String firstNode = sortedSet.first();
            SortedSet<String> lessThenMe = ((TreeSet<String>)sortedSet).headSet(CURRENT_LOCK);//排序过后，获取到小于当前节点的所有节点
            System.out.println(Thread.currentThread().getName()+"->"+lessThenMe);
            if(CURRENT_LOCK.equals(firstNode)){return true;}
            if(!lessThenMe.isEmpty()){
                WAIT_LOCK = lessThenMe.last();//获取当前节点的前一个节点
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void lock() {
        if(this.tryLock()){
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+",获取锁成功");
            return ;
        }
        waitForLock(WAIT_LOCK);
    }

    /**
     * 锁获取失败时，
     * 1）、使用exists方法给当前节点的上一节点设置监听为true，由于构造使用了当前对象作为监听器，那么this对象作为监听器对象
     * 2）、使用CountDownLatch.await()，阻塞当前线程等待
     * @param prev
     * @return
     */
    private boolean waitForLock(String prev){
        try {
            Stat stat =  zooKeeper.exists(prev,true);
            if(stat!=null){
                System.out.println(Thread.currentThread().getName()+"->等待锁"+prev+"释放");
                countDownLatch=new CountDownLatch(1);
                countDownLatch.await();
                System.out.println(Thread.currentThread().getName()+"->获得锁成功");
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
    public void lockInterruptibly() throws InterruptedException {
    }
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        System.out.println(Thread.currentThread().getName()+"->释放锁"+CURRENT_LOCK);
        try {
            zooKeeper.delete(CURRENT_LOCK,-1);
            CURRENT_LOCK=null;
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public Condition newCondition() {
        return null;
    }

    /**
     * 当服务器节点变化时，则会触发该方法，
     * 然后调用CountDownLatch.countDown()方法唤醒阻塞线程
     * @param watchedEvent
     */
    public void process(WatchedEvent watchedEvent) {
        if(this.countDownLatch!=null){
            this.countDownLatch.countDown();
        }
    }
}
