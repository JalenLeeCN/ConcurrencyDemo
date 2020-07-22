package pers.lzz.javaConcurrencyInPractice.chapter10;

import javax.naming.InsufficientResourcesException;

/**
 * 通过锁顺序来避免死锁
 * <br>通过比较对象hash值来防止动态死锁{@link DynamicDeadLock}
 * <br>？？？相同的对象执行操作可以顺序获取锁，若在分线程中创建对象并获取hashcode是否会失去确定锁顺序的作用？
 * @Author lzz
 */
public class AvoidDeadLock {
    private static final Object tieLock = new Object();
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
                            new AvoidDeadLock().transferMoney(toAccount, fromAccount, new DollarAmount());
                        } else
                            new AvoidDeadLock().transferMoney(fromAccount, toAccount, new DollarAmount());
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

    public void transferMoney(final Account fromAccount,
                              final Account toAccount,
                              final DollarAmount amount) throws InsufficientResourcesException {
        class Helper {
            public void transfer() throws InsufficientResourcesException {
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientResourcesException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }

        int fromHash = System.identityHashCode(fromAccount);
        int toHash = System.identityHashCode(toAccount);

        if (fromHash < toHash) {
            //A->B 先锁A
            synchronized (fromAccount) {
                synchronized (toAccount) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            //B->A 还是先锁A
            synchronized (toAccount) {
                synchronized (fromAccount) {
                    new Helper().transfer();
                }
            }
        } else {
            //A B返回的hash值一直，先获取加时锁
            //若使用真实账号确定执行顺序可省去加时赛锁（真实账号不存在相同可能性）
            synchronized (tieLock) {
                synchronized (fromAccount) {
                    synchronized (toAccount) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }
}
