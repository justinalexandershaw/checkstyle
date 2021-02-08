package edu.uw.cs.checks.readability.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * Handler for else blocks.
 *
 */
public class ElseHandler extends BlockParentHandler {

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public ElseHandler(ProperIndentationCheck indentCheck,
        DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "else", ast, parent);
    }

    @Override
    protected void checkTopLevelToken() {
        // check if else is nested with rcurly of if:
        //
        //  } else ...

        final DetailAST ifAST = getMainAst().getParent();
        final DetailAST slist = ifAST.findFirstToken(TokenTypes.SLIST);
        if (slist == null) {
            super.checkTopLevelToken();
        }
        else {
            final DetailAST lcurly = slist.getLastChild();
            // indentation checked as part of LITERAL IF check
            if (!TokenUtil.areOnSameLine(lcurly, getMainAst())) {
                super.checkTopLevelToken();
            }
        }
    }

    @Override
    protected DetailAST getNonListChild() {
        return getMainAst().getFirstChild();
    }

}
