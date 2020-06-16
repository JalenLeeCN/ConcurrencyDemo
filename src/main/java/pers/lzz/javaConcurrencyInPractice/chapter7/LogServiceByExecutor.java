package pers.lzz.javaConcurrencyInPractice.chapter7;

import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 将管理线程的工作委托给ExecutorService
 * @Author lzz
 * @Date 2020/6/2 14:35
 */
public class LogServiceByExecutor {
    private static final long TIMEOUT = 1000l;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private final PrintWriter writer;

    public LogServiceByExecutor(PrintWriter writer) {
        this.writer = writer;
    }

    public void start() {
    }

    public void stop() {
        try {
            exec.shutdown();
            //true:执行终止 false:超时
            exec.awaitTermination(TIMEOUT, UNIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            writer.close();
        }
    }

    public void log(String msg) {
        exec.execute(new WriterTask(msg));
    }

    private class WriterTask implements Runnable {
        public WriterTask(String msg) {

        }

        @Override
        public void run() {

        }
    }
}
