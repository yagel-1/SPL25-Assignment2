package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        return vector[index];
    }

    public int length() {
        // TODO: return vector length
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        return orientation;
    }

    public void writeLock() {
        // TODO: acquire write lock
    }

    public void writeUnlock() {
        // TODO: release write lock
    }

    public void readLock() {
        // TODO: acquire read lock
    }

    public void readUnlock() {
        // TODO: release read lock
    }

    public void transpose() {
        // TODO: transpose vector
        if (orientation == VectorOrientation.COLUMN_MAJOR){
            orientation = VectorOrientation.ROW_MAJOR;
        }
        else{
            orientation = VectorOrientation.COLUMN_MAJOR;
        }
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        if (this.length() != other.length()){
            throw new IllegalArgumentException("vectors must be in the same size");
        }
        if (this.getOrientation() != other.getOrientation()){
            throw new IllegalArgumentException("vectors must be in the same orientation");
        }
        for (int i=0; i<this.length(); i++){
            this.vector[i] += other.get(i);
        }
    }

    public void negate() {
        // TODO: negate vector
        for (int i=0; i<this.length(); i++){
            this.vector[i] *= -1;
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        if (this.getOrientation() == other.getOrientation()){
            throw new IllegalArgumentException("");
        }
        if (this.length() != other.length()){
            throw new IllegalArgumentException("vectors must be in the same size");
        }
        double sum = 0;
        for (int i=0; i<length(); i++){
            sum = sum + (this.get(i) * other.get(i));
        }
        return sum;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
    }
}
