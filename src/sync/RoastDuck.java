package sync;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: dongyantong
 * @Date: 2019/11/18
 */
public class RoastDuck {

//     private static Lock lock = new ReentrantLock();

    private static Lock lock = new Mutex();

    private static Condition full = lock.newCondition();

    private static Condition empty = lock.newCondition();

    private static LinkedList<String> roastDuckFactory = new LinkedList<>();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(RoastDuck::producer);
        executorService.submit(RoastDuck::consumer);
        executorService.shutdown();
    }

    private static void producer() {
        while (true) {
            lock.lock();
            try {
                while (roastDuckFactory.size() >= 5) {
                    System.out.println("当前烤鸭的量已经满了");
                    full.await();
                }
                System.out.println("生产了一只北京烤鸭");
                roastDuckFactory.add("北京烤鸭");
                empty.signalAll();
            } catch (InterruptedException e) {

            } finally {
                lock.unlock();
            }
        }
    }

    private static void consumer() {
        while (true) {
            lock.lock();
            try {
                while (roastDuckFactory.isEmpty()) {
                    System.out.println("烤鸭已经消费完了");
                    empty.await();
                }
                roastDuckFactory.pop();
                System.out.println("吃掉了一只北京烤鸭");
                full.signalAll();
            } catch (InterruptedException e) {

            } finally {
                lock.unlock();
            }
        }
    }
}
