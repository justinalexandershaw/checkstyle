package edu.uw.cs.checks.structure;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Enforces that all import statements use wildcard imports.
 */
public class OnlyWildcardImportsCheck extends AbstractCheck {

    /**
     * A key is pointing to the only wildcard imports warning message 
     * text in "messages.properties" file.
     */
    public static final String ONLY_WILDCARD_IMPORTS = "only.wildcard.imports";

    /** 
     * Suffix for the wildcard imports.
     */
    private static final String STAR_IMPORT_SUFFIX = ".*";

    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {TokenTypes.IMPORT};
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.IMPORT) {
            final DetailAST firstDot = ast.getFirstChild();
            logsStarredImportViolation(firstDot);
        }
    }

    /**
     * Finds the full identifier of the import. If the import is not a wildcard import
     * then a violation is logged.
     *
     * @param rootNode the starting dot for the import statement
     */
    private void logsStarredImportViolation(DetailAST rootNode) {
        final FullIdent name = FullIdent.createFullIdent(rootNode);
        final String fullImportText = name.getText();
        if (!fullImportText.endsWith(STAR_IMPORT_SUFFIX)) {
            log(rootNode, ONLY_WILDCARD_IMPORTS, fullImportText);
        }
    }
}
