package cn.yang.lock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JoinDemo01 {


    public static void main(String[] args) {
        log.debug("程序启动");

        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("t1线程执行。。。。");
        }, "t1");

        Thread t2 = new Thread(() -> {
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("t2线程执行。。。。");
        }, "t1");

        Thread t3 = new Thread(() -> {
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("t3线程执行。。。。");
        }, "t1");

        t1.start();
        t2.start();
        t3.start();

        try {
            t3.join();
            log.debug("主线程执行完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
