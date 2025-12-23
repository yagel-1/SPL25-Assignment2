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
        try{
            TiredThread worker = idleMinHeap.take();
            inFlight.incrementAndGet();
            Runnable newTask = ()->{
                try{
                    task.run();
                }
                finally{
                    if (worker.isAlive()){
                        idleMinHeap.put(worker);
                    }
                    inFlight.decrementAndGet();
                    synchronized (this){
                        notifyAll();
                    }
                }
            };
            worker.newTask(newTask);            
        } catch (InterruptedException ex){
            //Thread.currentThread().interrupt();
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable task : tasks){
            submit(task);
        }
        try{
            synchronized (this){
                while (inFlight.get() > 0){
                    wait();
                }
            }
        }
        catch (InterruptedException ex){
            //Thread.currentThread().interrupt();
        }
    }

    public void shutdown() throws InterruptedException {
        // TODO
        for (TiredThread worker : workers){
            worker.shutdown();
        }
        idleMinHeap.clear();
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        StringBuilder report = new StringBuilder();
        double avgFatigue = 0.0;
        for (TiredThread worker : workers){
            avgFatigue += worker.getFatigue();
        }
        avgFatigue = avgFatigue / workers.length;
        for (TiredThread worker : workers){
            report.append("worker: "+ worker.getWorkerId()+ ", time used: "+ worker.getTimeUsed()+
                ", time idle: "+worker.getTimeIdle()+", fatigue: "+worker.getFatigue()+
                ", Fatigue deviation: "+(worker.getFatigue()-avgFatigue)+"\n");
        }
        return report.toString();
    }
}
