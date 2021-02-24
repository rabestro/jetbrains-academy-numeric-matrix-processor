package processor;

import processor.matrix.Transposition;
import processor.ui.Application;
import processor.ui.Menu;

public final class Main {
    public static void main(String[] args) {
        final var app = new Application();

        new Menu("Numeric Matrix Processor")
                .add("Add matrices", app::addMatrices)
                .add("Multiply matrix to a constant", app::multiplyByConstant)
                .add("Multiply matrices", app::multiplyMatrices)
                .add("Transpose matrix", new Menu("Transpose Matrix")
                        .oneTime()
                        .add("Main diagonal", () -> app.transpose(Transposition.MAIN))
                        .add("Side diagonal", () -> app.transpose(Transposition.SIDE))
                        .add("Vertical line", () -> app.transpose(Transposition.VERTICAL))
                        .add("Horizontal line", () -> app.transpose(Transposition.HORIZONTAL))
                )
                .add("Calculate a determinant", app::calculateDeterminate)
                .add("Inverse matrix", app::inverseMatrix)
                .run();
    }
}
