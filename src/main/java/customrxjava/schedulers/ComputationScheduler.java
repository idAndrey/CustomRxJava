package customrxjava.schedulers;

import customrxjava.core.Observable;
import customrxjava.core.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ComputationScheduler implements Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(ComputationScheduler.class);


    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private final ExecutorService executor;

    public ComputationScheduler() {
        int cores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(
                cores,
                r -> {
                    Thread t = new Thread(r, "ComputeThread-" + COUNTER.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
        );
    }

    @Override
    public void execute(Runnable task) {
        logger.info("Выполнение задачи в ComputationScheduler: {}", task.toString());
        executor.submit(task);
    }
}
