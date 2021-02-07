package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for do...while blocks.
 *
 */
public class DoWhileHandler extends BlockParentHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public DoWhileHandler(IndentationCheck indentCheck,
            DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "do..while", ast, parent);
    }

    /**
     * Check the indentation level of the while and conditional expression.
     */
    private void checkWhileExpr() {
        // check while statement alone

        final DetailAST whileAst = getMainAst().findFirstToken(TokenTypes.DO_WHILE);

        if (isOnStartOfLine(whileAst)
                && !getIndent().isAcceptable(expandedTabsColumnNo(whileAst))) {
            logError(whileAst, "while", expandedTabsColumnNo(whileAst));
        }

        // check condition alone

        final DetailAST condAst = getMainAst().findFirstToken(TokenTypes.LPAREN).getNextSibling();

        checkExpressionSubtree(condAst, getIndent(), false, false);
    }

    @Override
    protected DetailAST getNonListChild() {
        return getMainAst().getFirstChild();
    }

    @Override
    public void checkIndentation() {
        super.checkIndentation();
        checkWhileExpr();
    }

}
