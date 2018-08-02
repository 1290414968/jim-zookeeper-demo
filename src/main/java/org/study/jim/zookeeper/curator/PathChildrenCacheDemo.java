package org.study.jim.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.study.jim.zookeeper.AddressConstant;

/**
 * 监听节点的子节点的变化
 */
public class PathChildrenCacheDemo {
    public static void main(String[] args) {
        String path = "/curator_path_cache";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework curatorFramework =  CuratorFrameworkFactory.newClient(AddressConstant.ZK_ADDR,3000,3000,retryPolicy);
        curatorFramework.start();
        try {
            //创建节点
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,"12".getBytes());
            final PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework,path,false);
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()){
                        case CHILD_ADDED:
                            System.out.println("Child add "+event.getData().getPath());
                            break;
                        case CHILD_UPDATED:
                            System.out.println("Child update "+event.getData().getPath());
                            break;
                        case CHILD_REMOVED:
                            System.out.println("Child remove "+event.getData().getPath());
                            break;
                        default:
                            break;
                    }
                }
            });
            curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(path+"/t1");
            Thread.sleep(1000);
            curatorFramework.delete().forPath(path+"/t1");
            Thread.sleep(1000);
            curatorFramework.delete().forPath(path);
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
