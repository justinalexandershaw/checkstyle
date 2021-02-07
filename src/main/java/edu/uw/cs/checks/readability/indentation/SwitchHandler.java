package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for switch statements.
 *
 */
public class SwitchHandler extends BlockParentHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public SwitchHandler(IndentationCheck indentCheck,
        DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "switch", ast, parent);
    }

    @Override
    protected DetailAST getLeftCurly() {
        return getMainAst().findFirstToken(TokenTypes.LCURLY);
    }

    @Override
    protected DetailAST getRightCurly() {
        return getMainAst().findFirstToken(TokenTypes.RCURLY);
    }

    @Override
    protected DetailAST getListChild() {
        // all children should be taken care of by case handler (plus
        // there is no parent of just the cases, if checking is needed
        // here in the future, an additional way beyond checkChildren()
        // will have to be devised to get children)
        return null;
    }

    @Override
    protected DetailAST getNonListChild() {
        return null;
    }

    /**
     * Check the indentation of the switch expression.
     */
    private void checkSwitchExpr() {
        checkExpressionSubtree(
            getMainAst().findFirstToken(TokenTypes.LPAREN).getNextSibling(),
            getIndent(),
            false,
            false);
    }

    @Override
    public void checkIndentation() {
        checkSwitchExpr();
        super.checkIndentation();
    }

}
