package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * Handler for if statements.
 *
 */
public class IfHandler extends BlockParentHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public IfHandler(ProperIndentationCheck indentCheck,
        DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "if", ast, parent);
    }

    @Override
    public IndentLevel getSuggestedChildIndent(AbstractExpressionHandler child) {
        final IndentLevel result;
        if (child instanceof ElseHandler) {
            result = getIndent();
        }
        else {
            result = super.getSuggestedChildIndent(child);
        }
        return result;
    }

    @Override
    protected IndentLevel getIndentImpl() {
        final IndentLevel result;
        if (isIfAfterElse()) {
            result = getParent().getIndent();
        }
        else {
            result = super.getIndentImpl();
        }
        return result;
    }

    /**
     * Determines if this 'if' statement is part of an 'else' clause
     * and on the same line.
     *
     * @return true if this 'if' is part of an 'else', false otherwise
     */
    private boolean isIfAfterElse() {
        // check if there is an 'else' and an 'if' on the same line
        final DetailAST parent = getMainAst().getParent();
        return parent.getType() == TokenTypes.LITERAL_ELSE
            && TokenUtil.areOnSameLine(parent, getMainAst());
    }

    @Override
    protected void checkTopLevelToken() {
        if (!isIfAfterElse()) {
            super.checkTopLevelToken();
        }
    }

    /**
     * Check the indentation of the conditional expression.
     */
    private void checkCondExpr() {
        final DetailAST condAst = getMainAst().findFirstToken(TokenTypes.LPAREN)
            .getNextSibling();
        final IndentLevel expected =
            new IndentLevel(getIndent(), getBasicOffset());
        checkExpressionSubtree(condAst, expected, false, false);
    }

    @Override
    public void checkIndentation() {
        super.checkIndentation();
        checkCondExpr();
        checkWrappingIndentation(getMainAst(), getIfStatementRightParen(getMainAst()));
    }

    /**
     * Returns right parenthesis of if statement.
     *
     * @param literalIfAst
     *          literal-if ast node(TokenTypes.LITERAL_IF)
     * @return right parenthesis of if statement.
     */
    private static DetailAST getIfStatementRightParen(DetailAST literalIfAst) {
        return literalIfAst.findFirstToken(TokenTypes.RPAREN);
    }

}
