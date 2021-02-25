package processor.matrix;

import java.util.Objects;
import java.util.Optional;
import java.util.function.IntToDoubleFunction;

import static java.util.stream.IntStream.range;

public interface Matrix {
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

    private void requireSizeEquals(final Matrix other) {
        if (this.rows() != other.rows() || this.cols() != other.cols()) {
            throw new IllegalArgumentException("the sizes of matrices are not equal");
        }
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
        final IntToDoubleFunction multiplyByMatrix = i -> range(0, this.cols()).mapToDouble(col ->
                element(i / other.cols() * cols() + col)
                        * other.element(i % other.cols()
                        + col * other.cols())).sum();

        return Matrix.create(this.rows(), other.cols(), multiplyByMatrix);
    }

    private void requireColsEqualRows(final Matrix other) {
        if (this.cols() != other.rows()) {
            throw new IllegalArgumentException("the matrix is not square and can't be transposed");
        }
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

    private void requireSquareMatrix() {
        if (cols() != rows()) {
            throw new IllegalArgumentException("the matrix is not square and can't be transposed");
        }
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
    double determinant();

    /**
     * Inverse matrix
     * <p>
     * Inverse matrix A^ is the matrix, the product of which to original matrix A is equal to
     * the identity matrix. The identity matrix is the matrix in which all elements of the main
     * diagonal are ones and other are zeros. Inverse matrix can’t be found if det(A) equals zero.
     *
     * @return an optional of matrix that represents inverse matrix if det(A) not equals zero.
     */
    Optional<Matrix> inverse();

    /**
     * Element of the matrix
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
     * Create and return the Matrix.
     *
     * The method uses the concrete implementation of Matrix interface MatrixImpl
     * If you need to replace the implementation then this is the only method to do this.
     *
     * @param rows number of rows in the Matrix
     * @param cols number of columns in the Matrix
     * @param elements array of elements
     * @return Matrix with given rows, cols and elements
     * @throws IllegalArgumentException if (rows * cols) not equals to number of elements in array
     */
    static Matrix create(final int rows, final int cols, final double[] elements) {
        return new MatrixImpl(rows, cols, elements);
    }

    /**
     * Create and return the Matrix.
     * @param rows number of rows in the Matrix
     * @param cols number of columns in the Matrix
     * @param function to calculate elements according to an index
     * @return Matrix with given rows, cols and function
     */
    static Matrix create(final int rows, final int cols, final IntToDoubleFunction function) {
        return Matrix.create(rows, cols, range(0, rows * cols).mapToDouble(function).toArray());
    }

    static Matrix createIdentityMatrix(final int size) {
        return Matrix.create(size, size, i -> i % (size + 1) == 0 ? 1 : 0);
    }

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

}
