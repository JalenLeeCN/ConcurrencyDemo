package pers.lzz.javaConcurrencyInPractice.chapter7;

import java.util.concurrent.*;

/**
 * 使用Future来取消任务
 */
public class InterruptedByFutureDemo {
    private static ScheduledExecutorService taskExec = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        Runnable r = null;
        long timeout = 0;
        TimeUnit unit = null;
        try {
            timedRun(r,timeout,unit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void timedRun(final Runnable r, long timeout, TimeUnit unit) throws Exception {
        Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout,unit);
        } catch (InterruptedException e) {
            //发生此异常时,不需要结果可取消任务
            e.printStackTrace();
            //mayInterruptIfRunning - true if the thread executing this task should be interrupted;
            // otherwise, in-progress tasks are allowed to complete
            task.cancel(true);
        } catch (ExecutionException e) {
            //执行任务中抛出了异常, 那么重新抛出该异常
            e.printStackTrace();
            throw launderThrowable(e.getCause());
        } catch (TimeoutException e) {
            //发生此异常时,不需要结果可取消任务
            e.printStackTrace();
            task.cancel(true);
        }finally {
            //简化代码 finally中执行取消
            //如果任务已经结束,执行取消不会带来任何影响
            //如果任务正在运行,将被中断
            task.cancel(true);
        }
    }

    /**
     * 执行失败后退出任务,捕获任务中的异常再抛出,由调用者执行决策?
     * @param cause
     * @return
     */
    private static Exception launderThrowable(Throwable cause) {
        return null;
    }

}
