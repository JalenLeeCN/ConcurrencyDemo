package pers.lzz.javaConcurrencyInPractice.chapter7;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 在专门的线程中执行中断任务
 * </br>依赖join, 退出时无法知道时join超时退出还是线程正常退出
 */
public class InterruptedByJoinDemo {
    private static ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        Runnable r = null;
        long timeout = 0;
        TimeUnit unit = null;
        try {
            timedRun(r, timeout, unit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void timedRun(final Runnable r, long timeout, TimeUnit unit) throws Exception {
        class RethrowableTask implements Runnable {
            private volatile Throwable t;

            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }

            void rethrow() throws Exception {
                if (t != null) {
                    throw launderThrowable(t);
                }
            }

            /**
             * 执行失败后退出任务,捕获任务中的异常再抛出,由调用者执行决策?
             * @param t
             * @return
             */
            private Exception launderThrowable(Throwable t) {
                return null;
            }
        }

        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        cancelExec.schedule(new Runnable() {
            @Override
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit);
        //Waits at most millis milliseconds for this thread to die
        taskThread.join(unit.toMillis(timeout));
        task.rethrow();
    }

}
