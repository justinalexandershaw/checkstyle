package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for for loops.
 *
 */
public class ForHandler extends BlockParentHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public ForHandler(ProperIndentationCheck indentCheck,
        DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "for", ast, parent);
    }

    /**
     * Check the indentation of the parameters of the 'for' loop.
     */
    private void checkForParams() {
        final IndentLevel expected =
            new IndentLevel(getIndent(), getBasicOffset());
        final DetailAST init = getMainAst().findFirstToken(TokenTypes.FOR_INIT);

        if (init == null) {
            // for each
            final DetailAST forEach =
                    getMainAst().findFirstToken(TokenTypes.FOR_EACH_CLAUSE);
            checkExpressionSubtree(forEach, expected, false, false);
        }
        else {
            checkExpressionSubtree(init, expected, false, false);

            final DetailAST cond =
                    getMainAst().findFirstToken(TokenTypes.FOR_CONDITION);
            checkExpressionSubtree(cond, expected, false, false);

            final DetailAST forIterator =
                    getMainAst().findFirstToken(TokenTypes.FOR_ITERATOR);
            checkExpressionSubtree(forIterator, expected, false, false);
        }
    }

    @Override
    public void checkIndentation() {
        checkForParams();
        super.checkIndentation();
        checkWrappingIndentation(getMainAst(), getForLoopRightParen(getMainAst()));
    }

    /**
     * Returns right parenthesis of for-loop statement.
     *
     * @param literalForAst
     *          literal-for ast node(TokenTypes.LITERAL_FOR)
     * @return right parenthesis of for-loop statement.
     */
    private static DetailAST getForLoopRightParen(DetailAST literalForAst) {
        return literalForAst.findFirstToken(TokenTypes.RPAREN);
    }

}
