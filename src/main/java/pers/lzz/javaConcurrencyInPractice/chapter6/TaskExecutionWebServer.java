package pers.lzz.javaConcurrencyInPractice.chapter6;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskExecutionWebServer {
    private static final int NTHREADS = 100;//线程池数量
    private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket conn = socket.accept();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    //创建线程处理连接请求
//                    handleRequest(conn);
                }
            };
            exec.execute(task);
        }
    }
}
