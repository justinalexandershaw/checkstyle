package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for package definitions.
 *
 */
public class PackageDefHandler extends AbstractExpressionHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public PackageDefHandler(IndentationCheck indentCheck,
            DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "package def", ast, parent);
    }

    @Override
    public void checkIndentation() {
        final int columnNo = expandedTabsColumnNo(getMainAst());

        if (!getIndent().isAcceptable(columnNo) && isOnStartOfLine(getMainAst())) {
            logError(getMainAst(), "", columnNo);
        }

        final DetailAST semi = getMainAst().findFirstToken(TokenTypes.SEMI);

        checkWrappingIndentation(getMainAst(), semi);
    }

}
