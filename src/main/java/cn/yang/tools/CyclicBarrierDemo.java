package cn.yang.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Slf4j
public class CyclicBarrierDemo {


    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(4, () -> log.debug("循环任务执行"));


        for (int i = 0; i < 12; i++) {
            new Thread(() -> {
                try {
                    log.debug("{}线程执行", Thread.currentThread().getName());
                    Thread.sleep(1000L);
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
