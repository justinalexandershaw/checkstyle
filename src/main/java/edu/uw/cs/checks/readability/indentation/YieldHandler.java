package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * Handler for yield expression.
 */
public class YieldHandler extends AbstractExpressionHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck the indentation check
     * @param ast         the abstract syntax tree
     * @param parent      the parent handler
     */
    public YieldHandler(ProperIndentationCheck indentCheck,
                      DetailAST ast,
                      AbstractExpressionHandler parent) {
        super(indentCheck, "yield", ast, parent);
    }

    @Override
    public void checkIndentation() {
        checkYield();
        final DetailAST expression = getMainAst().getFirstChild();
        if (!TokenUtil.areOnSameLine(getMainAst(), expression)) {
            checkExpressionSubtree(expression, getIndent(), false, false);
        }
    }

    /**
     * Check the indentation of the yield keyword.
     */
    private void checkYield() {
        final DetailAST yieldKey = getMainAst();
        final int columnNo = expandedTabsColumnNo(yieldKey);
        if (isOnStartOfLine(yieldKey) && !getIndent().isAcceptable(columnNo)) {
            logError(yieldKey, "", columnNo);
        }
    }
}
