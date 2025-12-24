import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

import scheduling.TiredThread;

import static org.junit.jupiter.api.Assertions.*;

public class TiredThreadTest {
    TiredThread worker;

    @BeforeEach
    public void setUp() {
        double fatigueFactor = 0.0;
        worker = new TiredThread(1, fatigueFactor);
        worker.start();
    }

    @AfterEach
    public void tearDown() {
        worker.shutdown();
    }

    @Test
    public void testWorkerInitialState() {
        assertEquals(0.0, worker.getFatigue(), "Initial fatigue should be 0");
        assertFalse(worker.isBusy(), "Worker should not be busy initially");
        assertEquals(0, worker.getTimeUsed(), "Initial time used should be 0");
    }

    @Test
    public void testTimeIdleIncreases() throws InterruptedException {
        Runnable dummyTask = () -> {};
        worker.newTask(dummyTask);
        long firstCheck = worker.getTimeIdle();
        Thread.sleep(100);
        long secondCheck = worker.getTimeIdle();
        assertTrue(secondCheck > firstCheck, "Idle time should increase as time passes");
    }

    @Test
    public void testNewTaskAssignment() {
        Runnable dummyTask = () -> {};
        assertDoesNotThrow(() -> worker.newTask(dummyTask));
    }

    @Test
    public void testShutdown() {
        TiredThread test = new TiredThread(99, 1.0);
        test.start();
        test.shutdown();
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            
        }
        
        assertTrue(test.isAlive() == false, "Thread shouldn't be alive after shutdown");
    }

    @Test
    public void testGracefulShutdownWithLongTask() throws InterruptedException {
        Runnable mediumTask = () -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        };
        worker.newTask(mediumTask);
        Thread shutdownThread = new Thread(() -> worker.shutdown());
        shutdownThread.start();
        worker.join(1000); 
        assertFalse(worker.isAlive(), "Worker should finish current task and then shutdown");
    }
}
