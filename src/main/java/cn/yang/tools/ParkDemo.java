package cn.yang.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * park方法和unPark方法可以不按照顺序使用，因为LockSupport中维护了一个标志位，他们都是通过标志位来进行操作，但是最多只能提前
 * unPark一次
 */
@Slf4j
public class ParkDemo {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("park");
            LockSupport.park();
            log.debug("unPark1");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LockSupport.park();
            log.debug("unPark2");
        }, "t1");


        t1.start();
        LockSupport.unpark(t1);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LockSupport.unpark(t1);




    }
}
