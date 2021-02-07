package edu.uw.cs.checks.readability.indentation;

import java.util.SortedMap;
import java.util.TreeMap;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * Represents a set of abstract syntax tree.
 *
 */
public class DetailAstSet {

    /**
     * The instance of {@code ProperIndentationCheck} used by this class.
     */
    private final ProperIndentationCheck indentCheck;

    /**
     * Maps line numbers to their ast.
     */
    private final SortedMap<Integer, DetailAST> astLines = new TreeMap<>();

    /**
     * Construct an instance of this class with {@code ProperIndentationCheck} parameters.
     *
     * @param indentCheck ProperIndentationCheck parameters
     */
    public DetailAstSet(ProperIndentationCheck indentCheck) {
        this.indentCheck = indentCheck;
    }

    /**
     * Add ast to the set of ast.
     *
     * @param ast   the ast to add
     */
    public void addAst(DetailAST ast) {
        addLineWithAst(ast.getLineNo(), ast);
    }

    /**
     * Map ast with their line number.
     *
     * @param lineNo    line number of ast to add
     * @param ast       ast to add
     */
    private void addLineWithAst(int lineNo, DetailAST ast) {
        astLines.put(lineNo, ast);
    }

    /**
     * Get starting column number for the ast.
     *
     * @param lineNum the line number as key
     * @return start column for ast
     */
    public Integer getStartColumn(int lineNum) {
        Integer startColumn = null;
        final DetailAST ast = getAst(lineNum);

        if (ast != null) {
            startColumn = expandedTabsColumnNo(ast);
        }

        return startColumn;
    }

    /**
     * Check if the this set of ast is empty.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return astLines.isEmpty();
    }

    /**
     * The first line in set of ast.
     *
     * @return first line in set of ast.
     */
    public DetailAST firstLine() {
        return astLines.get(astLines.firstKey());
    }

    /**
     * Get the ast corresponding to line number.
     *
     * @param lineNum   line number of ast.
     * @return          ast with their corresponding line number or null if no mapping is present
     */
    public DetailAST getAst(int lineNum) {
        return astLines.get(lineNum);
    }

    /**
     * Get the line number of the last line.
     *
     * @return the line number of the last line
     */
    public Integer lastLine() {
        return astLines.lastKey();
    }

    /**
     * Get the column number for the start of a given expression, expanding
     * tabs out into spaces in the process.
     *
     * @param ast   the expression to find the start of
     *
     * @return the column number for the start of the expression
     */
    protected final int expandedTabsColumnNo(DetailAST ast) {
        final String line = indentCheck.getLine(ast.getLineNo() - 1);

        return CommonUtil.lengthExpandedTabs(line, ast.getColumnNo(),
                indentCheck.getIndentationTabWidth());
    }

}
