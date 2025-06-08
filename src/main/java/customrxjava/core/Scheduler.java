package customrxjava.core;

public interface Scheduler {
    void execute(Runnable task);
}
