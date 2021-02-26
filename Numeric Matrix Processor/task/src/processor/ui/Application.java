package processor.ui;

import processor.matrix.Matrix;
import processor.matrix.Transposition;

import java.util.Scanner;

public final class Application implements Runnable {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        new Menu("Numeric Matrix Processor")
                .add("Add matrices", this::addMatrices)
                .add("Multiply matrix to a constant", this::multiplyByConstant)
                .add("Multiply matrices", this::multiplyMatrices)
                .add("Transpose matrix", new Menu("Transpose Matrix")
                        .oneTime()
                        .add("Main diagonal", () -> this.transpose(Transposition.MAIN_DIAGONAL))
                        .add("Side diagonal", () -> this.transpose(Transposition.SIDE_DIAGONAL))
                        .add("Vertical line", () -> this.transpose(Transposition.VERTICAL_AXIS))
                        .add("Horizontal line", () -> this.transpose(Transposition.HORIZONTAL_AXIS))
                )
                .add("Calculate a determinant", this::calculateDeterminate)
                .add("Inverse matrix", this::inverseMatrix)
                .run();
    }

    private void addMatrices() {
        final var first = readMatrix("first");
        final var second = readMatrix("second");
        print(first.add(second));
    }

    private void multiplyByConstant() {
        final var matrix = readMatrix("the");
        System.out.println("Enter constant:");
        final var constant = scanner.nextDouble();
        print(matrix.multiply(constant));
    }

    private void multiplyMatrices() {
        final var first = readMatrix("first");
        final var second = readMatrix("second");
        print(first.multiply(second));
    }

    private void transpose(final Transposition mode) {
        final var matrix = readMatrix("the");
        print(matrix.transpose(mode));
    }

    private void calculateDeterminate() {
        final var matrix = readMatrix("the");
        print(matrix.determinant());
    }

    private void inverseMatrix() {
        final var matrix = readMatrix("the");
        print(matrix.inverse().orElseThrow());
    }

    private Matrix readMatrix(final String name) {
        System.out.println("Enter size (rows and cols) of " + name + " matrix:");
        final var rows = scanner.nextInt();
        final var cols = scanner.nextInt();
        System.out.println("Enter " + name + " matrix:");
        return Matrix.create(rows, cols, i -> scanner.nextDouble());
    }

    private void print(Object result) {
        System.out.println("The result is:");
        System.out.println(result);
    }

}
