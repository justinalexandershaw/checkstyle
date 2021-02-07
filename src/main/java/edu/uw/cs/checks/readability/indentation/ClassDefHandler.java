package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for class definitions.
 *
 */
public class ClassDefHandler extends BlockParentHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public ClassDefHandler(IndentationCheck indentCheck,
                           DetailAST ast,
                           AbstractExpressionHandler parent) {
        super(indentCheck, getHandlerName(ast), ast, parent);
    }

    @Override
    protected DetailAST getLeftCurly() {
        return getMainAst().findFirstToken(TokenTypes.OBJBLOCK)
            .findFirstToken(TokenTypes.LCURLY);
    }

    @Override
    protected DetailAST getRightCurly() {
        return getMainAst().findFirstToken(TokenTypes.OBJBLOCK)
            .findFirstToken(TokenTypes.RCURLY);
    }

    @Override
    protected DetailAST getTopLevelAst() {
        return null;
        // note: ident checked by hand in check indentation;
    }

    @Override
    protected DetailAST getListChild() {
        return getMainAst().findFirstToken(TokenTypes.OBJBLOCK);
    }

    @Override
    public void checkIndentation() {
        final DetailAST modifiers = getMainAst().findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers.hasChildren()) {
            checkModifiers();
        }
        else {
            if (getMainAst().getType() != TokenTypes.ANNOTATION_DEF) {
                final DetailAST ident = getMainAst().findFirstToken(TokenTypes.IDENT);
                final int lineStart = getLineStart(ident);
                if (!getIndent().isAcceptable(lineStart)) {
                    logError(ident, "ident", lineStart);
                }
            }
        }
        if (getMainAst().getType() == TokenTypes.ANNOTATION_DEF) {
            final DetailAST atAst = getMainAst().findFirstToken(TokenTypes.AT);
            if (isOnStartOfLine(atAst)) {
                checkWrappingIndentation(getMainAst(), getListChild(), 0,
                        getIndent().getFirstIndentLevel(), false);
            }
        }
        else {
            checkWrappingIndentation(getMainAst(), getListChild());
        }
        super.checkIndentation();
    }

    @Override
    protected int[] getCheckedChildren() {
        return new int[] {
            TokenTypes.EXPR,
            TokenTypes.OBJBLOCK,
            TokenTypes.LITERAL_BREAK,
            TokenTypes.LITERAL_RETURN,
            TokenTypes.LITERAL_THROW,
            TokenTypes.LITERAL_CONTINUE,
        };
    }

    /**
     * Creates a handler name for this class according to ast type.
     *
     * @param ast the abstract syntax tree.
     * @return handler name for this class.
     */
    private static String getHandlerName(DetailAST ast) {
        final String name;

        if (ast.getType() == TokenTypes.CLASS_DEF) {
            name = "class def";
        }
        else if (ast.getType() == TokenTypes.ENUM_DEF) {
            name = "enum def";
        }
        else if (ast.getType() == TokenTypes.ANNOTATION_DEF) {
            name = "annotation def";
        }
        else {
            name = "interface def";
        }
        return name;
    }

}
