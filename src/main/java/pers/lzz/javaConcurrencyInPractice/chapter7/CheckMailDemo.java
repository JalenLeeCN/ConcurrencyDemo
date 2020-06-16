package pers.lzz.javaConcurrencyInPractice.chapter7;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用私有线程池, 该池生命周期受限于方法调用
 * @Author lzz
 * @Date 2020/6/3 10:57
 */
public class CheckMailDemo {
    boolean checkMail(Set<String> hosts, long timeout, TimeUnit unit) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        final AtomicBoolean hasNewMail = new AtomicBoolean(false);//并发情况保证同时只有一个线程访问
        try {
            for (final String host : hosts) {
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (checkMail(host)) {
                            hasNewMail.set(true);
                        }
                    }
                });
            }
        } finally {
            exec.shutdown();
            exec.awaitTermination(timeout, unit);
        }
        return hasNewMail.get();
    }

    //检查单个host是否有邮件
    private boolean checkMail(String host) {
        return false;
    }
}
