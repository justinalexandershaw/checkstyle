package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for labels.
 *
 */
public class LabelHandler extends AbstractExpressionHandler {

    /**
     * The types of expressions that are children of a label.
     */
    private static final int[] LABEL_CHILDREN = {
        TokenTypes.IDENT,
    };

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param expr          the abstract syntax tree
     * @param parent        the parent handler
     */
    public LabelHandler(IndentationCheck indentCheck,
        DetailAST expr, AbstractExpressionHandler parent) {
        super(indentCheck, "label", expr, parent);
    }

    @Override
    protected IndentLevel getIndentImpl() {
        final IndentLevel level = new IndentLevel(super.getIndentImpl(), -getBasicOffset());
        return IndentLevel.addAcceptable(level, super.getIndentImpl());
    }

    /**
     * Check the indentation of the label.
     */
    private void checkLabel() {
        checkChildren(getMainAst(), LABEL_CHILDREN, getIndent(), true, false);
    }

    @Override
    public void checkIndentation() {
        checkLabel();
        // need to check children (like 'block' parents do)
        final DetailAST parent = getMainAst().getFirstChild().getNextSibling();

        final IndentLevel expected =
            new IndentLevel(getIndent(), getBasicOffset());

        checkExpressionSubtree(parent, expected, true, false);
    }

}
