package sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: dongyantong
 * @Date: 2019/11/12
 */
public class App {

    private  static Lock lock = new Mutex();

    private static Condition condition = lock.newCondition();

    public static void main(String[] args)  {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(App::action);
        executorService.submit(App::action);
        executorService.submit(App::action);
        executorService.shutdown();
    }

    public static void action(){
        lock.lock();
        System.out.printf("当前执行的线程是【%s】\n",Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(1L);
            System.out.printf("当前执行的线程是【%s】 释放了锁 ！\n",Thread.currentThread().getName());
        } catch (InterruptedException e) {
            System.out.printf("线程【%s】接收到了中断 \n",Thread.currentThread().getName());
            // do something
        }finally {
            lock.unlock();
        }
    }
}
