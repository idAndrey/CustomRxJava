package customrxjava;

import customrxjava.core.Observable;
import customrxjava.schedulers.IOThreadScheduler;
import customrxjava.schedulers.SingleThreadScheduler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BaseCoreTest {
    @Test
    void testCreateAndSubscribe() {
        List<String> received = new ArrayList<>();
        Observable<String> source = Observable.create(observer -> {
            observer.onNext("Abc");
            observer.onNext("Def");
            observer.onComplete();
        });

        source.subscribe(received::add,
                Throwable::printStackTrace,
                () -> received.add("complete"));

        assertEquals(List.of("Abc", "Def", "complete"), received);
    }

    @Test
    void testJustSingleElement() {
        List<Integer> list = new ArrayList<>();
        Observable.just(999).subscribe(list::add);

        assertEquals(1, list.size());
        assertEquals(999, list.get(0));
    }

} 