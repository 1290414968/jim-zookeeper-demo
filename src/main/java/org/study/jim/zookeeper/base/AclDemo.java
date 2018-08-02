package org.study.jim.zookeeper.base;

import org.apache.zookeeper.*;
import org.study.jim.zookeeper.AddressConstant;

import java.io.IOException;

public class AclDemo implements Watcher {
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(AddressConstant.ZK_ADDR,3000,new AclDemo());
            String path = "/jim-acl";
            /**
             * digest  常用的用户密码权限逻辑控制 -> 用户名:密码
             */
            zooKeeper.addAuthInfo("digest","foo:true".getBytes());
            zooKeeper.create(path,"mm".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.PERSISTENT);


            ZooKeeper zooKeeper1 = new ZooKeeper(AddressConstant.ZK_ADDR,3000,new AclDemo());
            //无权限访问
//            zooKeeper1.getData(path,false,null);
//
//            //权限错误访问
//            zooKeeper1.addAuthInfo("digest","foo:false".getBytes());
//            zooKeeper1.getData(path,false,null);

            //删除权限节点
            zooKeeper1.addAuthInfo("digest","foo:true".getBytes());
            zooKeeper1.delete(path,-1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
