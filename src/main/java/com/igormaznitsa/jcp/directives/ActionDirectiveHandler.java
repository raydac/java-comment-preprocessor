/*
 * Copyright 2011 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307  USA
 */
package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.ExpressionItem;
import com.igormaznitsa.jcp.expression.ExpressionParser;
import com.igormaznitsa.jcp.expression.ExpressionTree;
import com.igormaznitsa.jcp.expression.Value;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The class implements the //#action directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ActionDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "action";
    }

    @Override
    public String getReference() {
        return "calls a preprocessor extension and pass directive arguments to it (arguments separated by comma)";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.MULTIEXPRESSION;
    }

    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
        if (context.getPreprocessorExtension() != null) {

            try {
                final List<ExpressionTree> args = parseString(string, context);

                final Value[] results = new Value[args.size()];
                int index = 0;
                for (final ExpressionTree expr : args) {
                    final Value val = Expression.evalTree(expr,context);
                    results[index++] = val;
                }

                if (!context.getPreprocessorExtension().processAction(context,results)) {
                    throw new RuntimeException("Extension can't process the action");
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException("Wrong string detected [" + string + ']', ex);
            }
        }
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }

    private List<ExpressionTree> parseString(final String str, final PreprocessorContext context) throws IOException {
        final ExpressionParser parser = ExpressionParser.getInstance();

        final PushbackReader reader = new PushbackReader(new StringReader(str));
        final List<ExpressionTree> result = new ArrayList<ExpressionTree>();

        while (true) {
            final ExpressionTree tree = new ExpressionTree();
            final ExpressionItem delimiter = parser.readExpression(reader, tree, context, false, true);

            if (delimiter != null && ExpressionParser.SpecialItem.COMMA != delimiter) {
                throw new IllegalArgumentException("Wrong format of an argument detected");
            }

            if (tree.isEmpty()) {
                if (delimiter == null) {
                    break;
                } else {
                    throw new IllegalArgumentException("Empty argument detected");
                }
            } else {
                result.add(tree);
                if (delimiter == null) {
                    break;
                }
            }
        }

        return result;
    }
}
