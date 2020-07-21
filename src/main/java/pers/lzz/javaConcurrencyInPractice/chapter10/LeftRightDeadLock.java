package pers.lzz.javaConcurrencyInPractice.chapter10;

import java.util.concurrent.TimeUnit;

/**
 * 简单的顺序死锁演示
 *
 * @Author lzz
 */
public class LeftRightDeadLock {
    static LeftRight lr = new LeftRight();

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 9; i++) {
            final int finalI = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (finalI % 2 == 0) {
                        lr.leftright();
                    } else {
                        lr.rightLeft();
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


class LeftRight {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftright() {
        synchronized (left) {
            synchronized (right) {
                doSomething();
            }
        }
    }

    public void rightLeft() {
        synchronized (right) {
            synchronized (left) {
                doSomethingElse();
            }
        }
    }

    private void doSomethingElse() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("doSomethingElse");
    }

    private void doSomething() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("doSomething");
    }


}
