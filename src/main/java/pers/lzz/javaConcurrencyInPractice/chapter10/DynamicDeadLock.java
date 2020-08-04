package pers.lzz.javaConcurrencyInPractice.chapter10;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.util.Comparator;

/**
 * 动态死锁顺序死锁演示
 * <br>transferMoney发生死锁情况：<br>
 * 一个线程从 X 向 Y 转账，另一个线程从 Y 向 X 转账，无法控制参数顺序，导致执行时序不当引发死锁
 *
 * @Author lzz
 */
public class DynamicDeadLock {
    static Account fromAccount = new Account();
    static Account toAccount = new Account();

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 9; i++) {
            final int finalI = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (finalI % 2 == 0) {
                            new Transfer().transferMoney(toAccount, fromAccount, new DollarAmount());
                        } else
                            new Transfer().transferMoney(fromAccount, toAccount, new DollarAmount());
                    } catch (InsufficientResourcesException e) {
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();
        }
        for (int j = 0; j < 9; j++) {
            threads[j].join();
        }
    }


}

class Transfer {
    public void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount) throws InsufficientResourcesException {
        synchronized (fromAccount) {
            synchronized (toAccount) {
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    //扣款账户余额 < 扣款金额
                    throw new InsufficientResourcesException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
    }
}

class Account {

    /**
     * 获取当前账户余额
     *
     * @return
     */
    public DollarAmount getBalance() {
        return new DollarAmount();
    }

    /**
     * 记入借（账户）借方，借记；（从银行账户中）取款
     *
     * @param amount
     */
    public void debit(DollarAmount amount) {
        System.out.println("fromAccount debit amount");
    }

    /**
     * 相信，信任；把…归给，归功于；赞颂
     *
     * @param amount
     */
    public void credit(DollarAmount amount) {
        System.out.println("toAccount credit amount");
    }
}

class DollarAmount implements Comparator<DollarAmount> {

    public DollarAmount() {
    }

    public DollarAmount(int i) {
    }

    //a negative integer, zero, or a positive integer as the first argument
    // is less than, equal to, or greater than the second.
    @Override
    public int compare(DollarAmount o1, DollarAmount o2) {
        //此处覆写比较方法
        return 0;
    }

    public int compareTo(DollarAmount amount) {
       /*
        //比较当前对象与传参
       int compare = compare(this, amount);
        if (compare < 0) {
            return -1;
        }*/
        return 0;
    }

    public BigDecimal getAmount() {
        return new BigDecimal(1);
    }
}