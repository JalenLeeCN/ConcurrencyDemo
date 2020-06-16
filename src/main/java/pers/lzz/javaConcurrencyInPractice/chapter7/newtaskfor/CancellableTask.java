package pers.lzz.javaConcurrencyInPractice.chapter7.newtaskfor;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

public interface CancellableTask<T> extends Callable<T> {
    void cancel();

    /**
     * 工厂方法用来构造RunnableFuture
     * @return
     */
    RunnableFuture<T> newTask();
}
