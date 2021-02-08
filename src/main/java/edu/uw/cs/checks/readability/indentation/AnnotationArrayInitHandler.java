package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for annotation array initialization blocks.
 *
 */
public class AnnotationArrayInitHandler extends BlockParentHandler {

    /**
     * Constant to define that the required character does not exist at any position.
     */
    private static final int NOT_EXIST = -1;

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public AnnotationArrayInitHandler(ProperIndentationCheck indentCheck,
                            DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "annotation array initialization", ast, parent);
    }

    @Override
    protected IndentLevel getIndentImpl() {
        final DetailAST parentAST = getMainAst().getParent();
        return new IndentLevel(getLineStart(parentAST));
    }

    @Override
    protected DetailAST getTopLevelAst() {
        return null;
    }

    @Override
    protected DetailAST getLeftCurly() {
        return getMainAst();
    }

    @Override
    protected IndentLevel curlyIndent() {
        int offset = 0;

        final DetailAST lcurly = getLeftCurly();
        if (isOnStartOfLine(lcurly)) {
            offset = getBraceAdjustment();
        }

        final IndentLevel level = new IndentLevel(getIndent(), offset);
        return IndentLevel.addAcceptable(level, level.getLastIndentLevel()
            + getLineWrappingIndentation());
    }

    @Override
    protected DetailAST getRightCurly() {
        return getMainAst().findFirstToken(TokenTypes.RCURLY);
    }

    @Override
    protected boolean canChildrenBeNested() {
        return true;
    }

    @Override
    protected DetailAST getListChild() {
        return getMainAst();
    }

    @Override
    protected IndentLevel getChildrenExpectedIndent() {
        IndentLevel expectedIndent =
            new IndentLevel(getIndent(), getArrayInitIndentation(), getLineWrappingIndentation());

        final int firstLine = getFirstLine(getListChild());
        final int lcurlyPos = expandedTabsColumnNo(getLeftCurly());
        final int firstChildPos =
            getNextFirstNonBlankOnLineAfter(firstLine, lcurlyPos);

        if (firstChildPos != NOT_EXIST) {
            expectedIndent = IndentLevel.addAcceptable(expectedIndent, firstChildPos, lcurlyPos
                    + getLineWrappingIndentation());
        }
        return expectedIndent;
    }

    /**
     * Returns column number of first non-blank char after
     * specified column on specified line or -1 if
     * such char doesn't exist.
     *
     * @param lineNo   number of line on which we search
     * @param columnNo number of column after which we search
     *
     * @return column number of first non-blank char after
     *         specified column on specified line or -1 if
     *         such char doesn't exist.
     */
    private int getNextFirstNonBlankOnLineAfter(int lineNo, int columnNo) {
        int realColumnNo = columnNo + 1;
        final String line = getIndentCheck().getLines()[lineNo - 1];
        final int lineLength = line.length();
        while (realColumnNo < lineLength
            && Character.isWhitespace(line.charAt(realColumnNo))) {
            realColumnNo++;
        }

        if (realColumnNo == lineLength) {
            realColumnNo = -1;
        }
        return realColumnNo;
    }

    /**
     * A shortcut for {@code IndentationCheck} property.
     *
     * @return value of lineWrappingIndentation property
     *         of {@code IndentationCheck}
     */
    private int getLineWrappingIndentation() {
        return getIndentCheck().getLineWrappingIndentation();
    }

    /**
     * A shortcut for {@code IndentationCheck} property.
     *
     * @return value of arrayInitIndent property
     *         of {@code IndentationCheck}
     */
    private int getArrayInitIndentation() {
        return getIndentCheck().getArrayInitIndent();
    }

}
