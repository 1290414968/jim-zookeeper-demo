package org.study.jim.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;

import java.util.concurrent.*;

/**
 * 分布式拦截同步
 * API：DistributedBarrier
 * setBarrier
 * waitOnBarrier
 * removeBarrier
 */
public class CyclicBarrierDemo {
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
    private static ExecutorService service = Executors.newFixedThreadPool(3);
    private static void javaCyclic(){
        service.submit(new MyRunnable());
        service.submit(new MyRunnable());
        service.submit(new MyRunnable());
        service.shutdown();
    }
    static class MyRunnable implements  Runnable{
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+"->准备好了");
            try {
                cyclicBarrier.await();
                System.out.println(Thread.currentThread().getName()+"->起跑");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        //javaCyclic();
        distributeCyclic();
    }
    static DistributedBarrier distributedBarrier;
    private static void distributeCyclic(){
        String path = "/distribute_cyclic";
        for(int i = 0 ;i<5;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CuratorFramework client =  ClientFrameUtil.getClient();
                    distributedBarrier  = new DistributedBarrier(client,path);
                    System.out.println(Thread.currentThread().getName()+"线程设置");
                    try {
                        distributedBarrier.setBarrier();
                        distributedBarrier.waitOnBarrier();
                        System.out.println("启动....");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        try {
            Thread.sleep(5000);
            distributedBarrier.removeBarrier();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
