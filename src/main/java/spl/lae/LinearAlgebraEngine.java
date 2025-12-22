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
        return null;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        List<ComputationNode> children = node.getChildren();
        switch (node.getNodeType()) {
            case ComputationNodeType.ADD:
                {
                    leftMatrix.loadRowMajor(children.getFirst().getMatrix());
                    rightMatrix.loadRowMajor(children.getLast().getMatrix());
                    executor.submitAll(createAddTasks());
                    break;
                }
            case ComputationNodeType.MULTIPLY:
                {
                    leftMatrix.loadRowMajor(children.getFirst().getMatrix());
                    rightMatrix.loadColumnMajor(children.getLast().getMatrix());
                    executor.submitAll(createMultiplyTasks());
                    break;
                }
                
            case ComputationNodeType.NEGATE:
                {
                    leftMatrix.loadRowMajor(children.getFirst().getMatrix());
                    executor.submitAll(createNegateTasks());
                    break;
                }
                
            case ComputationNodeType.TRANSPOSE:
                {
                    leftMatrix.loadRowMajor(children.getFirst().getMatrix());
                    executor.submitAll(createTransposeTasks());
                    break;
                }
                
            default:
                throw new IllegalArgumentException("Unknown operator: " + node.getNodeType());
        }

    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        return null;
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
        return null;
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
        return null;
    }
}
