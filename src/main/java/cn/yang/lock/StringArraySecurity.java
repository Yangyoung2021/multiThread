package cn.yang.lock;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StringArraySecurity {

    public static void main(String[] args) {
        String[] s = new String[10];
        Random random = new Random();
        ExecutorService service = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            int tem = i;
            service.submit(() -> {
                for (int j = 0; j <= 100; j++) {
                    try {
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s[tem] = j + "%";
                    System.out.print("\r" + Arrays.toString(s));
                }
            });
        }

        service.shutdown();

    }
}
