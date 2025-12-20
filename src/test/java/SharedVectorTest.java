import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import memory.SharedMatrix;
import memory.SharedVector;
import memory.VectorOrientation;

public class SharedVectorTest {
    double[] vector1 = {1, 2, 3};
    double[] vector2 = {4, 5, 6};
    double[] vector3 = {7, 8, 9};
    double[] vector4 = {0, 0, 0, 0, 0};
    double[] vector5 = {-2, 0};

    double[][] matrix1 = {
        {1, 2},
        {3, 4},
        {5, 6}
    };
    double[][] matrix2 = {
        {7, 8, -9},
        {10, 11, 12}
    };

    SharedVector sv1;
    SharedVector sv2;
    SharedVector sv3;
    SharedVector sv4;
    SharedVector sv5;

    SharedMatrix sm1;
    SharedMatrix sm2;

    @BeforeEach
    public void setUp() throws Exception {
        sv1 = new SharedVector(vector1, VectorOrientation.ROW_MAJOR);
        sv2 = new SharedVector(vector2, VectorOrientation.COLUMN_MAJOR);
        sv3 = new SharedVector(vector3, VectorOrientation.ROW_MAJOR);
        sv4 = new SharedVector(vector4, VectorOrientation.COLUMN_MAJOR);
        sv5 = new SharedVector(vector5, VectorOrientation.ROW_MAJOR);

        sm1 = new SharedMatrix(matrix1);
        sm2 = new SharedMatrix(matrix2);
    }

    @Test
    public void testGet(){
        double result = sv1.get(0);
        assertEquals(1, result, 0.0001);

        assertThrows(ArrayIndexOutOfBoundsException.class, ()->{sv1.get(-1);});

    }

    @Test
    public void testLength(){
        int result = sv1.length();
        assertEquals(3, result);        
    }

    @Test
    public void testGetOrientation(){
        VectorOrientation result = sv1.getOrientation();
        assertEquals(VectorOrientation.ROW_MAJOR, result);        
    }

    @Test
    public void testTranspose(){
        sv1.transpose();
        assertTrue(sv1.getOrientation() == VectorOrientation.COLUMN_MAJOR);
        sv2.transpose();
        assertTrue(sv2.getOrientation() == VectorOrientation.ROW_MAJOR);

    }

    @Test
    public void testAdd() {
        sv1.add(sv3);
        double[] expected = {8, 10, 12};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], sv1.get(i));
        }

        assertThrows(IllegalArgumentException.class, ()->{sv1.add(sv2);});
    }


    @Test
    public void testNegate() {
        sv1.negate();
        double[] expected = {-1, -2, -3};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], sv1.get(i));
        }
    }

    @Test
    public void testDot() {
        double result = sv1.dot(sv2);
        assertEquals(32, result);

        assertThrows(IllegalArgumentException.class, ()->{sv1.dot(sv3);});
        assertThrows(IllegalArgumentException.class, ()->{sv1.dot(sv4);});

    }

    @Test
    public void testVecMatMul() {
        sm1.loadColumnMajor(sm1.readRowMajor());
        sv1.vecMatMul(sm1);
        double[] expected = {22, 28};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], sv1.get(i));
        }
        sm2.loadColumnMajor(sm2.readRowMajor());
        sv5.vecMatMul(sm2);
        double[] expected2 = { -14, -16, 18};
        for (int i = 0; i < expected2.length; i++) {
            assertEquals(expected2[i], sv5.get(i));
        }

        assertThrows(IllegalArgumentException.class, ()->{sv2.vecMatMul(sm1);});
    }
}
