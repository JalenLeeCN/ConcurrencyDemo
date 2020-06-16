package pers.lzz.javaConcurrencyInPractice.chapter6;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FutureRenderer {
    //不建议使用Executors创建线程, 容易导致OOM
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final ExecutorService executor1 =
            new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1));

    //前提: 渲染页面, 将文本渲染与图片下载分离开来 (cpu密集型/io密集型分离)
    //效果: 前端页面首先看到文本, 图片下载完成后再进行展示
    void renderPage(CharSequence source) {
        final List<ImageInfo> imageInfos = scanForInmageInfo(source);
        //带有返回值的task
        Callable<List<ImagesData>> task = new Callable<List<ImagesData>>() {
            @Override
            public List<ImagesData> call() throws Exception {
                //下载图片
                List<ImagesData> result = new ArrayList<>();
                for (ImageInfo info : imageInfos) {
                    result.add(info.downloadImage());
                }
                return result;
            }
        };
        //异步计算结果
        Future<List<ImagesData>> future = executor.submit(task);
        //将下载图片的任务异步提交,先进行文本渲染
        renderText(source);

        try {
            //get方法会阻塞,等待线程任务执行完毕并获取任务返回值
            List<ImagesData> imagesDatas = future.get();
            for (ImagesData data : imagesDatas) {
                renderImage(data);
            }
        } catch (InterruptedException e) {
            //重新设置线程的中断状态
            Thread.currentThread().interrupted();
            //不需要结果, 取消任务
            future.cancel(true);
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //渲染图片
    private void renderImage(ImagesData data) {
    }

    //渲染文本
    private void renderText(CharSequence source) {

    }

    //扫描图片信息
    private List<ImageInfo> scanForInmageInfo(CharSequence source) {
        return null;
    }


    private class ImageInfo {
        //下载图片
        public ImagesData downloadImage() {
            return null;
        }
    }

    private class ImagesData {
    }
}
