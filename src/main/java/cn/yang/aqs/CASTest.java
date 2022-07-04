package cn.yang.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 测试CAS方法在修改失败之后会不会一直阻塞住还是循环一段时间之后自动结束
 */
@Slf4j
public class CASTest {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        new Thread(() -> {
            atomicInteger.incrementAndGet();
            log.debug("增加线程运行结束，当前值为{}", atomicInteger.get());
        }).start();

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            log.debug("开始时间是{}", System.currentTimeMillis());
            boolean result = atomicInteger.compareAndSet(0, 100);
            log.debug("结束时间是{}，当前值为{}，修改结果为：{}", System.currentTimeMillis(), atomicInteger.get(), result);
        }).start();
    }
}
