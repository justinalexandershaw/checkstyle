package edu.uw.cs.checks.readability;

import com.puppycrawl.tools.checkstyle.api.*;

/**
 * <p>
 * Detects non-blank System.out.println calls in the {@code main} method.
 * </p>
 * <p>
 * Rationale: A {@code main} method should be a concise summary of the program,
 * and should outline -- at a high level -- what the program does. In other words,
 * the main method should describe what the program does without describing how
 * it does that. Non-blank {@code System.out.println} calls are an implementation
 * detail that should not be included in our main method.
 * </p>
 * <p>
 * To configure the check:
 * </p>
 * <pre>
 * &lt;module name=&quot;NonBlankPrintlnInMain&quot;/&gt;
 * </pre>
 * <p>Example:</p>
 * <pre>
 * public class Hello {
 *    public static void main(String[] args) {
 *       System.out.println("Hello, World!"); // violation
 *       System.out.println(); // OK
 *       String helloWorld = "Hello, World!";
 *       System.out.print(helloWorld); // violation
 *    }
 * }
 * </pre>
 */
public class NonBlankPrintInMainCheck extends AbstractCheck {

    private static final String[] INVALID_PRINTS = new String[] {"print", "println", "printf"};

    /**
     * A key is pointing to the non-blank print in main
     * text in "messages.properties" file.
     */
    public static final String MSG_KEY = "nonblank.print.in.main";

    @Override
    public int[] getRequiredTokens() {
        return new int[] { TokenTypes.METHOD_DEF };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }

    @Override
    public void visitToken(DetailAST method) {
        if (isNamedMain(method) 
                && isPublicAndStatic(method)
                && isVoid(method)
                && hasOnlyStringArrayParam(method)) {
            final DetailAST listOfStatements = method.findFirstToken(TokenTypes.SLIST);
            if (listOfStatements != null) {
                DetailAST expr = listOfStatements.findFirstToken(TokenTypes.EXPR);
                while (expr != null) {
                    if (expr.getType() == TokenTypes.EXPR) {
                        checkExpression(expr);
                    }
                    expr = expr.getNextSibling();
                }
            }
        }
    }

    /**
     * Checks that method is named "main".
     *
     * @param method the METHOD_DEF node
     * @return true if check passed, false otherwise
     */
    private static boolean isNamedMain(DetailAST method) {
        final DetailAST ident = method.findFirstToken(TokenTypes.IDENT);
        return "main".equals(ident.getText());
    }

    /**
     * Checks that method has final and static modifiers.
     *
     * @param method the METHOD_DEF node
     * @return true if check passed, false otherwise
     */
    private static boolean isPublicAndStatic(DetailAST method) {
        final DetailAST modifiers = method.findFirstToken(TokenTypes.MODIFIERS);

        return modifiers.findFirstToken(TokenTypes.LITERAL_PUBLIC) != null
            && modifiers.findFirstToken(TokenTypes.LITERAL_STATIC) != null;
    }

    /**
     * Checks that return type is {@code void}.
     *
     * @param method the METHOD_DEF node
     * @return true if check passed, false otherwise
     */
    private static boolean isVoid(DetailAST method) {
        final DetailAST type =
            method.findFirstToken(TokenTypes.TYPE).getFirstChild();
        return type.getType() == TokenTypes.LITERAL_VOID;
    }

    /**
     * Checks that method has only {@code String[]} param.
     *
     * @param method the METHOD_DEF node
     * @return true if check passed, false otherwise
     */
    private static boolean hasOnlyStringArrayParam(DetailAST method) {
        boolean checkPassed = false;
        final DetailAST params = method.findFirstToken(TokenTypes.PARAMETERS);
        if (params.getChildCount() == 1) {
            final DetailAST firstChild = params.getFirstChild();
            final DetailAST typeAst = firstChild.findFirstToken(TokenTypes.TYPE);
            final DetailAST arrayDeclaration = typeAst.findFirstToken(TokenTypes.ARRAY_DECLARATOR);            
            if (arrayDeclaration != null) {
                checkPassed = isStringType(arrayDeclaration.getFirstChild());
            }
        }
        return checkPassed;
    }

    /**
     * Whether the type is java.lang.String.
     *
     * @param typeAst the type to check.
     * @return true, if the type is java.lang.String.
     */
    private static boolean isStringType(DetailAST typeAst) {
        final FullIdent type = FullIdent.createFullIdent(typeAst);
        return "String".equals(type.getText())
            || "java.lang.String".equals(type.getText());
    }

    /**
     * Checks that the given expression node is not a non-blank System.out.println expression.
     * @param expr the expression within the main method
     */
    private void checkExpression(DetailAST expr) {
        final DetailAST methodCall = expr.findFirstToken(TokenTypes.METHOD_CALL);
        if (methodCall != null
                && hasNonBlankParameters(methodCall)
                && hasSystemDotOutIdentifier(methodCall)
                && !getIdentifier(methodCall).equals("")) {
            final String identifier = getIdentifier(methodCall);
            log(methodCall, MSG_KEY, identifier);
        }
    }

    /**
     * Returns a string representation of the identifier of System.out.(identifier) if the
     * identifier matches one of the INVALID_PRINTS identifiers. Otherwise, this method returns
     * an empty string representing no matches were found.
     * @param method the AST node which represents a method call in the main method.
     * @return a string representation of the System.out.<identifier> or an empty String if no
     * matches were found.
     */
    private static String getIdentifier(DetailAST method) {
        try {
            final DetailAST lastDot = method.findFirstToken(TokenTypes.DOT);
            final String identifier = lastDot.findFirstToken(TokenTypes.IDENT).getText();
            for (String id : INVALID_PRINTS) {
                if (identifier.equals(id)) {
                    return id;
                }
            }
            return ""; // return a string that will never be in the INVALID_PRINTS array
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns true if the given method call has a System.out call.
     * @param method - the method call node in the AST
     * @return whether method has system dot out identifiers.
     */
    private static boolean hasSystemDotOutIdentifier(DetailAST method) {
        try {
            final DetailAST lastDot = method.findFirstToken(TokenTypes.DOT);
            final DetailAST firstDot = lastDot.findFirstToken(TokenTypes.DOT);
            final DetailAST system = firstDot.getFirstChild();
            final DetailAST out = firstDot.getLastChild();
            return system.getText().equals("System") && out.getText().equals("out");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the given method call has non-blank parameters.
     * The given method call should not be null. Returns false if there is no expression list.
     * @param method the method call node.
     * @return whether 
     */
    private static boolean hasNonBlankParameters(DetailAST method) {
        final DetailAST eList = method.findFirstToken(TokenTypes.ELIST);
        return eList != null && eList.getChildCount() > 0;
    }
}