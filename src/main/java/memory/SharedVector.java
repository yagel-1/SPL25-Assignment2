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
        try{
            readLock();
            return vector[index];
        } finally {
            readUnlock();
        }
    }

    public int length() {
        // TODO: return vector length
        try{
            readLock();
            return vector.length;
        } finally {
            readUnlock();
        }
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        try{
            readLock();
            return orientation;
        } finally {
            readUnlock();
        }
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
        try{
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
        } finally {
            writeUnlock();
            other.readUnlock();
        }
    }

    public void negate() {
        // TODO: negate vector
        try{
            writeLock();
            for (int i=0; i<this.length(); i++){
                this.vector[i] *= -1;
            }
        } finally {
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        try{
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
            return sum;
        } finally {
            readUnlock();
            other.readUnlock();
        }
        
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        try{
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
                matrix.get(i).readLock();
                result[i] = this.dot(matrix.get(i));
                matrix.get(i).readUnlock();
            }
            this.vector = result;
        } finally {
            writeUnlock();
        }
    }
}

