package customrxjava;

import customrxjava.core.Observable;
import customrxjava.schedulers.IOThreadScheduler;
import customrxjava.schedulers.SingleThreadScheduler;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulerTest {

    @Test
    void testSubscribeOnScheduler() throws InterruptedException {
        AtomicReference<String> threadName = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Observable.just("test subscribeOn() method")
                .subscribeOn(new IOThreadScheduler())
                .subscribe(item -> {
                    threadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertNotEquals(Thread.currentThread().getName(), threadName.get());
    }

    @Test
    void testObserveOnScheduler() throws InterruptedException {
        AtomicReference<String> threadName = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Observable.just("test observeOn() method")
                .observeOn(new SingleThreadScheduler())
                .subscribe(item -> {
                    threadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertTrue(threadName.get().startsWith("SingleThread"));
    }

    @Test
    void testSingleSchedulerSequential() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> first = new AtomicReference<>();
        AtomicReference<String> second = new AtomicReference<>();

        Observable.just("a")
                .observeOn(new SingleThreadScheduler())
                .subscribe(item -> {
                    first.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        Observable.just("b")
                .observeOn(new SingleThreadScheduler())
                .subscribe(item -> {
                    second.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(first.get(), second.get());
    }
}
