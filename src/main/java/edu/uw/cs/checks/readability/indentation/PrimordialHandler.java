package edu.uw.cs.checks.readability.indentation;

/**
 * A default no-op handler.
 *
 */
public class PrimordialHandler extends AbstractExpressionHandler {

    /**
     * Construct an instance of this handler with the given indentation check.
     *
     * @param indentCheck the indentation check
     */
    public PrimordialHandler(ProperIndentationCheck indentCheck) {
        super(indentCheck, null, null, null);
    }

    @Override
    public void checkIndentation() {
        // nothing to check
    }

    @Override
    public IndentLevel getSuggestedChildIndent(AbstractExpressionHandler child) {
        return getIndentImpl();
    }

    @Override
    protected IndentLevel getIndentImpl() {
        return new IndentLevel(0);
    }

}
