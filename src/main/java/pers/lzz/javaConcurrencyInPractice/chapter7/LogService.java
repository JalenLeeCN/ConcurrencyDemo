package pers.lzz.javaConcurrencyInPractice.chapter7;

import net.jcip.annotations.GuardedBy;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

/**
 * 向LogWriter添加可靠的取消操作
 * <br>解决LogWriter中不可靠关闭的竞态条件问题:使日志提交成为原子操作
 * @see LogWriter
 * @Author lzz
 * @Date 2020/6/2 11:16
 */
public class LogService {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    @GuardedBy("this")
    private boolean isShutdown;
    @GuardedBy("this")
    private int reservations;

    public LogService(BlockingQueue<String> queue, LoggerThread loggerThread, PrintWriter writer, boolean isShutdown, int reservations) {
        this.queue = queue;
        this.loggerThread = loggerThread;
        this.writer = writer;
        this.isShutdown = isShutdown;
        this.reservations = reservations;
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if(isShutdown) throw new IllegalStateException("");
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        //用原子方式检查关闭请求;计数器来保持提交消息的权力
        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (LogService.this) {
                            if (isShutdown && reservations == 0)
                                break;
                        }
                        String msg = queue.take();
                        synchronized (LogService.this) {
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }finally {
                writer.close();
            }
        }
    }
}
