package cn.yang.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;


@Slf4j
public class SynchronousQueueDemo {

    public static void main(String[] args) {
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();


        new Thread(() -> {
            log.debug("开始放入数据");
            boolean offerResult = false;
            try {
                offerResult = synchronousQueue.offer("one", 1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("放入数据{}", offerResult);

        }).start();

        new Thread(() -> {
            log.debug("开始取数据");
            int a = 1 / 0;
            String result = null;
            try {
                result = synchronousQueue.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("获取数据{}", result);
        }).start();
    }
}
