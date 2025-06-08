package customrxjava.schedulers;

import customrxjava.core.Scheduler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class IOThreadScheduler implements Scheduler {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private final ExecutorService executor =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "IOThread-" + COUNTER.getAndIncrement());
                t.setDaemon(true);
                return t;
            });

    @Override
    public void execute(Runnable task) {
        executor.submit(task);
    }
}