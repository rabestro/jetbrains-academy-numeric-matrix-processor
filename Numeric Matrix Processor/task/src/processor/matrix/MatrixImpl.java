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
     * Matrix Addition
     * <p>
     * Two matrices must have an equal number of rows and columns to be added.
     * The sum of two matrices A and B will be a matrix which has the same
     * number of rows and columns as do A and B.
     *
     * @param other matrix to be add
     * @return a new matrix that represents sum of first and second matrices
     * @throws IllegalArgumentException if number of rows and columns are not equals
     */

    @Override
    public Matrix add(final Matrix other) {
        if (this.rows != other.getRows() || this.cols != other.getCols()) {
            throw new IllegalArgumentException("the sizes of matrices have to be equal");
        }
        return Matrix.create(rows, cols, i -> this.element(i) + other.element(i));
    }

    /**
     * Multiplication by number
     * <p>
     * Multiply every element of the matrix by a constant.
     *
     * @param constant for matrix multiplication by
     * @return a new matrix that represents multiplication of the matrix by given constant
     */
    @Override
    public Matrix multiply(final double constant) {
        return Matrix.create(rows, cols, i -> element(i) * constant);
    }

    /**
     * Matrix by matrix multiplication
     * <p>
     * Matrix multiplication is a binary operation that produces a matrix from two matrices.
     * For matrix multiplication, the number of columns in the first matrix must be equal to
     * the number of rows in the second matrix. The result matrix, known as the matrix product,
     * has the number of rows of the first and the number of columns of the second matrix.
     *
     * @param other matrix
     * @return the matrix product
     * @throws IllegalArgumentException in case if number of rows for the first matrix is
     *                                  not equals to the number of rows for the second matrix
     */
    @Override
    public Matrix multiply(final Matrix other) {
        if (this.cols != other.getRows()) {
            throw new IllegalArgumentException(
                    "the number of columns for the first matrix should be equal "
                            + "to the number of rows for the second matrix.");
        }
        final IntToDoubleFunction multiplyByMatrix = i -> range(0, this.cols).mapToDouble(col ->
                element(i / other.getCols() * cols + col)
                        * other.element(i % other.getCols()
                        + col * other.getCols())).sum();

        return Matrix.create(this.rows, other.getCols(), multiplyByMatrix);
    }

    /**
     * Matrix Transposition
     * <p>
     * Matrix transposition is an operation in linear algebra that exchanges
     * matrix rows on matrix columns and returns a new matrix as a result.
     *
     * @param mode is transposition type
     * @return a new transposed matrix
     * @throws IllegalArgumentException if matrix is not a square
     */
    @Override
    public Matrix transpose(final Transposition mode) {
        if (cols != rows) {
            throw new IllegalArgumentException("only square matrix can be transposed.");
        }
        final var transpositionFormula = new IntToDoubleFunction[]{
                i -> element(i / rows + i % cols * cols),
                i -> element(cols * (rows - i % cols) - i / rows - 1),
                i -> element(cols - i % cols - 1 + i / rows * cols),
                i -> element(rows * (cols - i / rows - 1) + i % cols)
        }[mode.ordinal()];

        return Matrix.create(rows, cols, transpositionFormula);
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
