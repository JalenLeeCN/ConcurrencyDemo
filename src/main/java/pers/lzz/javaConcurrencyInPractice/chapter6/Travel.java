package pers.lzz.javaConcurrencyInPractice.chapter6;

import java.util.*;
import java.util.concurrent.*;

/**
 * context: 旅行预订网站门户: 用户输入参数,门户获取各公司报价
 * </br>从各个公司获取报价任务互相独立,拆分为不同的任务
 */
public class Travel {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 2, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2));


    /**
     * 获取报价任务
     */
    private class QuoteTask implements Callable<TravelQuote> {

        private final TravelCompany company;
        private final TravelInfo travelInfo;

        public QuoteTask(TravelCompany company, TravelInfo travelInfo) {
            this.company = company;
            this.travelInfo = travelInfo;
        }

        @Override
        public TravelQuote call() throws Exception {
            return company.solicitQuote(travelInfo);
        }

        /**
         * 获取失败报价
         */
        public TravelQuote getFailureQuote(Throwable cause) {
            return null;
        }

        /**
         * 获取超时报价
         */
        public TravelQuote getTimeoutQuote(CancellationException e) {
            return null;
        }
    }

    /**
     * 获取旅行报价排行
     */
    public List<TravelQuote> getRankedlTraveQuotes(TravelInfo travelInfo, Set<TravelCompany> companies, Comparator<TravelQuote> ranking, long time, TimeUnit unit
    ) throws InterruptedException {
        //报价任务集合 有多少家公司就有多少报价任务
        List<QuoteTask> tasks = new ArrayList<>();
        for (TravelCompany company : companies) {
            tasks.add(new QuoteTask(company, travelInfo));
        }
        //添加一个限时的且按照集合中顺序的一组任务至线程池
        //返回的集合的数据结构和顺序与提交的集合一致, 使Future可以与Callable
        //调用线程中断或者超时,invokeAll将返回,超时任务将被取消
        List<Future<TravelQuote>> futures = executor.invokeAll(tasks, time, unit);
        List<TravelQuote> quotes = new ArrayList<>(tasks.size());
        Iterator<QuoteTask> taskIterator = tasks.iterator();
        for (Future<TravelQuote> f : futures) {
            QuoteTask task = taskIterator.next();
            try {
                //通过get或isCancelled()判断状态
//                f.isCancelled();
                quotes.add(f.get());
            } catch (ExecutionException e) {
                quotes.add(task.getFailureQuote(e.getCause()));
                e.printStackTrace();
            } catch (CancellationException e) {
                quotes.add(task.getTimeoutQuote(e));
            }
        }
        //根据ranking排列旅行报价
        Collections.sort(quotes, ranking);
        return quotes;
    }

    /**
     * 旅行报价(门户展示给用户的信息)
     */
    private class TravelQuote {
    }

    /**
     * 旅行公司
     */
    private class TravelCompany {
        public TravelQuote solicitQuote(TravelInfo travelInfo) {
            return null;
        }
    }

    /**
     * 行程信息
     */
    private class TravelInfo {
    }
}
