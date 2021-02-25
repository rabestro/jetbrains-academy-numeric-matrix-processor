package processor.matrix;

import java.util.Optional;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

public final class MatrixImpl implements Matrix {
    private final int rows;
    private final int cols;
    private final double[] elements;

    MatrixImpl(final int rows, final int cols, final double[] elements) {
        if (elements.length != rows * cols) {
            throw new IllegalArgumentException(
                    "the number of cells is not equals to the product of rows and columns");
        }
        this.rows = rows;
        this.cols = cols;
        this.elements = elements;
    }

    /**
     * Determinant of the matrix
     * <p>
     * The determinant is a scalar value that can be computed from the elements of a square matrix
     * and encodes certain properties of the linear transformation described by the matrix.
     *
     * @return the determinant of the matrix
     * @throws IllegalArgumentException if matrix is not a square
     */
    @Override
    public double determinant() {
        if (cols != rows) {
            throw new IllegalArgumentException("the determinant is defined only for a square matrix.");
        }
        if (rows == 1) {
            return element(0);
        }
        if (rows == 2) {
            return element(0) * element(3) - element(1) * element(2);
        }
        return range(0, rows).mapToDouble(i -> element(i) * cofactor(i)).sum();
    }

    /**
     * Inverse matrix
     * <p>
     * Inverse matrix A^ is the matrix, the product of which to original matrix A is equal to
     * the identity matrix. The identity matrix is the matrix in which all elements of the main
     * diagonal are ones and other are zeros. Inverse matrix can’t be found if det(A) equals zero.
     *
     * @return an optional of matrix that represents inverse matrix if det(A) not equals zero.
     */
    @Override
    public Optional<Matrix> inverse() {
        final double det = determinant();
        return det == 0.0 ? Optional.empty() : Optional.of(this.cofactor().transpose().multiply(1 / det));
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public double element(final int index) {
        return elements[index];
    }

    private double minor(final int cell) {
        final int size = rows - 1;
        final IntToDoubleFunction calculateMinor = i ->
                elements[rows * ((i / size < cell / rows ? 0 : 1) + i / size)
                        + i % size + (i % size < cell % cols ? 0 : 1)];

        return Matrix.create(size, size, calculateMinor).determinant();
    }

    private double cofactor(final int cell) {
        return ((cell / rows + cell % cols) % 2 == 0 ? 1 : -1) * minor(cell);
    }

    private Matrix cofactor() {
        return Matrix.create(rows, cols, this::cofactor);
    }

    @Override
    public String toString() {
        return range(0, elements.length)
                .mapToObj(i -> String.format((i + 1) % cols == 0 ? "%9.2f%n" : "%9.2f ", elements[i]))
                .collect(Collectors.joining());
    }

}
