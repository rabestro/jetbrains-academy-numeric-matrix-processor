package processor.matrix;

import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntUnaryOperator;

public enum Transposition {
    MAIN(size -> i -> i / size + i % size * size),
    SIDE(size -> i -> size * (size - i % size) - i / size - 1),
    VERTICAL(size -> i -> size - i % size - 1 + i / size * size),
    HORIZONTAL(size -> i -> size * (size - i / size - 1) + i % size);

    private final IntFunction<IntUnaryOperator> function;

    Transposition(IntFunction<IntUnaryOperator> function) {
        this.function = function;
    }

    IntToDoubleFunction getFormula(final Matrix matrix) {
        return i -> matrix.element(function.apply(matrix.rows()).applyAsInt(i));
    }

}
