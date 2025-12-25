import spl.lae.LinearAlgebraEngine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import parser.*;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class LinearAlgebraEngineTest {

    private LinearAlgebraEngine lae;

    @Test
    void testComplexExpression() {
        double[][] matA = {{1, 2}, {3, 4}};
        double[][] matB = {{0, 1}, {1, 0}};
        double[][] matC = {{1, 2}, {3, 4}}; 

        double[][] expected = {{7, 15}, {12, 28}};

        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeB = new ComputationNode(matB);
        ComputationNode nodeC = new ComputationNode(matC);

        ComputationNode addNode = new ComputationNode(ComputationNodeType.ADD, List.of(nodeA, nodeB));
        ComputationNode transNode = new ComputationNode(ComputationNodeType.TRANSPOSE, List.of(nodeC));
        
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(addNode, transNode));

        lae = new LinearAlgebraEngine(4);
        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(expected, res);
    }

    @Test
    void testDoubleNegation() {
        // בדיקה לוגית: -(-A) == A
        double[][] matA = {{5, -3}, {0, 10}};
        double[][] expected = {{5, -3}, {0, 10}};
        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode neg1 = new ComputationNode(ComputationNodeType.NEGATE, List.of(nodeA));
        ComputationNode root = new ComputationNode(ComputationNodeType.NEGATE, List.of(neg1));

        lae = new LinearAlgebraEngine(2);
        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(expected, res);
    }

    @Test
    void testDoubleTranspose() {
        double[][] matA = {{1, 2, 3}, {4, 5, 6}};
        
        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode trans1 = new ComputationNode(ComputationNodeType.TRANSPOSE, List.of(nodeA));
        ComputationNode root = new ComputationNode(ComputationNodeType.TRANSPOSE, List.of(trans1));

        lae = new LinearAlgebraEngine(2);
        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(matA, res);
    }

    @Test
    void testIdentityMultiplication() {
        double[][] matA = {{1, 2}, {3, 4}};
        double[][] identity = {{1, 0}, {0, 1}};
        
        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeId = new ComputationNode(identity);
        
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(nodeA, nodeId));

        lae = new LinearAlgebraEngine(2);
        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(matA, res);
    }

    @Test
    void testDeepTree() {
        ComputationNode n1 = new ComputationNode(new double[][]{{1}});
        ComputationNode n2 = new ComputationNode(new double[][]{{1}});
        ComputationNode sum1 = new ComputationNode(ComputationNodeType.ADD, List.of(n1, n2));
        
        ComputationNode n3 = new ComputationNode(new double[][]{{1}});
        ComputationNode sum2 = new ComputationNode(ComputationNodeType.ADD, List.of(sum1, n3));
        
        ComputationNode n4 = new ComputationNode(new double[][]{{1}});
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, List.of(sum2, n4));

        lae = new LinearAlgebraEngine(2);
        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(new double[][]{{4}}, res);
    }

    @Test
    void testMismatchedAddition() {
        double[][] matA = {{1, 2}, {3, 4}};
        double[][] matB = {{1, 2, 3}, {4, 5, 6}};
        
        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeB = new ComputationNode(matB);
        
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, List.of(nodeA, nodeB));

        lae = new LinearAlgebraEngine(2);
        
        assertThrows(IllegalArgumentException.class, () -> {
            lae.run(root);
        });
    }
    
    @Test
    void testMismatchedMultiplication() {
        double[][] matA = {{1, 2}, {3, 4}};
        double[][] matB = {{1, 2}, {3, 4}, {5, 6}};
        
        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeB = new ComputationNode(matB);
        
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(nodeA, nodeB));

        lae = new LinearAlgebraEngine(2);
        
        assertThrows(IllegalArgumentException.class, () -> {
            lae.run(root);
        });
    }

    @Test
    void testSingleThreadedExecution() {
        double[][] matA = {{2, 0}, {1, 3}};
        double[][] matB = {{1, 4}, {2, 5}};
        double[][] expected = {{2, 8}, {7, 19}};

        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeB = new ComputationNode(matB);
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(nodeA, nodeB));

        lae = new LinearAlgebraEngine(1);
        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(expected, res);
    }

    @Test
    void testAdditionDimensionMismatch() {
        ComputationNode a = new ComputationNode(new double[][]{{1, 2}, {3, 4}});
        ComputationNode b = new ComputationNode(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, List.of(a, b));

        lae = new LinearAlgebraEngine(1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            lae.run(root);
        }, "Addition of different sized matrices should throw exception");
    }

    @Test
    void testMultiplicationDimensionMismatch() {
        double[][] matA = {{1, 2, 3}, {4, 5, 6}}; 
        double[][] matB = {{1, 2}, {3, 4}};

        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeB = new ComputationNode(matB);
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(nodeA, nodeB));

        lae = new LinearAlgebraEngine(1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            lae.run(root);
        }, "Multiplication with incompatible dimensions should throw exception");
    }

    @Test
    void testMissingChildrenForBinaryOp() {
        ComputationNode a = new ComputationNode(new double[][]{{1}});
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, List.of(a));

        lae = new LinearAlgebraEngine(1);
        assertThrows(Exception.class, () -> {
            lae.run(root);
        });
    }

    @Test
    void testLargeMatrixAddition() {
        int size = 500;
        double[][] matA = new double[size][size];
        double[][] matB = new double[size][size];
        double[][] expected = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matA[i][j] = 1.0;
                matB[i][j] = 2.0;
                expected[i][j] = 3.0;
            }
        }

        ComputationNode nodeA = new ComputationNode(matA);
        ComputationNode nodeB = new ComputationNode(matB);
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, List.of(nodeA, nodeB));

        lae = new LinearAlgebraEngine(50); 
        
        long start = System.currentTimeMillis();
        double[][] res = lae.run(root).getMatrix();
        long end = System.currentTimeMillis();

        System.out.println("Large Addition Time: " + (end - start) + "ms");

        assertArrayEquals(expected, res);
    }

    @Test
    void testMultiplyLargeMatrices() {
        int size = 500;
        double[][] matA = new double[size][size];
        double[][] matB = new double[size][size];
        double[][] expected = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matA[i][j] = i + 1;
                matB[i][j] = j + 1;
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                expected[i][j] = 0;
                for (int k = 0; k < size; k++) {
                    expected[i][j] += matA[i][k] * matB[k][j];
                }
            }
        }

        ComputationNode a = new ComputationNode(matA);
        ComputationNode b = new ComputationNode(matB);
        ComputationNode root = new ComputationNode(
                ComputationNodeType.MULTIPLY, List.of(a,b)
        );

        LinearAlgebraEngine lae = new LinearAlgebraEngine(100);
        double[][] res = lae.run(root).getMatrix();

        assertArrayEquals(expected, res);
    }

    @Test
    void testDeepTreeWithLargeMatrices() {
        int size = 100;
        double[][] matA = new double[size][size];
        
        // אתחול המטריצה המקורית
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                matA[i][j] = 1.0;
            }
        }

        ComputationNode n1 = new ComputationNode(deepCopy(matA));
        ComputationNode n2 = new ComputationNode(deepCopy(matA));
        
        ComputationNode sumLeft = new ComputationNode(ComputationNodeType.ADD, List.of(n1, n2)); 

        ComputationNode n3 = new ComputationNode(deepCopy(matA));
        ComputationNode n4 = new ComputationNode(deepCopy(matA));
        ComputationNode sumRight = new ComputationNode(ComputationNodeType.ADD, List.of(n3, n4)); 

        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, List.of(sumLeft, sumRight)); 

        lae = new LinearAlgebraEngine(4);
        double[][] res = lae.run(root).getMatrix();

        assertEquals(4.0, res[0][0], 0.0001);
        assertEquals(4.0, res[size-1][size-1], 0.0001);
    }

    private double[][] deepCopy(double[][] matrix) {
        double[][] newMatrix = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            newMatrix[i] = matrix[i].clone(); 
        }
        return newMatrix;
    }
}