package pers.lzz.javaConcurrencyInPractice.chapter7.newtaskfor;

import net.jcip.annotations.GuardedBy;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public abstract class SocketUsingTask<T> implements CancellableTask<T> {
    @GuardedBy("this")
    private Socket socket;
    private boolean mayInterruptIfRunning;

    protected synchronized void setSocket(Socket s) {
        socket = s;
    }

    public synchronized void cancel() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            //SocketUsingTask可以通过自己的task,即当前返回值,取消任务,底层的套接字将被关闭且线程将被中断,
            //提高了任务对取消操作的响应性:调用可中断方法的同时确保响应取消操作,而且还能调用可阻塞的套接字i/o方法
            //自定义取消方法,通过重写实现自定义取消逻辑???
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}
