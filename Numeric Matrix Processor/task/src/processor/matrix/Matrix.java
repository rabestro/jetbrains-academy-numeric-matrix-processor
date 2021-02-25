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
    Matrix add(final Matrix other);

    /**
     * Multiplication by number
     * <p>
     * Multiply every element of the matrix by a constant.
     *
     * @param constant for matrix multiplication by
     * @return a new matrix that represents multiplication of the matrix by given constant
     */
    Matrix multiply(final double constant);

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
    Matrix multiply(final Matrix other);

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
    Matrix transpose(final Transposition mode);

    default Matrix transpose() {
        return this.transpose(Transposition.MAIN);
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

    int getRows();

    int getCols();

    static Matrix create(final int rows, final int cols, final IntToDoubleFunction function) {
        return Matrix.create(rows, cols, range(0, rows * cols).mapToDouble(function).toArray());
    }

    static Matrix create(final int rows, final int cols, final double[] elements) {
        return new MatrixImpl(rows, cols, elements);
    }

    static Matrix createIdentityMatrix(final int size) {
        // TODO write a function for identity matrix
        return Matrix.create(size, size, i -> 0);
    }

    default double element(final int row, final int col) {
        Objects.checkIndex(row, getRows());
        Objects.checkIndex(col, getCols());
        return element(row * getCols() + col);
    }

}
