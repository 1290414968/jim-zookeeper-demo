package org.study.jim.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.study.jim.zookeeper.AddressConstant;

/**
 * master选举的封装API调用示例
 * 思路：
 * 在/master_select节点下创建/lock子节点，zookeeper的节点特性
 * 只能有一个机器的请求创建成功，那么该机器则为master
 *
 * API:LeaderSelector
 */
public class MasterSelectDemo {
    public static void main(String[] args) {
        String path = "/master_select";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,3000);
        CuratorFramework client =  CuratorFrameworkFactory.newClient(AddressConstant.ZK_ADDR,3000,3000,retryPolicy);
        client.start();

        LeaderSelector leaderSelector = new LeaderSelector(client, path, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println("成为master角色");
                Thread.sleep(3000);
                System.out.println("释放master操作");
            }
        });
        leaderSelector.autoRequeue();
        leaderSelector.start();
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
