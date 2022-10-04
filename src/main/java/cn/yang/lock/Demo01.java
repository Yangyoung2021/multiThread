package cn.yang.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Demo01 {

    public static void main(String[] args) {
        AccountCas accountCas = new AccountCas(0);
        Account.test(accountCas);

        System.out.println("----------");

        AccountUnsafe unsafe = new AccountUnsafe(0);
        Account.test(unsafe);

    }

    interface Account {
        //获取当前账户余额
        Integer getBalance();
        //取款
        void withdraw(Integer amount);
        //存款
        void save(Integer amount);

        //测试线程
        static void test(Account account) {
            ArrayList<Thread> threads = new ArrayList<>();
            //模拟存钱
            for (int i = 0; i < 1000; i++) {
                threads.add(new Thread(() -> account.save(100)));
            }
            //模拟取钱
            for (int i = 0; i < 1000; i++) {
                threads.add(new Thread(() -> account.withdraw(100)));
            }
            long start = System.currentTimeMillis();
            threads.forEach(Thread::start);
            threads.forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            long end = System.currentTimeMillis();
            log.debug("耗时{}ms", (end - start));
            log.debug("最终剩余金额{}", account.getBalance());
        }
    }

    static class AccountUnsafe implements Account {

        private int money;

        public AccountUnsafe(Integer money) {
            this.money = money;
        }

        @Override
        public Integer getBalance() {
            return money;
        }

        @Override
        public synchronized void withdraw(Integer amount) {
            this.money -= amount;
        }

        @Override
        public synchronized void save(Integer amount) {
            this.money += amount;
        }
    }

    static class AccountCas implements Account{

        private final AtomicInteger money;

        public AccountCas(Integer money) {
            this.money = new AtomicInteger(money);
        }

        @Override
        public Integer getBalance() {
            return money.get();
        }

        @Override
        public void withdraw(Integer amount) {
            money.getAndAdd(-amount);
        }

        @Override
        public void save(Integer amount) {
            money.addAndGet(amount);
        }
    }
}
