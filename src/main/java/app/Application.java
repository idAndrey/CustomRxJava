package app;

import customrxjava.core.Disposable;
import customrxjava.core.Observable;
import customrxjava.schedulers.ComputationScheduler;
import customrxjava.schedulers.IOThreadScheduler;
import customrxjava.schedulers.SingleThreadScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws InterruptedException {

        logger.info("======/ Реализация пользовательской библиотеки RxJava /======");
//        System.out.println("Начало: базовая эмиссия");
        Observable.<Integer>create(obs -> {
                    obs.onNext(1);
                    obs.onNext(2);
                    obs.onNext(3);
                    obs.onComplete();
                })
                .subscribe(
                        item -> logger.info("  получено: {}", item),
                        err  -> logger.error("  ошибка: {}", err.toString(), err),
                        ()   -> logger.info("  завершено")
                );

        logger.info("\n\nПроверка преобразования: map и filter");
        Observable.<Integer>create(obs -> {
                    for (int i = 1; i <= 5; i++) obs.onNext(i);
                    obs.onComplete();
                })
                .map(x -> x * 10)
                .filter(x -> x % 20 == 0)
                .subscribe(
                        item -> logger.info("  результат: {}", item),
                        err  -> logger.error("  ошибка: {}", err.toString(), err),
                        ()   -> logger.info("  выполнено")
                );

        logger.info("\n\nПроверка преобразования: flatMap");
        Observable.<String>create(obs -> {
                    obs.onNext("A");
                    obs.onNext("B");
                    obs.onComplete();
                })
                .flatMap(ch -> Observable.<String>create(inner -> {
                    inner.onNext(ch + "1");
                    inner.onNext(ch + "2");
                    inner.onComplete();
                }))
                .subscribe(
                        s   -> logger.info("  элемент: {}", s),
                        err -> logger.error("  ошибка: {}", err.getMessage(), err),
                        ()  -> logger.info("  завершено")
                );

        logger.info("\n\nПроверка управления потоками: subscribeOn и observeOn");
        CountDownLatch latch = new CountDownLatch(2);
        Observable.<Integer>create(obs -> {
                    obs.onNext(100);
                    obs.onNext(200);
                    obs.onComplete();
                })
                .subscribeOn(new IOThreadScheduler())
                .observeOn(new ComputationScheduler())
                .subscribe(
                        x -> {
                            logger.info("  Поток {} получил {}",
                                    Thread.currentThread().getName(), x);
                            latch.countDown();
                        },
                        err -> logger.error("  ошибка: {}", err.getMessage(), err),
                        ()  -> { /* ignore */ }
                );

        // ждём два onNext
        latch.await(2, TimeUnit.SECONDS);

        logger.info("\n\nПроверка отмены подписки");
        Observable<Long> ticker = Observable.create(obs -> {
            long i = 0L;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    obs.onNext(i++);
                    Thread.sleep(300);
                }
            } catch (InterruptedException ex) {
                obs.onComplete();
            }
        });
        Disposable disp = ticker
                .subscribeOn(new SingleThreadScheduler())
                .subscribe(
                        i   -> logger.info("  счетчик: {}", i),
                        err -> logger.error("  ошибка: {}", err.getMessage(), err),
                        ()  -> logger.info("  счетчик завершен")
                );

        Thread.sleep(1500);
        disp.dispose();
        logger.info("  подписка отменена");

        Thread.sleep(2000);
        logger.info("===================/ Программа завершена /===================");
    }
}
