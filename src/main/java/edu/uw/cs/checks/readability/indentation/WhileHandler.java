package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for while loops.
 *
 */
public class WhileHandler extends BlockParentHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public WhileHandler(ProperIndentationCheck indentCheck,
        DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "while", ast, parent);
    }

    /**
     * Check the indentation of the conditional expression.
     */
    private void checkCondExpr() {
        final DetailAST condAst = getMainAst().findFirstToken(TokenTypes.EXPR);
        final IndentLevel expected =
            new IndentLevel(getIndent(), getBasicOffset());
        checkExpressionSubtree(condAst, expected, false, false);
    }

    @Override
    public void checkIndentation() {
        checkCondExpr();
        super.checkIndentation();
    }

}
