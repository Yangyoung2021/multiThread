package cn.yang.pcm;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AtomicCAS {

    public static void main(String[] args) {
        Factory factory = new Factory();

        Plus plus = new Plus(factory);
        Sub sub = new Sub(factory);

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(plus::plus, "p" + i);
            thread.start();
        }

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(sub::sub, "s" + i);
            thread.start();
        }

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        log.debug("最终值为：" + factory.getValue());
    }

    static class Factory{
        private final AtomicInteger value = new AtomicInteger(0);

        public void increment() {
            synchronized (this) {
                value.getAndIncrement();
                log.debug(Thread.currentThread().getName() + "生产结果" + value.get());
            }
        }

        public void decrement() {
            synchronized (this) {
                value.getAndDecrement();
                log.debug(Thread.currentThread().getName() + "----------消费结果" + value.get());
            }

        }

        public int getValue() {
            return value.get();
        }
    }
}

class Plus {
    private final AtomicCAS.Factory factory;

    public Plus(AtomicCAS.Factory factory) {
        this.factory = factory;
    }

    public void plus() {
        factory.increment();
    }
}

class Sub {
    private final AtomicCAS.Factory factory;

    public Sub(AtomicCAS.Factory factory) {
        this.factory = factory;
    }

    public void sub() {
        factory.decrement();
    }
}
