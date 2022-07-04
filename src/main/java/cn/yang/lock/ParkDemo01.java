package cn.yang.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class ParkDemo01 {
    private static volatile int state;

    public static void main(String[] args) {

        ReentrantLock lock = new ReentrantLock();

        Condition c1 = lock.newCondition();
        Condition c2 = lock.newCondition();
        Condition c3 = lock.newCondition();

        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    lock.lock();
                    while (state != 0) {
                        try {
                            c1.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    state = 1;
                    printA();
                    c2.signal();
                } finally {
                    lock.unlock();
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    lock.lock();
                    while (state != 1) {
                        try {
                            c2.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    state = 2;
                    printB();
                    c3.signal();
                } finally {
                    lock.unlock();
                }
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    lock.lock();
                    while (state != 2) {
                        try {
                            c3.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    state = 0;
                    printC();
                    c1.signal();
                } finally {
                    lock.unlock();
                }
            }
        }, "C").start();

    }

    public static void printA() {
        for (int i = 0; i < 1; i++) {
            log.debug("A");
        }
    }

    public static void printB() {
        for (int i = 0; i < 2; i++) {
            log.debug("B");
        }
    }

    public static void printC() {
        for (int i = 0; i < 3; i++) {
            log.debug("C");
        }
    }


}
