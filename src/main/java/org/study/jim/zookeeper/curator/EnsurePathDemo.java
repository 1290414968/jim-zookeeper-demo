package org.study.jim.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.EnsurePath;

/**
 * EnsurePath工具类：已废弃
 * 封装了创建之前判断节点是否存在，先判断是否存在，不存在则创建，否则会报错的异常的处理
 * 替代方式：
 * CuratorFramework.create().creatingParentContainersIfNeeded() or CuratorFramework.exists().creatingParentContainersIfNeeded()
 */
public class EnsurePathDemo {
    public static void main(String[] args) {
        String path = "/ensure_path/c1";
        CuratorFramework client =  ClientFrameUtil.getClient();
        client.usingNamespace("ensure");

        EnsurePath ensurePath = new EnsurePath(path);
        try {
            ensurePath.ensure(client.getZookeeperClient());
            ensurePath.ensure(client.getZookeeperClient());

            EnsurePath path2 = client.newNamespaceAwareEnsurePath("/c1");
            path2.ensure(client.getZookeeperClient());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
