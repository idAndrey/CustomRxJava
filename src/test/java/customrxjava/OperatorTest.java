package customrxjava;

import customrxjava.core.Observable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OperatorTest {

    @Test
    void testMapAndFilter() {
        Observable<Integer> src = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onComplete();
        });

        List<Integer> result = new ArrayList<>();

        src.map(i -> i * 10).filter(i -> i > 10).subscribe(result::add);

        assertEquals(List.of(20, 30), result);
    }

    @Test
    void testFlatMap() {
        Observable<Integer> src = Observable.<Integer>create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        });

        List<Integer> out = new ArrayList<>();
        // теперь просто используем varargs-just:
        Observable<Integer> flat = src.flatMap(i ->
                        Observable.just(i, i * 10));

        flat.subscribe(out::add);

        assertEquals(4, out.size());
        assertTrue(out.containsAll(List.of(1, 10, 2, 20)));
    }
}
