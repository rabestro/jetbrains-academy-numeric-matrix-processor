package processor.matrix;

import java.util.Objects;
import java.util.Optional;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

public interface Matrix {

    /**
     * Gets an element of the matrix by index
     * <p>
     * The index of elements starts from zero like on the scheme:
     * <p>
     * ( 0, 1, 2 )
     * ( 3, 4, 5 )
     * ( 6, 7, 8 )
     *
     * @param index is an element's index
     * @return an element of matrix
     */
    double element(final int index);

    /**
     * Gets an element of the matrix by row and column
     * <p>
     * The row and col of elements starts from zero like on the scheme:
     * <p>
     * [ (0,0) (0,1) (0,2) ]
     * [ (1,0) (1,1) (1,2) ]
     * [ (2,0) (2,1) (2,2) ]
     *
     * @param row is a row where the element is located
     * @param col is a column where the element is located
     * @return an element of matrix
     * @throws IndexOutOfBoundsException if parameters is out of range
     */
    default double element(final int row, final int col) {
        Objects.checkIndex(row, rows());
        Objects.checkIndex(col, cols());
        return element(row * cols() + col);
    }

    /**
     * Rows number
     *
     * @return number of rows in the matrix
     */
    int rows();

    /**
     * Columns number
     *
     * @return number of columns in the matrix
     */
    int cols();

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
    default Matrix add(final Matrix other) {
        requireSizeEquals(other);
        return Matrix.create(rows(), cols(), i -> this.element(i) + other.element(i));
    }

    /**
     * Multiplication by number
     * <p>
     * Multiply every element of the matrix by a constant.
     *
     * @param constant for matrix multiplication by
     * @return a new matrix that represents multiplication of the matrix by given constant
     */
    default Matrix multiply(final double constant) {
        return Matrix.create(rows(), cols(), i -> element(i) * constant);
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
    default Matrix multiply(final Matrix other) {
        requireColsEqualRows(other);
        final IntToDoubleFunction multiplyByMatrix = i -> range(0, this.cols())
                .mapToDouble(col -> element(i / other.cols() * cols() + col)
                        * other.element(i % other.cols() + col * other.cols())).sum();

        return Matrix.create(this.rows(), other.cols(), multiplyByMatrix);
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
    default Matrix transpose(final Transposition mode) {
        requireSquareMatrix();
        return Matrix.create(rows(), cols(), mode.getFormula(this));
    }

    default Matrix transpose() {
        return transpose(Transposition.MAIN_DIAGONAL);
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
    default double determinant() {
        requireSquareMatrix();
        if (rows() == 1) {
            return element(0);
        }
        return range(0, rows()).mapToDouble(i -> element(i) * cofactor(i)).sum();
    }

    private double minor(final int index) {
        final int size = rows() - 1;
        final IntToDoubleFunction calculateMinor = i ->
                element(rows() * ((i / size < index / rows() ? 0 : 1) + i / size)
                        + i % size + (i % size < index % cols() ? 0 : 1));

        return Matrix.create(size, size, calculateMinor).determinant();
    }

    private double cofactor(final int index) {
        return ((index / rows() + index % cols()) % 2 == 0 ? 1 : -1) * minor(index);
    }

    private Matrix cofactor() {
        return Matrix.create(rows(), cols(), this::cofactor);
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
    default Optional<Matrix> inverse() {
        final double det = determinant();
        return det == 0.0 ? Optional.empty() : Optional.of(this.cofactor().transpose().multiply(1 / det));
    }


    /**
     * Create and return the Matrix.
     *
     * @param rows number of rows in the Matrix
     * @param cols number of columns in the Matrix
     * @param function to calculate elements according to an index
     * @return Matrix with given rows, cols and function
     */
    static Matrix create(final int rows, final int cols, final IntToDoubleFunction function) {
        return Matrix.create(rows, cols, range(0, rows * cols).mapToDouble(function).toArray());
    }

    /**
     * Create and return the Identity Matrix.
     *
     * @param size number of rows and columns in the Matrix
     * @return Identity Matrix with given size
     */
    static Matrix createIdentityMatrix(final int size) {
        return Matrix.create(size, size, i -> i % (size + 1) == 0 ? 1 : 0);
    }

    /**
     * Create and return the Matrix.
     * <p>
     * The method creates the concrete implementation of Matrix interface.
     *
     * @param rows     number of rows
     * @param cols     number of columns
     * @param elements array of elements
     * @return Matrix with given rows, cols and elements
     * @throws IllegalArgumentException if (rows * cols) not equals to number of elements in array
     */
    static Matrix create(final int rows, final int cols, final double[] elements) {
        requireEquals(elements.length, rows * cols);
        return new Matrix() {

            @Override
            public double element(int index) {
                return elements[index];
            }

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public String toString() {
                return range(0, elements.length)
                        .mapToObj(i -> String.format((i + 1) % cols == 0 ? "%9.2f%n" : "%9.2f ", elements[i]))
                        .collect(Collectors.joining());
            }
        };
    }

    private static void requireEquals(final int cells, final int product) {
        if (cells != product) {
            throw new IllegalArgumentException(
                    "the number of cells is not equals to the product of rows and columns");
        }
    }

    private void requireSizeEquals(final Matrix other) {
        if (this.rows() != other.rows() || this.cols() != other.cols()) {
            throw new IllegalArgumentException("the sizes of matrices are not equal");
        }
    }

    private void requireSquareMatrix() {
        if (cols() != rows()) {
            throw new IllegalArgumentException("the square matrix is required for the operation");
        }
    }

    private void requireColsEqualRows(final Matrix other) {
        if (this.cols() != other.rows()) {
            throw new IllegalArgumentException("the matrix is not square and can't be transposed");
        }
    }

}
