package customrxjava.core;

import app.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Observable<T> {

    private static final Logger logger = LoggerFactory.getLogger(Observable.class);

    public interface OnSubscribe<T> {
        void call(Observer<T> observer);
    }

    private final OnSubscribe<T> onSubscribe;

    private Observable(OnSubscribe<T> Subscribe) {
        this.onSubscribe = Subscribe;
    }

    public static <U> Observable<U> create(OnSubscribe<U> Subscribe) {
        logger.info("Создание Observable с применением метода create()");
        return new Observable<>(Subscribe);
    }

    public Disposable subscribe(
            Consumer<? super T> onNext,
            Consumer<Throwable> onError,
            Runnable onComplete
    ) {
        AtomicBoolean cancelled = new AtomicBoolean(false);

        Observer<T> actual = new Observer<>() {
            @Override
            public void onNext(T item) {
                if (!cancelled.get()) onNext.accept(item);
            }
            @Override
            public void onError(Throwable t) {
                if (!cancelled.get()) {
                    cancelled.set(true);
                    onError.accept(t);
                }
            }
            @Override
            public void onComplete() {
                if (!cancelled.get()) {
                    cancelled.set(true);
                    onComplete.run();
                }
            }
        };

        try {
            onSubscribe.call(actual);
        } catch (Throwable ex) {
            actual.onError(ex);
        }

        return new Disposable() {
            @Override
            public void dispose() {
                cancelled.set(true);
            }
            @Override
            public boolean isDisposed() {
                return cancelled.get();
            }
        };
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return create(observer ->
                scheduler.execute(() -> {
                    try {
                        onSubscribe.call(observer);
                    } catch (Throwable ex) {
                        observer.onError(ex);
                    }
                })
        );
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return create(observer ->
                this.subscribe(
                        item -> scheduler.execute(() -> observer.onNext(item)),
                        err  -> scheduler.execute(() -> observer.onError(err)),
                        ()   -> scheduler.execute(observer::onComplete)
                )
        );
    }
}
