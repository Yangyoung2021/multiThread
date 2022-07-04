package cn.yang.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TestDemo01 {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try {
                    lock.lock();
                    log.debug("执行任务。。。");
                    try {
                        TimeUnit.SECONDS.sleep(10000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    lock.unlock();
                }
            }).start();
        }


    }
}
