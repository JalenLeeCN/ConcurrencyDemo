package pers.lzz.javaConcurrencyInPractice.chapter10;

import javax.naming.InsufficientResourcesException;
import java.util.Random;

/**
 * 典型条件下会发生死锁的循环
 * <br>？？？没搞明白这节内容在讲啥，这个不是和{@link DynamicDeadLock}一样吗？
 * @Author lzz
 */
public class DemonstrateDeadLock {
    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_INERATIONS = 1_000_000;

    public static void main(String[] args) {
        final Random rnd = new Random();
        final Account[] accounts = new Account[NUM_ACCOUNTS];

        for (int i = 0; i < accounts.length; i++)
            accounts[i] = new Account();

        class TransferThread extends Thread {
            @Override
            public void run() {
                for (int i = 0; i < NUM_THREADS; i++) {
                    int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
                    int toAcct = rnd.nextInt(NUM_ACCOUNTS);
                    DollarAmount amount = new DollarAmount(rnd.nextInt(1000));
                    try {
                        transferMoney(accounts[fromAcct], accounts[toAcct], amount);
                    } catch (InsufficientResourcesException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (int i=0;i<NUM_THREADS;i++) {
            new TransferThread().start();
        }
    }

    public static void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount) throws InsufficientResourcesException {
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
