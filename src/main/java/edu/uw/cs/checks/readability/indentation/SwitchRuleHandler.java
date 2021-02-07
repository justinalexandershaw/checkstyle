package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for switch rules.
 */
public class SwitchRuleHandler extends AbstractExpressionHandler {

    /**
     * The child elements of a switch rule.
     */
    private static final int[] SWITCH_RULE_CHILDREN = {
        TokenTypes.LITERAL_CASE,
        TokenTypes.LITERAL_DEFAULT,
    };

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck the indentation check
     * @param expr        the abstract syntax tree
     * @param parent      the parent handler
     */
    public SwitchRuleHandler(IndentationCheck indentCheck,
                       DetailAST expr, AbstractExpressionHandler parent) {
        super(indentCheck, "case", expr, parent);
    }

    @Override
    protected IndentLevel getIndentImpl() {
        return new IndentLevel(getParent().getIndent(),
            getIndentCheck().getCaseIndent());
    }

    /**
     * Check the indentation of the case statement.
     */
    private void checkCase() {
        checkChildren(getMainAst(), SWITCH_RULE_CHILDREN, getIndent(),
            true, false);
    }

    @Override
    public IndentLevel getSuggestedChildIndent(AbstractExpressionHandler child) {
        return getIndent();
    }

    @Override
    public void checkIndentation() {
        checkCase();
    }

}
