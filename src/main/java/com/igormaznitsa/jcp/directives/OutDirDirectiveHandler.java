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
import com.igormaznitsa.jcp.context.JCPSpecialVariableProcessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;

/**
 * The class implements the //#outdir directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class OutDirDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "outdir";
    }

    @Override
    public String getReference() {
        return "allows to change the output directory for the preprocessing file (also it can be read through "+JCPSpecialVariableProcessor.VAR_DEST_DIR+')';
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.STRING;
    }
    
    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
        final Value name = Expression.evalExpression(string, context);

        if (name == null || name.getType() != ValueType.STRING) {
            throw new IllegalArgumentException(DIRECTIVE_PREFIX+"outdir needs a string expression");
        }
        context.getPreprocessingState().getRootFileInfo().setDestinationDir((String) name.getValue());
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
