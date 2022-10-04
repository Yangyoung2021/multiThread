package cn.yang.pcm;


import lombok.extern.slf4j.Slf4j;

/**
 * 使用Synchronized中的wait()方法和notify()方法创建一个生产消费者模式
 */
@Slf4j
public class SynchronizedMonitor {

    public static void main(String[] args) {
        SuperMarket market = new SuperMarket();

        Worker worker = new Worker(market);
        Consumer consumer = new Consumer(market);

        for (int i = 0; i < 10; i++) {
            Thread t1 = new Thread(worker::product, "w" + i);
            t1.start();
        }

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10; i++) {
            Thread t1 = new Thread(consumer::consume, "c" + i);
            t1.start();
        }
    }


    static class SuperMarket{
        private int goods;

        public void productOne() {
            synchronized (this) {
                while (this.goods >= 5) {
                    try {
//                        log.debug("商品有剩余，等待消费");
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug(Thread.currentThread().getName() + "生产了商品");
                this.goods++;
//                try {
//                    Thread.sleep(100L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                notifyAll();
            }
        }

        public void consumeOne() {
            synchronized (this) {
                while (this.goods == 0) {
                    try {
//                        log.debug("商品余额不足，等待生产");
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug(Thread.currentThread().getName() + "消费了商品");
                this.goods--;
//                try {
//                    Thread.sleep(100L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                notifyAll();
            }
        }
    }


}


class Consumer{
    private final SynchronizedMonitor.SuperMarket market;

    public Consumer(SynchronizedMonitor.SuperMarket market) {
        this.market = market;
    }

    public void consume() {
        market.consumeOne();
    }
}

class Worker{
    private final SynchronizedMonitor.SuperMarket market;

    public Worker(SynchronizedMonitor.SuperMarket market) {
        this.market = market;
    }

    public void product() {
        market.productOne();
    }
}


