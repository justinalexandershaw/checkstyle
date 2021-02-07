package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for catch blocks.
 *
 */
public class CatchHandler extends BlockParentHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public CatchHandler(IndentationCheck indentCheck,
        DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "catch", ast, parent);
    }

    /**
     * Check the indentation level of the conditional expression.
     */
    private void checkCondExpr() {
        final DetailAST condAst = getMainAst().findFirstToken(TokenTypes.LPAREN)
            .getNextSibling();
        checkExpressionSubtree(condAst, getIndent(), true, true);
    }

    @Override
    public void checkIndentation() {
        super.checkIndentation();
        checkCondExpr();
    }

}
