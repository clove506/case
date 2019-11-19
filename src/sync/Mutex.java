package sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: dongyantong
 * @Date: 2019/11/14
 */
public class Mutex implements Lock {

    private final Sync sync;

    public Mutex() {
        this.sync = new Sync();
    }

    private  class Sync extends AbstractQueuedSynchronizer {
        @Override
        public boolean tryAcquire(int tag) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() && compareAndSetState(0, tag)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }

            } else if (current == getExclusiveOwnerThread()) {
                int nextc = c + tag;
                if (nextc < 0) // overflow
                    throw new RuntimeException("已经超过可重入的最大次数限制！");
                setState(nextc);
                return true;
            }

            return false;
        }

        @Override
        public boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
        @Override
        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        Condition newCondition() {
            return new ConditionObject();
        }
    }

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
