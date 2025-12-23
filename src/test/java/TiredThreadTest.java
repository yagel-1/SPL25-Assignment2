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
        double fatigueFactor = 0.5 + Math.random();
        worker = new TiredThread(1, fatigueFactor);
        worker.start();
    }

    @Test
    public void testWorkerInitialState() {
        assertEquals(0.0, worker.getFatigue(), "Initial fatigue should be 0");
        assertFalse(worker.isBusy(), "Worker should not be busy initially");
        assertEquals(0, worker.getTimeUsed(), "Initial time used should be 0");
    }

    @Test
    public void testTimeIdleIncreases() throws InterruptedException {
        long firstCheck = worker.getTimeIdle();
        Thread.sleep(10);
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
    public void testTaskOverflow() {
    Runnable longTask = () -> {
        try { 
            Thread.sleep(500); 
        } 
        catch (InterruptedException e) {
        }
    };
    worker.newTask(longTask);
    assertThrows(IllegalStateException.class, () -> {
        worker.newTask(() -> {});
    }, "Should throw IllegalStateException when worker's handoff queue is full");
}


   @Test
    public void testWorkerRecoversAfterTaskCrash() throws InterruptedException {
        Runnable crashingTask = () -> {
            throw new RuntimeException("Unexpected Crash!");
        };
        worker.newTask(crashingTask);
        Thread.sleep(100);
        assertFalse(worker.isBusy(), "Worker should reset busy status even after a crash");
        assertTrue(worker.isAlive(), "Worker thread should still be alive after a task crash");
        assertDoesNotThrow(() -> worker.newTask(() -> {}), "Worker should be able to accept new tasks");
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
