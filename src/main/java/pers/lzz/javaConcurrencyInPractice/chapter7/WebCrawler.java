package pers.lzz.javaConcurrencyInPractice.chapter7;

import net.jcip.annotations.GuardedBy;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 网页爬虫程序 使用{@link TrackingExecutor}来保存未完成的任务已备后续执行
 * <br>爬虫程序重启时,将停止时记录的已抓取/未抓取的任务加入到队列中
 *
 * @Author lzz
 * @Date 2020/6/3 15:55
 */
public abstract class WebCrawler {
    private static final long TIMEOUT = 10L;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private volatile TrackingExecutor exec;
    @GuardedBy("this")
    private final Set<URL> urlsToCrawl = new HashSet<>();

    //遍历任务列表提交爬取任务
    public synchronized void start() {
        exec = new TrackingExecutor(Executors.newCachedThreadPool());
        for (URL url : urlsToCrawl) submitCrawlTask(url);
        urlsToCrawl.clear();
    }

    /**
     * 停止任务
     * @throws InterruptedException
     */
    public synchronized void stop() throws InterruptedException {
        try {
            //首先保存未执行的任务
            saveUncrawled(/*list of tasks that never commenced execution*/exec.shutdownNow());
            if (exec.awaitTermination(TIMEOUT, UNIT)) {//true if this executor terminated and false if the timeout elapsed before termination
                //获取被取消的任务
                saveUncrawled(exec.getCancelledTasks());
            }
        } finally {
            exec = null;
        }
    }

    /**
     * 处理网页方法
     * @param url
     * @return 处理完成的url列表
     */
    protected abstract List<URL> processPage(URL url);


    /**
     * 保存未执行的爬取网页任务
     * @param unCrawled
     */
    private void saveUncrawled(List<Runnable> unCrawled) {
        for (Runnable task : unCrawled) {
            //根据任务获取页面url
            urlsToCrawl.add(((CrawlTask) task).getPage());
        }
    }

    //提交爬取任务
    private void submitCrawlTask(URL url) {
        exec.execute(new CrawlTask(url));
    }

    private class CrawlTask implements Runnable {
        private final URL url;

        public CrawlTask(URL url) {
            this.url = url;
        }

        public URL getPage() {
            return url;
        }

        @Override
        public void run() {
            for (URL link : processPage(url)) {
                if(Thread.currentThread().isInterrupted())//当前任务是否被取消?
                    return;
                submitCrawlTask(link);
            }
        }
    }
}
