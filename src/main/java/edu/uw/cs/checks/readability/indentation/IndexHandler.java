package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

/**
 * Handler for array index operation.
 *
 */
public class IndexHandler extends AbstractExpressionHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public IndexHandler(ProperIndentationCheck indentCheck,
                      DetailAST ast,
                      AbstractExpressionHandler parent) {
        super(indentCheck, "index op", ast, parent);
    }

    @Override
    public void checkIndentation() {
        // do nothing. Used to provide a correct suggested child level for now.
    }

}
