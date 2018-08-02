package org.study.jim.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.study.jim.zookeeper.AddressConstant;

public class ClientFrameUtil {
    public static CuratorFramework getClient(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,3000);
        CuratorFramework client = CuratorFrameworkFactory.newClient(AddressConstant.ZK_ADDR,3000,3000,retryPolicy);
        client.start();
        return client;
    }
}
