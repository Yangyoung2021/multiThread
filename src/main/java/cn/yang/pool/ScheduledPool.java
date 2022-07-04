package cn.yang.pool;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
public class ScheduledPool {

    public static void main(String[] args) {
        long period = 1000;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time = now.withHour(15).withMinute(15).withSecond(0).withNano(0);

        long initialDelay = Duration.between(now, time).toMillis();

        log.debug("{}ms后程序启动", initialDelay);
        log.debug("程序启动。。。。");
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleAtFixedRate(() -> {
            log.debug("命令执行");
        }, initialDelay, period, TimeUnit.MILLISECONDS);

    }
}
