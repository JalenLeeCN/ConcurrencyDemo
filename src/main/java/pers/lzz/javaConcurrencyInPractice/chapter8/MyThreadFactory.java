package pers.lzz.javaConcurrencyInPractice.chapter8;

import java.util.concurrent.ThreadFactory;

/**
 * 自定义的线程工厂
 * <br> 创建新的MyAppThread实例,并将特定于线程池的名字传递给MyAppThread的构造函数,从而
 * 可以在线程转储和错误日志信息中区分来自不同线程池的线程
 * @Author lzz
 */
public class MyThreadFactory implements ThreadFactory {
    private final String poolName;

    public MyThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new MyAppThread(r, poolName);
    }
}
