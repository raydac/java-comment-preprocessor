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
package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.JCPSpecialVariableProcessor;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

/**
 * The class implements the //#outname directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class OutNameDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "outname";
    }

    @Override
    public String getReference() {
        return "allows to change the result file name for the current file (it can be read through "+JCPSpecialVariableProcessor.VAR_DEST_FILE_NAME+')';
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.STRING;
    }
    
    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
        final Value dirName = Expression.evalExpression(string, context);

        if (dirName == null || dirName.getType() != ValueType.STRING) {
            throw new IllegalArgumentException(DIRECTIVE_PREFIX+"outname needs a string expression");
        }
        context.getPreprocessingState().getRootFileInfo().setDestinationName(dirName.asString());
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
