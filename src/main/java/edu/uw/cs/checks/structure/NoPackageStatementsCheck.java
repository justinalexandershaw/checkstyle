package edu.uw.cs.checks.structure;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Enforces a no-package rule.
 */
public class NoPackageStatementsCheck extends AbstractCheck {

    /**
     * A key is pointing to the no package statements warning message 
     * text in "messages.properties" file.
     */
    public static final String NO_PACKAGE_STATEMENTS = "no.package.statements";

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
        return new int[] {TokenTypes.PACKAGE_DEF};
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST firstDot = ast.findFirstToken(TokenTypes.DOT);
        final FullIdent fullPackageIdentifier = FullIdent.createFullIdent(firstDot);
        final String fullImportText = fullPackageIdentifier.getText();
        log(ast, NO_PACKAGE_STATEMENTS, fullImportText);
    }
    
}
