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
        readLock();
        double ret = vector[index];
        readUnlock();
        return ret;
    }

    public int length() {
        // TODO: return vector length
        readLock();
        int ret =vector.length;
        readUnlock();
        return ret;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        readLock();
        VectorOrientation ret = orientation;
        readUnlock();
        return ret;
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        writeLock();
        if (orientation == VectorOrientation.COLUMN_MAJOR){
            orientation = VectorOrientation.ROW_MAJOR;
        }
        else{
            orientation = VectorOrientation.COLUMN_MAJOR;
        }
        writeUnlock();
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        writeLock();
        other.readLock();
        if (this.length() != other.length()){
            throw new IllegalArgumentException("vectors must be in the same size");
        }
        if (this.getOrientation() != other.getOrientation()){
            throw new IllegalArgumentException("vectors must be in the same orientation");
        }
        for (int i=0; i<this.length(); i++){
            this.vector[i] += other.get(i);
        }
        writeUnlock();
        other.readUnlock();
    }

    public void negate() {
        // TODO: negate vector
        writeLock();
        for (int i=0; i<this.length(); i++){
            this.vector[i] *= -1;
        }
        writeUnlock();
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        readLock();
        other.readLock();
        if (this.getOrientation() != VectorOrientation.ROW_MAJOR) {
            throw new IllegalArgumentException("vector must be orianted in row");
        }
        if (other.getOrientation() != VectorOrientation.COLUMN_MAJOR) {
            throw new IllegalArgumentException("other must be orianted in column");

        }
        if (this.length() != other.length()){
            throw new IllegalArgumentException("vectors must be in the same size");
        }
        double sum = 0;
        for (int i=0; i<length(); i++){
            sum = sum + (this.get(i) * other.get(i));
        }
        readUnlock();
        other.readUnlock();
        return sum;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        writeLock();
        if (this.length() != matrix.get(0).length()) {
            throw new IllegalArgumentException("Incompatible dimensions for vector-matrix multiplication.");
        }
        if (this.getOrientation() != VectorOrientation.ROW_MAJOR) {
            throw new IllegalArgumentException("vector must be orianted in row");
        }
        if (matrix.getOrientation() != VectorOrientation.COLUMN_MAJOR) {
            throw new IllegalArgumentException("matrix must be orianted in columns");
        }
        double[] result = new double[matrix.length()];
        for(int i=0 ; i < matrix.length() ; i++){
            result[i] = this.dot(matrix.get(i));
        }
        this.vector = result;
        writeUnlock();
    }

    //helper function
    public double[] getVector(){
        try{
            readLock();
            return vector;
        }
        finally{
            readUnlock();
        }
    }
}

