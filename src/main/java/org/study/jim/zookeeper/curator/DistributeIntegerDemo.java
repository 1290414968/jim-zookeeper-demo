package org.study.jim.zookeeper.curator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
/**
 * 分布式原子计数器：
 * DistributedAtomicInteger 的原子计算
 */
public class DistributeIntegerDemo {
    public static void main(String[] args) {
        String path = "/dis_integer";
        CuratorFramework client =  ClientFrameUtil.getClient();
        DistributedAtomicInteger num = new DistributedAtomicInteger(client,path,new ExponentialBackoffRetry(3000,3000));
        try {
            AtomicValue<Integer> atomicValue = num.add(100);
            System.out.println(atomicValue.succeeded());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
