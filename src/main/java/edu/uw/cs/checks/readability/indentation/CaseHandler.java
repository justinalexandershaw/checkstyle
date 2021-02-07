package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for case statements.
 *
 */
public class CaseHandler extends AbstractExpressionHandler {

    /**
     * The child elements of a case expression.
     */
    private static final int[] CASE_CHILDREN = {
        TokenTypes.LITERAL_CASE,
        TokenTypes.LITERAL_DEFAULT,
    };

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param expr          the abstract syntax tree
     * @param parent        the parent handler
     */
    public CaseHandler(IndentationCheck indentCheck,
        DetailAST expr, AbstractExpressionHandler parent) {
        super(indentCheck, "case", expr, parent);
    }

    @Override
    protected IndentLevel getIndentImpl() {
        return new IndentLevel(getParent().getIndent(),
                               getIndentCheck().getCaseIndent());
    }

    /**
     * Check the indentation of the case statement.
     */
    private void checkCase() {
        checkChildren(getMainAst(), CASE_CHILDREN, getIndent(), true, false);
    }

    @Override
    public IndentLevel getSuggestedChildIndent(AbstractExpressionHandler child) {
        return getIndent();
    }

    @Override
    public void checkIndentation() {
        checkCase();
    }

}
