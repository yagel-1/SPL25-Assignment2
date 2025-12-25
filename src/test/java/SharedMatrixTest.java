import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import memory.SharedMatrix;
import memory.SharedVector;
import memory.VectorOrientation;

public class SharedMatrixTest {
    double[][] matrix1 = {
        {1, 2},
        {3, 4},
        {5, 6}
    };
    double[][] matrix2 = {
        {7, 0, -9},
        {3, -11, 10}
    };

    double[][] matrix3 = {
        {1, 0, -1}
    };

    double[][] matrix4 = {
        {2},
        {4},
        {6}
    };

    double[][] matrix5 = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9}
    };

    double[][] single = {
        {42.5}
    };

    double[][] zeros = {
        {0, 0, 0},
        {0, 0, 0}
    };

    double[][] wideMatrix = {
        {10, 20, 30, 40}
    };

    double[][] tallMatrix = {
        {1},
        {2},
        {3},
        {4}
    };

    double[][] empty = {};

    double[][] squareMatrix = {
        {1, 3},
        {2, 4}
    };

    SharedMatrix sm1;
    SharedMatrix sm2;
    SharedMatrix sm3;
    SharedMatrix sm4;
    SharedMatrix sm5;

    @BeforeEach
    public void setUp() throws Exception {
        sm1 = new SharedMatrix(matrix1);
        sm2 = new SharedMatrix(matrix2);
        sm3 = new SharedMatrix(matrix3);
        sm4 = new SharedMatrix(matrix4);
        sm5 = new SharedMatrix(matrix5);
    }

    @Test
    public void testReadRowMajor(){
        double[][] result = {
            {1, 2},
            {3, 4},
            {5, 6}
        };
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[0].length; j++) {
                assertEquals(matrix1[i][j], result[i][j]);
            }
        }

        sm4.loadRowMajor(matrix4);
        double[][] result2 = {
            {2},
            {4},
            {6}
        };
        for (int i = 0; i < matrix4.length; i++) {
            for (int j = 0; j < matrix4[0].length; j++) {
                assertEquals(matrix4[i][j], result2[i][j]);
            }
        }
    }

    @Test
    public void testLoadRowMajor(){
        double[][] newMatrix = {
            {9, 8, 7},
            {6, 5, 4}
        };
        sm2.loadRowMajor(newMatrix);
        double[][] result = sm2.readRowMajor();
        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[0].length; j++) {
                assertEquals(newMatrix[i][j], result[i][j]);
            }
        }

        double[][] newMatrix2 = {
            {3, 2},
            {1, 0},
            {-1, -2}
        };
        sm3.loadRowMajor(newMatrix2);
        double[][] result2 = sm3.readRowMajor();
        for (int i = 0; i < newMatrix2.length; i++) {
            for (int j = 0; j < newMatrix2[0].length; j++) {
                assertEquals(newMatrix2[i][j], result2[i][j]);
            }
        }
    }

    @Test
    public void testLoadColumnMajor(){
        double[][] newMatrix = {
            {1, 4},
            {2, 5},
            {3, 6}
        };
        sm1.loadColumnMajor(newMatrix);
        for (int i = 0; i < newMatrix[0].length; i++) {
            for (int j = 0; j < newMatrix.length; j++) {
                assertEquals(newMatrix[j][i], sm1.get(i).get(j));
            }
        }

        double[][] newMatrix2 = {
            {7, 3, 1},
            {8, -11, 0}
        };
        sm2.loadColumnMajor(newMatrix2);
        for (int i = 0; i < newMatrix2[0].length; i++) {
            for (int j = 0; j < newMatrix2.length; j++) {
                assertEquals(newMatrix2[j][i], sm2.get(i).get(j));
            }
        }

        sm3.loadColumnMajor(sm4.readRowMajor());
        double[][] result3 = {
            {2},
            {4},
            {6}
        };
        for (int i = 0; i < result3[0].length; i++) {
            for (int j = 0; j < result3.length; j++) {
                assertEquals(result3[j][i], sm3.get(i).get(j));
            }
        }
    }

    @Test
    public void testSingleElementMatrix() {
        SharedMatrix smSingle = new SharedMatrix(new double[][]{{1, 2}, {3, 4}});
        smSingle.loadRowMajor(single);
        
        double[][] result = smSingle.readRowMajor();
        assertEquals(1, result.length);
        assertEquals(1, result[0].length);
        assertEquals(42.5, result[0][0]);
    }

    @Test
    public void testZeroMatrix() {
        sm1.loadRowMajor(zeros);
        
        double[][] result = sm1.readRowMajor();
        for (int i = 0; i < zeros.length; i++) {
            for (int j = 0; j < zeros[0].length; j++) {
                assertEquals(0.0, result[i][j]);
            }
        }
    }

    @Test
    public void testResizingMatrixDimensions() {
        sm1.loadRowMajor(wideMatrix);
        double[][] resultWide = sm1.readRowMajor();
        
        assertEquals(1, resultWide.length);
        assertEquals(4, resultWide[0].length);
        assertEquals(40, resultWide[0][3]);
    
        sm1.loadRowMajor(tallMatrix);
        double[][] resultTall = sm1.readRowMajor();
        
        assertEquals(4, resultTall.length);
        assertEquals(1, resultTall[0].length);
        assertEquals(4, resultTall[3][0]);
    }

    @Test
    public void testEmptyMatrix() {
        try {
            SharedMatrix smEmpty = new SharedMatrix(empty);
            double[][] result = smEmpty.readRowMajor();
            assertEquals(0, result.length);
        } catch (Exception e) {
            fail("Creating an empty matrix should not throw an exception.");
        }
    }

    @Test
    public void testLoadColumnMajorResizing() {
        sm4.loadColumnMajor(squareMatrix);
        
        for (int i = 0; i < squareMatrix[0].length; i++) {
            for (int j = 0; j < squareMatrix.length; j++) {
                 assertEquals(squareMatrix[j][i], sm4.get(i).get(j));
            }
        }
    }
}
