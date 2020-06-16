package pers.lzz.javaConcurrencyInPractice.chapter8.puzzle;

import net.jcip.annotations.GuardedBy;

import java.util.concurrent.CountDownLatch;

/**
 * 由{@link ConcurrentPuzzleSolver}使用的携带结果的闭锁
 * @Author lzz
 */
public class ValueLatch<T> {
    @GuardedBy("this") private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return done.getCount() == 0;
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.await();
        synchronized (this) {
            return value;
        }
    }
}
