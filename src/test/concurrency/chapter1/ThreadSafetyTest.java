package test.concurrency.chapter1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * related material
 * https://stackoverflow.com/questions/55277660/how-to-test-that-java-class-is-thread-safe
 * https://stackoverflow.com/questions/17827022/how-is-countdownlatch-used-in-java-multithreading
 * https://dzone.com/articles/how-to-test-if-a-class-is-thread-safe-in-java
 */
public class ThreadSafetyTest {

    ///@NotThreadSafe
    public class UnsafeSequence {
        private int value;
        /** Returns a unique value. */
        public int getNext() {
            return value++;
        }
    }

    //@ThreadSafe
    public class Sequence {
        private int value;
        public synchronized int getNext() {
            return value++;
        }
    }

    @Test
    public void unsafe_sequence() throws InterruptedException {
        UnsafeSequence unsafeSequence = new UnsafeSequence();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    unsafeSequence.getNext();
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    unsafeSequence.getNext();
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Assertions.assertNotEquals(200, unsafeSequence.getNext());
    }

    @Test
    public void safe_sequence() throws InterruptedException {
        Sequence safeSequence = new Sequence();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    safeSequence.getNext();
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    safeSequence.getNext();
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Assertions.assertEquals(200, safeSequence.getNext());
    }
}
