package pers.lzz.javaConcurrencyInPractice.chapter7.newtaskfor;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.*;

/**
 * 改写newTaskFor使得CancellableTask可以创建自己的Future
 * <br>
 * @since java6 把Callable提交给ExecutorService时, submit方法返回一个Future,可以通过Future取消任务.
 * <br>newTaskFor:返回一个RunnableFuture接口,该接口扩展了Future和Runnable(并由FutureTask实现)
 */
@ThreadSafe
public class CancellingExecutor extends ThreadPoolExecutor {

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * 返回SocketUsingTask的Future(通过重写自定义的返回结果)
     * @param callable
     * @param <T>
     * @return
     */
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof CancellableTask)
            return ((CancellableTask) callable).newTask();
        else
            return super.newTaskFor(callable);
    }

}
