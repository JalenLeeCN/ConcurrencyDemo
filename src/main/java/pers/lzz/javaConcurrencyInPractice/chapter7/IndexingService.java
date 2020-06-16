package pers.lzz.javaConcurrencyInPractice.chapter7;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * 只有在生产者和消费者数量已知情况下才能使用毒丸对象
 * <br>使用毒丸对象,当队列得到这个对象时,立即停止
 * <br>在FIFO中,毒丸对象保证消费者关闭之前完成队列中的所有操作
 * <br>无界队列才可使用毒丸对象(有界可能会发生毒丸对象无法放至队列的情况?)
 * @Author lzz
 * @Date 2020/6/2 16:56
 */
public class IndexingService {
    private static final File POISON = new File("");
    private final IndexerThread consumer = new IndexerThread();
    private final CrawlerThread producer = new CrawlerThread();
    private final BlockingQueue<File> queue;
    private final FileFilter fileFilter;
    private final File root;

    public IndexingService(BlockingQueue<File> queue, FileFilter fileFilter, File root) {
        this.queue = queue;
        this.fileFilter = fileFilter;
        this.root = root;
    }
    public void start() {
        producer.start();
        consumer.start();
    }

    //使生产者响应中断进行停止生产,停止后放入毒丸对象
    public void stop() {
        producer.interrupt();
    }

    //保证队列中的数据被消费完
    public void awaitTermination() throws InterruptedException {
        consumer.join();
    }

    /**
     * 消费队列中的对象,当读取到的对象为毒丸时停止
     */
    private class IndexerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    File file = queue.take();
                    if(file == POISON)
                        break;
                    else
                        indexFile(file);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生产结束后放入毒丸对象
     */
    private class CrawlerThread extends Thread {
        @Override
        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                while (true) {
                    try {
                        queue.put(POISON);
                        break;
                    } catch (InterruptedException e) {
                        /* 重新尝试 */
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void crawl(File root) throws InterruptedException { }

    private void indexFile(File file) { }

}
