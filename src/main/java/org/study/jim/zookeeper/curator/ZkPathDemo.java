package org.study.jim.zookeeper.curator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.ZooKeeper;
/**
 * ZKPath工具类的部分API的示例
 */
public class ZkPathDemo {
    public static void main(String[] args) {
        String path = "/curator_zkpath_sample";
        CuratorFramework client =  ClientFrameUtil.getClient();
        try {
            /**
             *返回namespace+path的节点路径
             */
            System.out.println(ZKPaths.fixForNamespace(path,"/sub"));
            /**
             *返回namespace+path的节点路径
             */
            System.out.println(ZKPaths.makePath(path,"/sub"));
            String subPath = "/curator_zk_sample/sub1";
            /**
             * 返回一个路径下的子节点路径->sub1
             */
            System.out.println(ZKPaths.getNodeFromPath(subPath));
            /**
             * 将一个路径拆分成namespace 和path ，可以分别获取name和path
             */
            ZKPaths.PathAndNode pn =  ZKPaths.getPathAndNode(subPath);
            System.out.println(pn.getPath());
            System.out.println(pn.getNode());
            String dir1 = path+"/child1";
            String dir2 = path+"/child2";
            //在path节点下创建两个顺序子节点，会在服务器上创建
            ZooKeeper zooKeeper = client.getZookeeperClient().getZooKeeper();
            ZKPaths.mkdirs(zooKeeper,dir1);
            ZKPaths.mkdirs(zooKeeper,dir2);
            System.out.println(ZKPaths.getSortedChildren(zooKeeper,path));
            //删除path节点和递归删除子节点
            ZKPaths.deleteChildren(client.getZookeeperClient().getZooKeeper(),path,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
