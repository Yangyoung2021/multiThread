package cn.yang.pcm;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class LockCAS {

    public static void main(String[] args) {
        Storage storage = new Storage();

        Workman workman = new Workman(storage);
        People people = new People(storage);

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(workman::produce, "w" + i);
            thread.start();
        }

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(people::consume, "p" + i);
            thread.start();
        }
    }

    static class Storage{
        private int count;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();


        public void getOne() {
            try {
                lock.lock();
                while (this.count == 0) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug(Thread.currentThread().getName() + "消费");
                this.count--;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public void saveOne() {
            try {
                lock.lock();
                while (this.count >= 5) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug(Thread.currentThread().getName() + "进行了生产");
                this.count++;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}

class People{
    private final LockCAS.Storage storage;

    public People(LockCAS.Storage storage) {
        this.storage = storage;
    }

    public void consume() {
        storage.getOne();
    }
}

class Workman{
    private final LockCAS.Storage storage;

    public Workman(LockCAS.Storage storage) {
        this.storage = storage;
    }

    public void produce() {
        storage.saveOne();
    }
}
