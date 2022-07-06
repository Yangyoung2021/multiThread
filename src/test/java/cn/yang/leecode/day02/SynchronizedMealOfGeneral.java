package cn.yang.leecode.day02;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class SynchronizedMealOfGeneral {

    private static volatile boolean eatable = true;
    static HashMap<String, Integer> recodes = new HashMap<>();

    public static void main(String[] args) {

        Chopstick c1 = new Chopstick(1);
        Chopstick c2 = new Chopstick(2);
        Chopstick c3 = new Chopstick(3);
        Chopstick c4 = new Chopstick(4);
        Chopstick c5 = new Chopstick(5);

        General g1 = new General("关羽", c1, c2);
        General g2 = new General("张飞", c2, c3);
        General g3 = new General("马超", c3, c4);
        General g4 = new General("黄忠", c4, c5);
        General g5 = new General("赵云", c5, c1);

        recodes.put(g1.getName(), 0);
        recodes.put(g2.getName(), 0);
        recodes.put(g3.getName(), 0);
        recodes.put(g4.getName(), 0);
        recodes.put(g5.getName(), 0);

        new Thread(g1).start();
        new Thread(g2).start();
        new Thread(g3).start();
        new Thread(g4).start();
        new Thread(g5).start();

        stopEatAfterTime(1000L);
        recodes.forEach((key, value) -> log.debug("{}就餐{}次", key, value));
    }

    public static void stopEatAfterTime(long time) {
        try {
            Thread.sleep(time);
            eatable = false;
            Thread.sleep(1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static class Chopstick {
        private volatile boolean used;
        private final int id;

        public Chopstick(boolean used, int id) {
            this.used = used;
            this.id = id;
        }

        public Chopstick(int id) {
            this(false, id);
        }

        public int getId() {
            return this.id;
        }

        public boolean isFree() {
            return !used;
        }

        public void occupy() {
            this.used = true;
        }

        public void free() {
            this.used = false;
        }
    }

    static class General implements Runnable {
        private final String name;
        private final Chopstick left;
        private final Chopstick right;

        public General(String name, Chopstick left, Chopstick right) {
            this.left = left;
            this.right = right;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public void run() {
            while (eatable) {
                eat();
            }
        }

        public void eat() {
            //采用左手边筷子没有使用直接锁右手筷子方法
            if (left.isFree()) {
                synchronized (right) {
                    log.debug("{}获得筷子{}", getName(), right.getId());
                    right.occupy();
                    if (left.isFree()) {
                        synchronized (left) {
                            log.debug("{}获得筷子{}", getName(), left.getId());
                            left.occupy();
                            try {
                                Thread.sleep(0L);
                                log.debug("{}开始就餐", getName());
                                //记录就餐
                                recodes.put(this.getName(), recodes.get(getName()) + 1);
                                left.free();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    right.free();
                }
            }
        }

    }

}
