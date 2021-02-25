package processor.matrix;

import java.util.function.IntToDoubleFunction;

public enum Transposition {
    MAIN {
        @Override
        IntToDoubleFunction getFormula(final Matrix m) {
            return i -> m.element(i / m.getRows() + i % m.getCols() * m.getCols());
        }
    }, SIDE {
        @Override
        IntToDoubleFunction getFormula(final Matrix m) {
            return i -> m.element(m.getCols() * (m.getRows() - i % m.getCols()) - i / m.getRows() - 1);
        }
    }, VERTICAL {
        @Override
        IntToDoubleFunction getFormula(final Matrix m) {
            return i -> m.element(m.getCols() - i % m.getCols() - 1 + i / m.getRows() * m.getRows());
        }
    }, HORIZONTAL {
        @Override
        IntToDoubleFunction getFormula(final Matrix m) {
            return i -> m.element(m.getRows() * (m.getCols() - i / m.getRows() - 1) + i % m.getCols());
        }
    };

    abstract IntToDoubleFunction getFormula(final Matrix m);
}
