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

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;

/**
 * The class implements the //#postfix[+|-] directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PostfixDirectiveHandler  extends AbstractDirectiveHandler  {

    @Override
    public String getName() {
        return "postfix";
    }

    @Override
    public String getReference() {
        return "allows either to switch on (+) or switch off (-) the mode when all texts are printed into the postfix text buffer";
    }
    
    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.ONOFF;
    }

    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
        final PreprocessingState state = context.getPreprocessingState();
        if (!string.isEmpty()) {
            switch (string.charAt(0)) {
                case '+': {
                    state.setPrinter(PreprocessingState.PrinterType.POSTFIX);
                }
                break;
                case '-': {
                    state.setPrinter(PreprocessingState.PrinterType.NORMAL);
                }
                break;
                default:
                    throw new IllegalArgumentException("Unsupported paremeter");
            }
            return AfterDirectiveProcessingBehaviour.PROCESSED;
        }
        throw new IllegalArgumentException(DIRECTIVE_PREFIX+"prefix needs a parameter");
    }
}
