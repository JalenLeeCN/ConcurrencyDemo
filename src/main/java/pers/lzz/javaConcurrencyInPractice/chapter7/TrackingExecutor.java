package pers.lzz.javaConcurrencyInPractice.chapter7;

import java.util.*;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 在ExecutorService中跟踪在关闭后被取消的任务
 * <br>存在不可避免的竞态条件: 被认为已取消的任务可能已经完成(任务执行最后一条指令和线程池将任务记录为结束的两个时刻之间,线程池可能被关闭)
 * <br>可通过保证任务幂等性解决此问题
 * @Author lzz
 * @Date 2020/6/3 11:34
 */
public class TrackingExecutor extends AbstractExecutorService {

    private final ExecutorService exec;
    private final Set<Runnable> tasksCancelledAtShutdown = Collections.synchronizedSet(new HashSet<Runnable>());

    public TrackingExecutor(ExecutorService exec) {
        this.exec = exec;
    }

    //ExecutorService停止之后才获取已取消的任务
    public List<Runnable> getCancelledTasks() {
        if (!exec.isTerminated())
            throw new IllegalStateException("...");
        return new ArrayList<>(tasksCancelledAtShutdown);
    }

    @Override
    public void execute(final Runnable command) {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    command.run();
                } finally {
                    if (isShutdown() && Thread.currentThread().isInterrupted()) {
                        //Executors终止且当前线程响应中断,将此任务添加至此队列,用以追踪在关闭后被取消的任务?
                        tasksCancelledAtShutdown.add(command);
                    }
                }
            }
        });

    }
    //将ExecutorService的其他方法委托给exec
    @Override
    public void shutdown() {

    }

    //返回中断的任务列表
    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

}
