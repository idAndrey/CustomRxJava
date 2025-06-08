package customrxjava;

import customrxjava.core.Observable;
import customrxjava.core.Observer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlingTest  {
    @Test
    void testErrorInMap() {
        List<String> result = new ArrayList<>();

        Observable<Integer> source = Observable.create(observer -> {
            observer.onNext(1);
            observer.onNext(0); // деление на ноль
            observer.onNext(2);
            observer.onComplete();
        });

        source.map(n -> 10 / n)
                .subscribe(
                        s   ->  { result.add("Next: " + s);},
                        err ->  { result.add("Error: " + err.getClass().getSimpleName());},
                        ()  ->  { result.add("Completed");}
                );

        assertEquals(List.of("Next: 10", "Error: ArithmeticException"), result);
    }

}
