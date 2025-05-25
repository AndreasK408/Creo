package matrix; // Oder dein gewähltes Paket

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition; // Zur Invertierung
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

public class MatrixHandler {
    private static final RandomGenerator randomGenerator = new JDKRandomGenerator();

    /**
     * Erzeugt eine zufällige quadratische Matrix der gegebenen Größe,
     * füllt sie mit Werten zwischen 0.0 und 1.0 und invertiert sie.
     *
     * @param size Die Kantenlänge der quadratischen Matrix (muss >= 1 sein).
     * @return Eine Liste von Listen (repräsentiert die invertierte Matrix).
     * @throws IllegalArgumentException wenn size < 1.
     * @throws SingularMatrixException wenn die zufällige Matrix nicht invertierbar ist.
     */
    public static List<List<Double>> invertRandomMatrix(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Matrix size must be greater or equal to 1, but was " + size + ".");
        }
        // Erzeuge eine leere Matrix der Größe size x size
        RealMatrix matrix = new Array2DRowRealMatrix(size, size);
        //fülle die Matrix mit Zufallswerten zwischen 0.0 (inklusive) und 1.0 (exklusive)
        //    (entspricht np.random.random und Rusts Uniform::new(0.0, 1.0))
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix.setEntry(i, j, randomGenerator.nextDouble());
            }
        }
        // Invertiere die Matrix
        //  wirft eine SingularMatrixException, wenn die Matrix nicht invertierbar ist.
        RealMatrix inverseMatrix;
        try {
            inverseMatrix = new LUDecomposition(matrix).getSolver().getInverse();
        } catch (SingularMatrixException e) {
            System.err.println("Warnung: Zufällig generierte Matrix ist singulär und nicht invertierbar (size=" + size + ").");
            throw e;
        }
        List<List<Double>> resultList = new ArrayList<>(size);
        double[][] data = inverseMatrix.getData();
        for (int i = 0; i < size; i++) {
            List<Double> row = new ArrayList<>(size);
            for (int j = 0; j < size; j++) {
                row.add(data[i][j]);
            }
            resultList.add(row);
        }
        return resultList;
    }
}