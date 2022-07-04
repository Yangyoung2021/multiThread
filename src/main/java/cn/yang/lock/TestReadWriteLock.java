package cn.yang.lock;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 由于读锁会唤醒后面的共享锁直到下一个是独占锁为止，测试独占锁之后放一个共享锁是否能被唤醒
 */
@Slf4j
public class TestReadWriteLock {

    public static void main(String[] args) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try {
                    readLock.lock();
                    log.debug("读线程{}执行", Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    readLock.unlock();
                }
            }, "r" + i).start();
        }

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try {
                    writeLock.lock();
                    log.debug("写线程{}执行", Thread.currentThread().getName());
                } finally {
                    writeLock.unlock();
                }
            }, "w" + i).start();
        }

        for (int i = 2; i < 4; i++) {
            new Thread(() -> {
                try {
                    readLock.lock();
                    log.debug("读线程{}执行", Thread.currentThread().getName());
                } finally {
                    readLock.unlock();
                }
            }, "r" + i).start();
        }
    }
}
