package processor.ui;

import processor.matrix.Matrix;
import processor.matrix.Transposition;

import java.util.Scanner;
import java.util.stream.DoubleStream;

public final class Application {
    private final Scanner scanner = new Scanner(System.in);

    public void addMatrices() {
        final var first = readMatrix("first");
        final var second = readMatrix("second");
        print(first.add(second));
    }

    public void multiplyByConstant() {
        final var matrix = readMatrix("the");
        System.out.println("Enter constant:");
        final var constant = scanner.nextDouble();
        print(matrix.multiply(constant));
    }

    public void multiplyMatrices() {
        final var first = readMatrix("first");
        final var second = readMatrix("second");
        print(first.multiply(second));
    }

    public void transpose(Transposition mode) {
        final var matrix = readMatrix("the");
        print(matrix.transpose(mode));
    }

    public void calculateDeterminate() {
        final var matrix = readMatrix("the");
        print(matrix.determinant());
    }

    public void inverseMatrix() {
        final var matrix = readMatrix("the");
        print(matrix.inverse().orElseThrow());
    }

    private Matrix readMatrix(String name) {
        System.out.println("Enter size of " + name + " matrix:");
        final var rows = scanner.nextInt();
        final var cols = scanner.nextInt();

        System.out.println("Enter " + name + " matrix:");
        final var cells = DoubleStream
                .generate(scanner::nextDouble)
                .limit(rows * cols)
                .toArray();

        return new Matrix(rows, cols, cells);
    }

    private void print(Object result) {
        System.out.println("The result is:");
        System.out.println(result);
    }
}
