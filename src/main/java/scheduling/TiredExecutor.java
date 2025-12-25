package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads];
        for (int i=0; i<numThreads; i++){
            double fatigueFactor = 0.5 + Math.random();
            workers[i] = new TiredThread(i, fatigueFactor);
            idleMinHeap.put(workers[i]);
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        // TODO
        if (task == null){
            throw new IllegalArgumentException("task is null");
        }
        if (inFlight.get() < 0){
            throw new IllegalStateException("Executor is shutdown");
        }
        try{
            TiredThread worker = idleMinHeap.take();
            inFlight.incrementAndGet();
            Runnable newTask = ()->{
                try{
                    task.run();
                }
                finally{
                    idleMinHeap.put(worker);
                    inFlight.decrementAndGet();
                    synchronized (this){
                        notifyAll();
                    }
                }
            };
            try {
                worker.newTask(newTask);
            } catch (IllegalStateException e) {
                inFlight.decrementAndGet();
                synchronized (this){
                    notifyAll();
                }
            }            
        } catch (InterruptedException ex){}
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable task : tasks){
            if (inFlight.get() < 0){
                break;
            }
            submit(task);
        }
        try{
            synchronized (this){
                while (inFlight.get() > 0){
                    wait();
                }
            }
        }
        catch (InterruptedException ex){}
    }

    public void shutdown() throws InterruptedException {
        // TODO
        inFlight.set(-1);
        for (TiredThread worker : workers){
            worker.shutdown();
        }
        idleMinHeap.clear();
        synchronized (this) {
            notifyAll();
        }
        for (TiredThread worker : workers) {
            worker.join(); 
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        StringBuilder report = new StringBuilder();
        double avgFatigue = 0.0;
        for (TiredThread worker : workers){
            avgFatigue += worker.getFatigue();
        }
        avgFatigue = avgFatigue / workers.length;
        double totalSquaredDeviation = 0.0;

        for (TiredThread worker : workers){
            double fatigue = worker.getFatigue();
            double deviation = fatigue - avgFatigue;
            
            totalSquaredDeviation += (deviation * deviation);

            report.append("worker: ").append(worker.getWorkerId())
                .append(", time used: ").append(worker.getTimeUsed())
                .append(", time idle: ").append(worker.getTimeIdle())
                .append(", fatigue: ").append(fatigue)
                .append(", Deviation: ").append(deviation)
                .append("\n");
        }
        report.append("\n--- Fairness Score (Sum of Squared Deviations) ---\n");
        report.append("Score: ").append(totalSquaredDeviation);
        return report.toString();
    }
}
