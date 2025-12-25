package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        computationRoot.associativeNesting();
        ComputationNode curr = computationRoot.findResolvable();
        try{
            while (curr != null) {
                loadAndCompute(curr);
                curr.resolve(leftMatrix.readRowMajor());
                curr = computationRoot.findResolvable();
            }
        }
        finally{
            try{
                executor.shutdown();
            }
            catch (InterruptedException e){}
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        List<ComputationNode> children = node.getChildren();
        switch (node.getNodeType()) {
            case ComputationNodeType.ADD:
                {
                    if (children.get(0).getMatrix().length != children.get(1).getMatrix().length ||
                        children.get(0).getMatrix()[0].length != children.get(1).getMatrix()[0].length ){
                        throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
                    }
                    leftMatrix.loadRowMajor(children.get(0).getMatrix());
                    rightMatrix.loadRowMajor(children.get(1).getMatrix());
                    executor.submitAll(createAddTasks());
                    break;
                }
            case ComputationNodeType.MULTIPLY:
                {
                    if (children.get(0).getMatrix()[0].length != children.get(1).getMatrix().length){
                        throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
                    }
                    leftMatrix.loadRowMajor(children.get(0).getMatrix());
                    rightMatrix.loadColumnMajor(children.get(1).getMatrix());
                    executor.submitAll(createMultiplyTasks());
                    break;
                }
                
            case ComputationNodeType.NEGATE:
                {
                    leftMatrix.loadRowMajor(children.get(0).getMatrix());
                    executor.submitAll(createNegateTasks());
                    break;
                }
                
            case ComputationNodeType.TRANSPOSE:
                {
                    leftMatrix.loadRowMajor(children.get(0).getMatrix());
                    executor.submitAll(createTransposeTasks());
                    break;
                }
                
            default:
                throw new IllegalArgumentException("Unknown operator: " + node.getNodeType());
        }

    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> tasks = new ArrayList<>();
        for (int i=0; i<leftMatrix.length(); i++){
            int rowIndex = i;
            tasks.add(()->{
                leftMatrix.get(rowIndex).add(rightMatrix.get(rowIndex));
            });
        }
        return tasks;

    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        List<Runnable> tasks = new ArrayList<>();
        for (int i=0; i<leftMatrix.length(); i++){
            int rowIndex = i;
            tasks.add(()->{
                leftMatrix.get(rowIndex).vecMatMul(rightMatrix);
            });
        }
        return tasks;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        List<Runnable> tasks = new ArrayList<>();
        for (int i=0; i<leftMatrix.length(); i++){
            int rowIndex = i;
            tasks.add(()->{
                leftMatrix.get(rowIndex).negate();
            });
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        List<Runnable> tasks = new ArrayList<>();
        for (int i=0; i<leftMatrix.length(); i++){
            int rowIndex = i;
            tasks.add(()->{
                leftMatrix.get(rowIndex).transpose();;
            });
        }
        return tasks;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
