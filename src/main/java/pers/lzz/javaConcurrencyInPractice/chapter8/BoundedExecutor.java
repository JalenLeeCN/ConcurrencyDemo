package pers.lzz.javaConcurrencyInPractice.chapter8;

import java.util.concurrent.*;

/**
 * 使用Semaphore来控制任务的提交速率
 * @Author lzz
 */
public class BoundedExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor exec, int bound) {
        this.exec = exec;
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command) throws InterruptedException {
        /* 创建一个固定大小的线程,并采用有界队列以及"调用者运行"饱和策略
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());*/
        semaphore.acquire();
        try {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        command.run();
                    } finally {
                        semaphore.release();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
            e.printStackTrace();
        }
    }
}
