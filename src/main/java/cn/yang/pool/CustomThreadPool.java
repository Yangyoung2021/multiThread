package cn.yang.pool;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j(topic = "ThreadPool")
public class CustomThreadPool {

    //核心线程数
    private final int corePoolSize;
    //最大线程数
    private final int maxPoolSize;
    //额外线程最大等待时间
    private final long timeout;
    //额外线程最大等待时间单位
    private final TimeUnit timeUnit;
    //工作线程
    private final Stack<Worker> workThreads;
    //阻塞队列
    private final BlockingQueue<Runnable> taskQueue;
    //额外线程
    private final AtomicInteger extraThreads;
    //最大额外线程
    private final int maxExtraThreads;

    /**
     * 全参构造
     *
     * @param corePoolSize 核心线程数
     * @param maxPoolSize  最大线程数
     * @param timeout      额外线程等待最大时间
     * @param timeUnit     额外线程等待时间单位
     * @param workThreads  工作的线程
     * @param taskQueue    任务队列
     */
    public CustomThreadPool(int corePoolSize,
                            int maxPoolSize,
                            long timeout,
                            TimeUnit timeUnit,
                            Stack<Worker> workThreads,
                            BlockingQueue<Runnable> taskQueue) {

        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.workThreads = workThreads;
        this.taskQueue = taskQueue;
        this.extraThreads = new AtomicInteger(maxPoolSize - corePoolSize);
        this.maxExtraThreads = getExtraThreads();
    }

    /**
     * @param corePoolSize 核心线程数
     * @param maxPoolSize  最大线程数
     * @param timeout      额外线程等待最大时间
     * @param timeUnit     额外线程等待时间单位
     */
    public CustomThreadPool(int corePoolSize, int maxPoolSize, long timeout, TimeUnit timeUnit) {
        this(corePoolSize, maxPoolSize, timeout, timeUnit, new Stack<>(), new ArrayBlockingQueue<>(3));
    }


    /**
     * @param corePoolSize 核心线程数
     */
    public CustomThreadPool(int corePoolSize) {
        this(corePoolSize, corePoolSize + 2, 1, TimeUnit.SECONDS);
    }

    /**
     * 启动线程
     *
     * @param task 要执行的任务对象
     */
    public void execute(Runnable task) {
        //当工作线程数小于核心线程数时直接创建新的线程用来处理当前任务
        if (workThreads.size() < corePoolSize) {
            addWorkThread(task);
        } else if (workThreads.size() <= maxPoolSize) {
            try {
                //尝试将当前任务对象添加到任务队列中
                boolean enqueue = taskQueue.offer(task, timeout, timeUnit);
                if (enqueue) {
                    //入队成功，打印日志
                    log.debug("任务队列中添加任务对象{}", task);
                } else {
                    //入队失败，判断当前是否能创建额外的线程用来执行任务
                    if (getExtraThreads() > 0) {
                        //额外可用线程减一
                        extraThreads.decrementAndGet();
                        //创建新的线程执行任务
                        addWorkThread(task);
                        log.debug("添加额外线程成功，当前执行任务的线程有{}个", workThreads.size());
                    } else {
                        //没有可用的额外线程用于执行任务
                        abortTask(task);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //当前工作线程数大于最大线程数，直接将当前任务抛弃
            abortTask(task);
        }

    }

    /**
     * 抛弃任务方法
     *
     * @param task 要抛弃的任务对象
     */
    private void abortTask(Runnable task) {
        log.debug("{}任务被抛弃", task);
    }

    /**
     * 创建新的工作线程处理任务
     *
     * @param task 要处理的任务对象
     */
    private void addWorkThread(Runnable task) {
        //创建工作线程对象
        Worker worker = new Worker(task);
        //将工作线程对象添加到工作线程队列中
        workThreads.add(worker);
        //启动工作线程
        worker.getThread().start();
    }


    /**
     * 释放额外的线程
     */
    private void releaseExtraThreads() {
        Worker pop = workThreads.pop();
        extraThreads.incrementAndGet();
        log.debug("释放额外的线程{}当前工作队列数量{}------", pop.getThread(), workThreads.size());
    }

    private synchronized int getExtraThreads() {
        return extraThreads.get();
    }

    class Worker implements Runnable {

        //当前Worker执行的任务对象
        private Runnable task;
        //当前Worker属于的线程对象
        private final Thread thread;

        public Worker(Runnable task) {
            this.task = task;
            this.thread = new Thread(this);
        }

        /**
         * 获取当前工作对象所属的线程
         *
         * @return thread
         */
        public Thread getThread() {
            return this.thread;
        }

        @SneakyThrows
        @Override
        public void run() {
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {
                handleTask(task);
                task = null;
            }
            synchronized (CustomThreadPool.class) {
                //从任务队列获取到的任务对象为空，表明当前没有任务需要执行，释放创建的额外的工作线程
                for (int i = 0; i < maxExtraThreads - getExtraThreads(); i++) {
                    releaseExtraThreads();
                }
            }
        }

        /**
         * 运行任务方法
         *
         * @param task 要运行的任务对象
         */
        private void handleTask(Runnable task) {
            log.debug("{}线程执行任务{}", this.getThread(), task);
            task.run();
        }
    }

}

class TestPool1 {
    private static final AtomicInteger target = new AtomicInteger(0);

    public static void main(String[] args) {
        CustomThreadPool threadPool = new CustomThreadPool(3, 5, 2, TimeUnit.MILLISECONDS);
        TestPool1 testPool1 = new TestPool1();
        for (int i = 0; i < 200; i++) {
            create(threadPool, testPool1);
        }

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(testPool1.get());
    }

    public void incrementTen() {
        for (int i = 0; i < 10; i++) {
            target.incrementAndGet();
        }
    }

    public int get() {
        return target.get();
    }

    private static void create(CustomThreadPool pool, TestPool1 testPool1) {
//        pool.execute(testPool1::incrementTen);
        pool.execute(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(2L);
                testPool1.incrementTen();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}



