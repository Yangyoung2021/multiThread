package cn.yang.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;


/**
 * 使用缓存的线程池不会创建核心线程，只有额外的线程。当任务来临时才会创建额外线程去处理，数量最大为Integer.MAX_VALUE - 1
 * 采用的是同步队列，SynchronousQueue只有当来队列中取值时才会进行存放
 */
@Slf4j
public class CacheThreadPool {


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();

        //submit方法中参数可以提供Callable类型的方法（任务有返回值就是传入的Callable对象，没有就是Runnable对象），可以获取返回值
        Future<String> futureResult = executorService.submit(() -> {
            log.debug("执行Callable()方法！！！");
            return "提交成功";
        });

        try {
            log.debug("执行Callable方法获取的返回值是---->{}", futureResult.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Future<?> future = executorService.submit(() -> {
            int a = 100;
            log.debug("启动线程");
            Thread.sleep(2000L);
            return a;
        });

        try {
            log.debug("{}", future.get());
            Thread.sleep(1000L);
            executorService.shutdown();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
}
