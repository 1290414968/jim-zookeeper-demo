package org.study.jim.zookeeper.testserver;
import com.sun.security.ntlm.Client;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.CreateMode;
import org.study.jim.zookeeper.curator.ClientFrameUtil;
import java.io.File;
/**
 * 简单启动一个测试zookeeper服务，进行开发测试
 * 版本异常：
 * 不能为最新版本：4.0.1
 * 必须为：2.4.x 版本
 */
public class TestServerDemo {
    public static void main(String[] args) {
        try {
            //单机测试服务器 TestingServer
            TestingServer testingServer = new TestingServer(2181,new File("D:/tmp"));
            String path = "/test";
            CuratorFramework client = CuratorFrameworkFactory.newClient(testingServer.getConnectString(),3000,3000,new ExponentialBackoffRetry(3000,3000));
            client.start();
            client.create().withMode(CreateMode.PERSISTENT).forPath(path);
            System.out.println(client.getChildren().forPath(path));
//            testingServer.close();

            //集群测试服务器 TestingCluster
            TestingCluster cluster = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
