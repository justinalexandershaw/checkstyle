package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

/**
 * Handler for lambda expressions.
 *
 */
public class LambdaHandler extends AbstractExpressionHandler {
    /**
     * Checks whether the lambda is correctly indented, this variable get its value from checking
     * the lambda handler's indentation, and it is being used in aligning the lambda's children.
     * A true value depicts lambda is correctly aligned without giving any errors.
     * This is updated to false where there is any Indentation error log.
     */
    private boolean isLambdaCorrectlyIndented = true;

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck the indentation check
     * @param ast the abstract syntax tree
     * @param parent the parent handler
     */
    public LambdaHandler(ProperIndentationCheck indentCheck,
                         DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "lambda", ast, parent);
    }

    @Override
    public IndentLevel getSuggestedChildIndent(AbstractExpressionHandler child) {
        IndentLevel childIndent = getIndent();
        if (isLambdaCorrectlyIndented) {
            childIndent = IndentLevel.addAcceptable(childIndent, getLineStart(getMainAst()),
                    getLineStart(getMainAst().getFirstChild()));
        }

        return childIndent;
    }

    /**
     * {@inheritDoc}.
     *
     * @noinspection MethodWithMultipleReturnPoints
     */
    @Override
    protected IndentLevel getIndentImpl() {
        if (getParent() instanceof MethodCallHandler) {
            return getParent().getSuggestedChildIndent(this);
        }

        DetailAST parent = getMainAst().getParent();
        if (getParent() instanceof NewHandler) {
            parent = parent.getParent();
        }

        // Use the start of the parent's line as the reference indentation level.
        IndentLevel level = new IndentLevel(getLineStart(parent));

        // If the start of the lambda is the first element on the line;
        // assume line wrapping with respect to its parent.
        final DetailAST firstChild = getMainAst().getFirstChild();
        if (getLineStart(firstChild) == expandedTabsColumnNo(firstChild)) {
            level = new IndentLevel(level, getIndentCheck().getLineWrappingIndentation());
        }

        return level;
    }

    @Override
    public void checkIndentation() {
        final DetailAST mainAst = getMainAst();
        final DetailAST firstChild = mainAst.getFirstChild();

        // If the "->" has no children, it is a switch
        // rule lambda (i.e. 'case ONE -> 1;')
        final boolean isSwitchRuleLambda = firstChild == null;

        if (!isSwitchRuleLambda
            && getLineStart(firstChild) == expandedTabsColumnNo(firstChild)) {
            final int firstChildColumnNo = expandedTabsColumnNo(firstChild);
            final IndentLevel level = getIndent();

            if (isNonAcceptableIndent(firstChildColumnNo, level)) {
                isLambdaCorrectlyIndented = false;
                logError(firstChild, "arguments", firstChildColumnNo, level);
            }
        }

        // If the "->" is the first element on the line, assume line wrapping.
        final int mainAstColumnNo = expandedTabsColumnNo(mainAst);
        final boolean isLineWrappedLambda = mainAstColumnNo == getLineStart(mainAst);
        if (isLineWrappedLambda) {
            checkLineWrappedLambda(isSwitchRuleLambda, mainAstColumnNo);
        }
    }

    /**
     * Checks that given indent is acceptable or not.
     *
     * @param astColumnNo indent value to check
     * @param level indent level
     * @return true if indent is not acceptable
     */
    private boolean isNonAcceptableIndent(int astColumnNo, IndentLevel level) {
        return astColumnNo < level.getFirstIndentLevel()
            || getIndentCheck().isForceStrictCondition()
               && !level.isAcceptable(astColumnNo);
    }

    /**
     * This method checks a line wrapped lambda, whether it is a lambda
     * expression or switch rule lambda.
     *
     * @param isSwitchRuleLambda if mainAst is a switch rule lambda
     * @param mainAstColumnNo the column number of the lambda we are checking
     */
    private void checkLineWrappedLambda(final boolean isSwitchRuleLambda,
                                        final int mainAstColumnNo) {
        final IndentLevel level;
        final DetailAST mainAst = getMainAst();

        if (isSwitchRuleLambda) {
            // We check the indentation of the case literal or default literal
            // on the previous line and use that to determine the correct
            // indentation for the line wrapped "->"
            final DetailAST previousSibling = mainAst.getPreviousSibling();
            final int previousLineStart = getLineStart(previousSibling);

            level = new IndentLevel(new IndentLevel(previousLineStart),
                    getIndentCheck().getLineWrappingIndentation());
        }
        else {
            level = new IndentLevel(getIndent(),
                getIndentCheck().getLineWrappingIndentation());
        }

        if (isNonAcceptableIndent(mainAstColumnNo, level)) {
            isLambdaCorrectlyIndented = false;
            logError(mainAst, "", mainAstColumnNo, level);
        }
    }
}
