package cn.yang.pcm;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BlockingQueuePCM {

    public static void main(String[] args) {
        BlockingQueueDemo queue = new BlockingQueueDemo(new ArrayBlockingQueue<>(10));

        new Thread(queue::produce, "p").start();

        new Thread(queue::consumer, "c").start();


        try {
            TimeUnit.SECONDS.sleep(5);
            queue.stopWork();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class BlockingQueueDemo {
        private volatile boolean flag;
        private final AtomicInteger atomicInteger;
        private final BlockingQueue<String> queue;

        public BlockingQueueDemo(BlockingQueue<String> queue) {
            this.queue = queue;
            this.flag = true;
            this.atomicInteger = new AtomicInteger(0);
        }

        public void produce() {
            String data;
            boolean result;
            while (flag) {
                data = atomicInteger.incrementAndGet() + "";
                try {
                    result = queue.offer(data, 1L, TimeUnit.SECONDS);
                    if (result) {
                        log.debug(Thread.currentThread().getName() + "\t 生产队列值\t" + data + "成功");
                        TimeUnit.SECONDS.sleep(1L);
                    } else {
                        log.debug(Thread.currentThread().getName() + "\t 生产队列值失败");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("停止生产。。。");
        }

        public void consumer() {
            String result;
            while (flag) {
                try {
                    result = queue.poll(1L, TimeUnit.SECONDS);
                    if (null == result || result.equalsIgnoreCase("")) {
                        //叫停生产线程
                        flag = false;

                        log.debug(Thread.currentThread().getName() + "消费消息超时， 线程结束");
                        return;
                    }
                    log.debug(Thread.currentThread().getName() + "\t 消费消息" + result + "成功");
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("消费结束");
        }

        public void stopWork() {
            log.debug("大老板叫停。。");
            this.flag = false;
        }

    }
}
