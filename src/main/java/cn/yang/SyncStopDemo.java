package cn.yang;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SyncStopDemo {

    public static void main(String[] args) {
        TwoPhaseTermination tpt1 = new TwoPhaseTermination();
        TwoPhaseTermination tpt2 = new TwoPhaseTermination();
        TwoPhaseTermination tpt3 = new TwoPhaseTermination();

        Thread t2 = new Thread(() -> {
            tpt1.startMonitor();

            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tpt1.stopMonitor();

        }, "t2");

        t2.start();

        Thread t1 = new Thread(() -> {
            tpt2.startMonitor();

            try {
                Thread.sleep(3500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tpt2.stopMonitor();

        }, "t1");

        t1.start();

        Thread t3 = new Thread(() -> {
            tpt3.startMonitor();

            try {
                Thread.sleep(3500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tpt3.stopMonitor();

        }, "t3");

        t3.start();
    }
}

@Slf4j
class TwoPhaseTermination {
    private Thread monitor;

    public void startMonitor() {
        monitor = new Thread(() -> {
            while (true) {
                Thread current = Thread.currentThread();
                if (current.isInterrupted()) {
                    log.debug("{}处理后事。。。", current.getName());
                    log.debug("{}。。。", monitor.getName());
                    break;
                }
                try {
                    Thread.sleep(1000L);
                    log.debug("执行监控{}", current.getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 重新设置打断标记
                    current.interrupt();
                }
            }
        });

        monitor.start();
    }

    public void stopMonitor() {
        monitor.interrupt();
    }
}
