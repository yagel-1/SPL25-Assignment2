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
}
