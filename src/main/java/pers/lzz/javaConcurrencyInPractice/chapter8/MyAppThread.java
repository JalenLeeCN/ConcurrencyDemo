package pers.lzz.javaConcurrencyInPractice.chapter8;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 可以在此类中定制行为: 指定线程名/设置自定义UncaughtExceptionHandler向Logger中写入信息,维护统计信息(包括有多少个线程被创建和销毁,以及在线程被创建或者终止时把调式信息写入日志)
 * @Author lzz
 */
public class MyAppThread extends Thread {

    public static final String DEFAULT_NAME = "MyAppThread";
    private static volatile boolean debugLifecycle = false;
    private static final AtomicInteger created = new AtomicInteger();//创建线程数
    private static final AtomicInteger alive = new AtomicInteger();//存活线程数
    private static final Logger log = Logger.getAnonymousLogger();


    public MyAppThread(Runnable r) {
        this(r, DEFAULT_NAME);
    }

    public MyAppThread(Runnable runnable, String poolName) {
        super(runnable, poolName + "-" + created.incrementAndGet());
        setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.log(Level.SEVERE, "UNCAUGHT in thread " + t.getName(), e);
            }
        });
    }

    @Override
    public void run() {
        //复制debug标志以确保唯一的值
        boolean debug = debugLifecycle;
        if (debug) {
            log.log(Level.FINE, "Created " + getName());
        }
        try {
            alive.incrementAndGet();//递增
            super.run();
        } finally {
            alive.decrementAndGet();//递减
            if (debug) {
                log.log(Level.FINE, "Exiting " + getName());
            }
        }
    }

    public static int getThreadsCreated() {
        return created.get();
    }

    public static int getThreadsAlive() {
        return alive.get();
    }

    public static boolean getDebug() {
        return debugLifecycle;
    }

    public static void setDebug(boolean b) {
        debugLifecycle = b;
    }
}
