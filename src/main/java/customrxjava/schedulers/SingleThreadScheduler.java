package customrxjava.schedulers;

import customrxjava.core.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadScheduler implements Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(SingleThreadScheduler.class);

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "SingleThread");
                t.setDaemon(true);
                return t;
            });

    @Override
    public void execute(Runnable task) {
        logger.info("Выполнение задачи в SingleThreadScheduler: {}", task.toString());
        executor.submit(task);
    }
}
