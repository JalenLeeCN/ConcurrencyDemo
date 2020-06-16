package pers.lzz.javaConcurrencyInPractice.chapter7;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * !不支持关闭的生产者-消费者日志服务
 * @Date 2020/6/2 10:35
 * @Author lzz
 */
public class LogWriter {
    private static final int CAPACITY = 4;
    private final BlockingQueue<String> queue;
    private final LoggerThread logger;
    private boolean shutdownRequested;

    public LogWriter(PrintWriter writer) {
        this.queue = new LinkedBlockingQueue<>(CAPACITY);
        this.logger = new LoggerThread(writer);
    }

    public void start() {
        logger.start();
    }

    /** 若无终止日志线程方法,jvm无法正常关闭.<br>可在能响应中断的take抛出异常后退出,实现中断日志线程
  <br>若直接中断将会丢失待写入的日志*/
    public void log(String msg) throws InterruptedException {
        queue.put(msg);
    }

    /**
     * <br>!先判断在运行: 存在竞态条件问题:可能会使服务关闭后仍将元素放入日志队列
     * @param msg
     * @throws InterruptedException
     */
    public void log1(String msg) throws InterruptedException {
        if(!shutdownRequested)
            queue.put(msg);
        else
            throw new IllegalStateException("logger is shut down");
    }

    private class LoggerThread extends Thread{
        private final PrintWriter writer;

        private LoggerThread(PrintWriter writer) {
            this.writer = writer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    //死循环消费阻塞队列元素
                    //take:Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
                    writer.print(queue.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                writer.close();
            }
        }
    }
}
