package pers.lzz.javaConcurrencyInPractice.chapter6;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用ScheduledThreadPoolExecutor
 */
public class TimerWithScheduledThreadPoolDemo {
    public static void main(String[] args) throws InterruptedException {
        ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(5);


        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("--------------------"+Thread.currentThread().getName());
            }
        },0,500, TimeUnit.MILLISECONDS);

/*        for (int i = 0;i<10;i++) {
            scheduled.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+" : "+System.currentTimeMillis());
                }
            });
        }*/

/*        Thread.sleep(10000);
        if (scheduled != null) {
            scheduled.shutdown();
        }*/
    }
}
