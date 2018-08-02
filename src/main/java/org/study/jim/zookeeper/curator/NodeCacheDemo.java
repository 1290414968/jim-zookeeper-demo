package org.study.jim.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.study.jim.zookeeper.AddressConstant;
/**
 * NodeCache:监听节点数据变化和节点创建的变化
 */
public class NodeCacheDemo {
    public static void main(String[] args) {
        //事件监听
        String path = "/curator_cache";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework curatorFramework =  CuratorFrameworkFactory.newClient(AddressConstant.ZK_ADDR,3000,3000,retryPolicy);
        curatorFramework.start();
        try {
            //创建节点
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,"12".getBytes());
            //为节点添加NodeCache对象
            final NodeCache nodeCache = new NodeCache(curatorFramework,path,false);
            nodeCache.start(true);
            //添加节点监听器
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    System.out.println("current node data = > "+new String(nodeCache.getCurrentData().getData()));
                }
            });
            curatorFramework.setData().forPath(path,"u".getBytes());
            Thread.sleep(1000);
            curatorFramework.delete().deletingChildrenIfNeeded().forPath(path);
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //监听节点

    }
}
