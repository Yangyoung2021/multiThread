package cn.yang.pool;


import com.sun.javafx.font.PrismFontFactory;
import sun.misc.Unsafe;
import sun.nio.ch.ThreadPool;
import sun.text.resources.cldr.aa.FormatData_aa;

import java.text.SimpleDateFormat;
import java.util.concurrent.*;

/**
 * 测试阻塞队列的三种API
 *      add/remove方法：添加或者移除失败直接报异常
 *      offer/poll方法：添加或者移除返回特定值false/null，另外还能提供等待时间
 *      put/take方法：
 */
public class BlockingQueueDemo {

    public static void main(String[] args) {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
        queue.add("one");
        queue.add("two");
        queue.add("three");

        System.out.println(FormatData_aa.class.getClassLoader());
        System.out.println(PrismFontFactory.class.getClassLoader());
        System.out.println(String.class.getClassLoader());

        System.out.println(SimpleDateFormat.class.getClassLoader());


        ExecutorService executorService = Executors.newFixedThreadPool(3);
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        ExecutorService executorService2 = Executors.newCachedThreadPool();
        Unsafe unsafe = null;
        try {
            unsafe = Unsafe.getUnsafe();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(unsafe);

        executorService.submit(() -> {
            System.out.println("这是执行的第1个任务");
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            System.out.println("这是执行的第2个任务");
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            System.out.println("这是执行的第3个任务");
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            System.out.println("这是执行的第4个任务");
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
        System.out.println("关闭线程池");

//        testAdd(queue);
        System.out.println("queue.size() = " + queue.size());

        queue.remove();
        queue.remove();
        queue.remove();
        System.out.println("queue.size() = " + queue.size());

//        testRemove(queue);
    }

    public static void testAdd(BlockingQueue<String> queue) {
        try {
            queue.offer("four", 2, TimeUnit.SECONDS);
            queue.put("four");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testRemove(BlockingQueue<String> queue) {
        System.out.println("执行移除方法");
        try {
            queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}
