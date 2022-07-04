package cn.yang.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;


@Slf4j
public class CountdownLatchDemo {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    log.debug("{}线程执行", Thread.currentThread().getName());
                    Thread.sleep(1000L);
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            latch.await();
            log.debug("主线程执行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
