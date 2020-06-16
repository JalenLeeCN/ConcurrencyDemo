package pers.lzz.javaConcurrencyInPractice.chapter6;

import java.util.List;
import java.util.concurrent.*;

public class Renderer {
    private final ExecutorService executor;

    public Renderer(ExecutorService executor) {
        this.executor = executor;
    }

    /***
     * 在FutureRenderer逻辑上,为每一张图片创建一个任务
     * @param source
     */
    void renderPage(CharSequence source) {
        List<ImageInfo> info = scanForImageInfo(source);
        CompletionService<ImagesData> completionService = new ExecutorCompletionService<>(executor);
        for (final ImageInfo img : info) {
            completionService.submit(new Callable<ImagesData>() {
                @Override
                public ImagesData call() throws Exception {
                    return img.downloadImage();
                }
            });
            //渲染文本
            renderText(source);
            try {
                for (int t = 0, n = info.size(); t < n; t++) {
                    Future<ImagesData> future = completionService.take();
                    ImagesData imagesData = future.get();
        /*            //get方法可设置超时时间,超时则取消任务
                    long timeLeft = 1000;
                    ImagesData imagesData = null;
                    try {
                        imagesData = future.get(timeLeft, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        future.cancel(true);
                        e.printStackTrace();
                    }*/
                    //渲染单张图片
                    renderImage(imagesData);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.getCause();
                e.printStackTrace();
            }
        }
    }

    private void renderImage(ImagesData imagesData) {

    }

    private void renderText(CharSequence source) {
    }

    private List<ImageInfo> scanForImageInfo(CharSequence source) {
        return null;
    }

    private class ImageInfo {
        public ImagesData downloadImage() {
            return null;
        }
    }

    private class ImagesData {
    }
}
