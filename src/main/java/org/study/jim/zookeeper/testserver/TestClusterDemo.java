package org.study.jim.zookeeper.testserver;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingZooKeeperServer;

/**
 * 测试启动集群环境下的3台服务器
 * 1）、集群leader模拟关闭
 * 2）、集群重新选举之后的状态
 */
public class TestClusterDemo {
    public static void main(String[] args) {
        TestingCluster cluster = new TestingCluster(3);
        try {
            cluster.start();
            Thread.sleep(3000);
            TestingZooKeeperServer leader = null;
            leader =  eachShowServer(cluster);
            leader.kill();
            System.out.println("after leader kill");
            leader = eachShowServer(cluster);
            cluster.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static TestingZooKeeperServer eachShowServer(TestingCluster cluster){
        TestingZooKeeperServer leader = null;
        for(TestingZooKeeperServer zs:cluster.getServers()){
            System.out.println(zs.getInstanceSpec().getServerId()+"-");
            System.out.println(zs.getQuorumPeer().getServerState()+"-");
            System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
            if(zs.getQuorumPeer().getServerState().equals("leading")){
                leader = zs;
            }
        }
        return leader;
    }
}
