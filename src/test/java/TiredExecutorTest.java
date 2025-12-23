import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import scheduling.TiredExecutor;
import scheduling.TiredThread;



public class TiredExecutorTest {

    TiredExecutor executor;
    PriorityBlockingQueue<TiredThread> heap;
    TiredThread[] workers;
    AtomicInteger inFlight;
    


    @BeforeEach
    public void setup() {
        executor = new TiredExecutor(4);
        try {
            Field heapField = TiredExecutor.class.getDeclaredField("idleMinHeap");
            Field workersField = TiredExecutor.class.getDeclaredField("workers");
            Field inFlightField = TiredExecutor.class.getDeclaredField("inFlight");
            heapField.setAccessible(true);
            workersField.setAccessible(true);
            inFlightField.setAccessible(true);
            heap = (PriorityBlockingQueue<TiredThread>) heapField.get(executor);
            workers = (TiredThread[]) workersField.get(executor);
            inFlight = (AtomicInteger) inFlightField.get(executor);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }
    
    @Test
    public void constructorTest() {
        assertNotNull(executor);
        assertEquals(4, heap.size());
        assertEquals(0, inFlight.get());
        for (int i = 0; i < workers.length; i++) {
            assertNotNull(workers[i]);
            assertTrue(workers[i].isAlive());
        }
    }

    @Test
    public void testSubmitHeapSize() {
        Runnable task = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        };
        executor.submit(task);
        assertEquals(3, heap.size());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {}
        assertEquals(4, heap.size());
    }

    @Test
    public void testSubmitInFlightCount() {
        Runnable task = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        };
        executor.submit(task);
        assertEquals(1, inFlight.get());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {}
        assertEquals(0, inFlight.get());
    }

    @Test
    public void testSubmitDoTask() {
        final AtomicBoolean check = new AtomicBoolean(false);
        Runnable task = () -> {
            try {
                check.set(true);
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        };
        executor.submit(task);
        assertEquals(3, heap.size());
        assertEquals(1, inFlight.get());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {}
        assertTrue(check.get());
        assertEquals(0, inFlight.get());
        assertEquals(4, heap.size());
    }

    @Test
    public void testSubmitAllHeapSize(){
        Runnable task = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        };
        List<Runnable> tasks = java.util.Arrays.asList(task, task, task, task);
        executor.submitAll(tasks);
        assertEquals(4, heap.size());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        assertEquals(4, heap.size());
    }

    @Test
    public void testSubmitAllInFlightCount(){
        Runnable task = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        };
        List<Runnable> tasks = java.util.Arrays.asList(task, task, task, task);
        executor.submitAll(tasks);
        assertEquals(0, inFlight.get());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        assertEquals(0, inFlight.get());
    }

    @Test
    public void testSubmitAllBlocksUntilCompletion() {
        int numTasks = 100;
        AtomicInteger counter = new AtomicInteger(0);
        
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
                counter.incrementAndGet();
            });
        }
        executor.submitAll(tasks);
        assertEquals(numTasks, counter.get());
    }

    @Test
    public void testSubmitAllParallel() {
        long start = System.currentTimeMillis();        
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tasks.add(() -> {
                try { Thread.sleep(200); } catch (InterruptedException e) {}
            });
        }
        executor.submitAll(tasks);
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration < 400);
    }
    
    @Test
    public void testThreadReuse() {
        TiredExecutor oneThreadExecutor = new TiredExecutor(1);
        AtomicInteger counter = new AtomicInteger(0);
        
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(() -> counter.incrementAndGet());
        }

        oneThreadExecutor.submitAll(tasks);
        assertEquals(10, counter.get());        
    }

    @Test
    public void testShutdownClearsQueue() {
        try{
            executor.shutdown();
        } catch (Exception e){}
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testShutdownStopsThreads() {
        try{
            executor.shutdown();
        } catch (Exception e){}
        try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        for (TiredThread worker : workers) {
            assertFalse(worker.isAlive());
        }
    }

    // @Test
    // public void testSubmitAfterShutdown() {
    //     try{
    //         executor.shutdown();
    //     } catch (Exception e){}
    //     Runnable task = () -> {};
    //     assertThrows(IllegalStateException.class, () -> {
    //         executor.submit(task);
    //     });
    // }

    // @Test
    // public void testShutDownFinishCurrTasks() {
    //     AtomicInteger counter = new AtomicInteger(0);
    //     Runnable task = () -> {
    //         try {
    //             Thread.sleep(100);
    //             counter.incrementAndGet();
    //         } catch (InterruptedException e) {}
    //         finally {
    //             try {
    //                 executor.shutdown();
    //             } catch (InterruptedException e) {}
    //         }
    //     };
    //     List<Runnable> tasks = new ArrayList<>();
    //     for (int i = 0; i < 10; i++) {
    //         tasks.add(task);
    //     }
    //     executor.submitAll(tasks);
    //     assertTrue(heap.isEmpty());
    //     assertTrue(counter.get() < 5);
    // }

}
