package processor.matrix;

import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntUnaryOperator;

public enum Transposition {
    MAIN_DIAGONAL(size -> i -> i / size + i % size * size),
    SIDE_DIAGONAL(size -> i -> size * (size - i % size) - i / size - 1),
    VERTICAL_AXIS(size -> i -> size - i % size - 1 + i / size * size),
    HORIZONTAL_AXIS(size -> i -> size * (size - i / size - 1) + i % size);

    private final IntFunction<IntUnaryOperator> function;

    Transposition(final IntFunction<IntUnaryOperator> function) {
        this.function = function;
    }

    IntToDoubleFunction getFormula(final Matrix matrix) {
        return i -> matrix.element(function.apply(matrix.rows()).applyAsInt(i));
    }

}
