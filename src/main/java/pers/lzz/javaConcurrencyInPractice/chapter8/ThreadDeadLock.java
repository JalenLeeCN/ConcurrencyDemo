package pers.lzz.javaConcurrencyInPractice.chapter8;

import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 在单线程Executor中任务发生死锁
 *<br> 如果一个任务将另一个任务提交到同一个Executor,并且等待这个被提交任务的结果,那么通常会发生死锁
 * <br> 单例ExecutorService 或者线程池不够大的情况, 多个任务通过栅栏(barrier)机制协调, 将导致线程饥饿死锁
 * <br> 新任务需要等待其他任务释放连接
 * @Author lzz
 * @Date 2020/6/5 15:08
 */
public class ThreadDeadLock {
    static ExecutorService exec = Executors.newSingleThreadExecutor();
//    static ExecutorService exec = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        try {
            Future<String> submit = exec.submit(new RenderPageTask(exec));
            String s = submit.get();
            System.out.println("main.s: "+s);
        } catch (Exception e) {
            e.printStackTrace();
        }
/*        Future<String> aaaa = exec.submit(new LoadFileTask("aaaa"));
        try {
            System.out.println("aaaa: "+aaaa.get());
            List<Runnable> runnables = exec.shutdownNow();
            System.out.println(runnables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }
}

class RenderPageTask implements Callable<String> {
    private ExecutorService exec;

    public RenderPageTask(ExecutorService exec) {
        this.exec = exec;
    }

    @Override
    public String call() throws Exception {
        Future<String> header, footer;
        header = exec.submit(new LoadFileTask("header.html"));
        footer = exec.submit(new LoadFileTask("footer.html"));
        footer = exec.submit(new LoadFileTask("footer1.html"));
        footer = exec.submit(new LoadFileTask("footer2.html"));
        String page = renderBody();
        //此处会导致死锁
        return header.get() + page + footer.get();
//        return header.get() + page;
    }

    private String renderBody() {
        System.out.println("renderBody()");
        return " renderBody";
    }
}

class LoadFileTask implements Callable<String> {
    private String s;

    public LoadFileTask(String s) {
        this.s = s;
    }

    @Override
    public String call() throws Exception {
        SECONDS.sleep(1);
        System.out.println(Thread.currentThread().getName() + " - " + s);
        return s;
    }
}

