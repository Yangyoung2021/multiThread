package cn.yang.pcm;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BlockingQueueDemo {

    public static void main(String[] args) {
        MyResource resource = new MyResource(new ArrayBlockingQueue<>(3));

        new Thread(resource::myProd, "Produce").start();
        new Thread(resource::myCons, "consumer").start();

        try {
            TimeUnit.SECONDS.sleep(5L);
            resource.stopWork();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    static class MyResource {
        private volatile boolean FLAG;
        private final AtomicInteger atomicInteger;
        private final BlockingQueue<String> blockingQueue;

        public MyResource(BlockingQueue<String> blockingQueue) {
            this.blockingQueue = blockingQueue;
            //默认开启工作
            this.FLAG = true;
            this.atomicInteger = new AtomicInteger();
        }

        /**
         * 生产方法
         */
        public void myProd() {
            String data;
            while (FLAG) {
                data = atomicInteger.getAndIncrement() + "";
                try {
                    boolean result = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
                    if (result) {
                        //插入成功
                        log.debug(Thread.currentThread().getName() + "\t插入队列值\t" + data + "成功");
                    } else {
                        log.debug(Thread.currentThread().getName() + "\t插入队列值失败");
                    }
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 消费方法
         */
        public void myCons() {
            String result;
            while (FLAG) {
                try {
                    result = blockingQueue.poll(2L, TimeUnit.SECONDS);
                    if (result == null || "".equalsIgnoreCase(result)) {
                        log.debug(Thread.currentThread().getName() + "\t获取到null值，表示需要停止消费动作");
                        //叫停生产线程
                        FLAG = false;
                        return;
                    }
                    log.debug(Thread.currentThread().getName() + "\t从队列中获取到值" + result);
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopWork() {
            log.debug(Thread.currentThread().getName() + "叫停工作。。。");
            this.FLAG = false;
        }
    }
}
