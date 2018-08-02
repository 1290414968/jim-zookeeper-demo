package org.study.jim.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.study.jim.zookeeper.AddressConstant;

public class ApiSyncDemo {
    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework curatorFramework =  CuratorFrameworkFactory.newClient(AddressConstant.ZK_ADDR,3000,3000,retryPolicy);
        curatorFramework.start();
        try {
            String path = "/curator_demo";
            curatorFramework.create()
                    .creatingParentsIfNeeded()//父子节点递归创建
                    .withMode(CreateMode.PERSISTENT)//节点类型
                    .forPath(path,"0".getBytes());
            Stat stat = new Stat();
            curatorFramework.getData().storingStatIn(stat).forPath(path);
            System.err.println(stat);
            curatorFramework.setData().withVersion(stat.getVersion()).forPath(path,"2".getBytes());
            curatorFramework.getData().storingStatIn(stat).forPath(path);
            System.err.println(stat);
            //删除节点
            curatorFramework.delete().deletingChildrenIfNeeded().forPath(path);
            System.err.println("execute complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
