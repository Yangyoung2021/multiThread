package cn.yang.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j(topic = "CustomPool")
public class CustomPool {
    //核心线程数
    private final int corePoolSize;
    //最大线程数
    private final int maxPoolSize;
    //空闲线程等待时间
    private final long timeout;
    //空闲线程等待时间单位
    private final TimeUnit timeUnit;
    //处理任务的线程集合
    private final Stack<Work> workers = new Stack<>();
    //阻塞队列
    private final BlockingQueue<Runnable> taskQueue;

    /**
     * 自定义的线程池
     *
     * @param corePoolSize 核心线程数
     * @param maxPoolSize  最大线程数
     * @param timeout      等待时间
     * @param timeUnit     等待时间单元
     * @param taskQueue    任务队列
     */
    public CustomPool(int corePoolSize, int maxPoolSize, long timeout, TimeUnit timeUnit, BlockingQueue<Runnable> taskQueue) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = taskQueue;
    }

    /**
     * 默认实现表示最大线程数等于核心线程数
     *
     * @param corePoolSize 核心线程数
     */
    public CustomPool(int corePoolSize) {
        this(corePoolSize, corePoolSize, 5, TimeUnit.SECONDS);
    }

    /**
     * 自定义的线程池
     *
     * @param corePoolSize 核心线程数
     * @param maxPoolSize  最大线程数
     * @param timeout      等待时间
     * @param timeUnit     等待时间单元
     */
    public CustomPool(int corePoolSize, int maxPoolSize, long timeout, TimeUnit timeUnit) {
        this(corePoolSize, maxPoolSize, timeout, timeUnit, new ArrayBlockingQueue<>(3));

    }

    /**
     * 任务执行方法
     *
     * @param task 要执行的任务
     */
    public void execute(Runnable task) {
//        synchronized (workers) {
//        log.debug("{}任务请求执行", task);
        //判断当前的核心线程数是否全部启用
        if (workers.size() < corePoolSize) {
            //创建新的线程处理当前任务
            Work work = new Work(task);
            log.debug("创建核心线程{}处理任务{}", work, task);
            workers.add(work);
            //启动线程
            new Thread(work).start();
        } else if (workers.size() <= maxPoolSize) {
            //全部核心线程都启用中，尝试将任务放入阻塞队列中
            try {
                //让当前线程进入阻塞队列
                boolean enqueue;
                synchronized (this) {
//                    enqueue = taskQueue.offer(task, 1, TimeUnit.SECONDS);
                    enqueue = taskQueue.offer(task, 50, TimeUnit.MILLISECONDS);
                }
                if (enqueue) {
                    //入队成功
                    log.debug("阻塞队列中加入任务对象{}", task);
                } else {
                    //判断是否还能添加线程处理当前任务
                    int extraAddableThread = maxPoolSize - workers.size();
                    if (extraAddableThread > 0) {
                        //可以添加线程处理任务
//                        for (int i = 0; i < extraAddableThread; i++) {
                        Runnable r = taskQueue.poll();
                        taskQueue.put(task);
                        if (r != null) {
                            //将阻塞队列中的任务对象获取并运行
                            Work externalWorker = new Work(r);
                            log.debug("加入新创建的线程{}帮助清理任务=========", externalWorker);
                            //将新创建的工作对象存入工作队列中
                            workers.add(externalWorker);
                            //启动线程
                            new Thread(externalWorker).start();
                        }
//                        else break;
//                        }
                    } else {
                        //不能添加线程处理任务
                        abortTask(task);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //抛弃策略代码编写
            abortTask(task);
        }
//        }
    }

    private void abortTask(Runnable task) {
        log.debug("{}被抛弃", task);
    }

    class Work implements Runnable {

        private Runnable task;

        public Work(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            //如果task对象不为空
            while (true) {
                while (task != null) {
                    goAhead();
                }
                //最开始的任务对象为执行完成
                try {
                    //从阻塞队列中获取任务
                    synchronized (this) {
                        task = taskQueue.poll(timeout, timeUnit);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //获取到了阻塞队列中的任务对象
                if (task != null) {
                    //enqueue成功
                    goAhead();
                } else {
                    synchronized (CustomPool.class) {
                        //task为null表示阻塞队列中没有任务
                        log.debug("工作线程数{}， 核心线程数{}", workers.size(), corePoolSize);
//                    System.out.println("当前工作线程数：" + workers.size() + "核心线程数：" + corePoolSize);
                        //入队失败，判断当前线程数是否大于核心线程数，如果是就释放worker.size-corePoolSize数量的线程
                        if (workers.size() > corePoolSize) {
                            //释放maxPoolSize-corePoolSize数量的线程
                            for (int i = 0; i < workers.size() - corePoolSize; i++) {
                                Work pop = workers.pop();
                                log.debug("------移除后添加的工作线程{}", pop);
                            }
                        }
                    }
                    //线程执行结束
                    break;
                }
            }
        }

        private synchronized void goAhead() {
            log.debug("----{}开始执行任务{}", this, this.task);
            task.run();
            task = null;
            log.debug("当前阻塞队列中含有任务对象的个数为{}", taskQueue.size());
        }
    }

}

class TestPool {
    private static final AtomicInteger target = new AtomicInteger(0);

    public static void main(String[] args) {
        CustomPool threadPool = new CustomPool(3, 5, 1, TimeUnit.SECONDS);
        TestPool testPool = new TestPool();
        for (int i = 0; i < 20000; i++) {
            create(threadPool, testPool);
        }

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(testPool.get());
    }

    public void incrementTen() {
        for (int i = 0; i < 10; i++) {
            target.incrementAndGet();
        }
    }

    public int get() {
        return target.get();
    }

    private static void create(CustomPool pool, TestPool testPool) {
        pool.execute(testPool::incrementTen);
    }
}
