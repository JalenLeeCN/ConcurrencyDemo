package pers.lzz.javaConcurrencyInPractice.chapter7;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 持续枚举素数,搜索下一个素数之前先检查是否需要取消任务标志
 */
@ThreadSafe
public class PrimeGenerator implements Runnable{

    //运行1秒的素数生成器
    List<BigInteger> aSecondsOfPrimes() {
        PrimeGenerator generator = new PrimeGenerator();
        new Thread(generator).start();
        try {
            SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            generator.cancel();
        }
        return generator.get();
    }

    @GuardedBy("this")
    private final List<BigInteger> primes = new ArrayList<>();
    private volatile  boolean cancelled;

    @Override
    public void run() {
        BigInteger p  = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }

    }
    //取消任务
    public void cancel() {
        cancelled = true;
    }

    //获取素数
    public synchronized List<BigInteger> get() {
        return new ArrayList<>(primes);
    }
}

class PrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;

    PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                queue.put(p.nextProbablePrime());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //允许线程退出
        }
    }

    public void cancel() {
        interrupt();
    }
}
