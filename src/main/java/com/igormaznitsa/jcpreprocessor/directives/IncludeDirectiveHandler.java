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

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

/**
 * The class implements the //#include directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class IncludeDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "include";
    }

    @Override
    public String getReference() {
        return "allows to include another file body into the current preprocessing file";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.STRING;
    }
    
    @Override
    public AfterDirectiveProcessingBehaviour execute(String string, PreprocessorContext context, PreprocessingState state) {
        final Value includedFilePath = Expression.evalExpression(string, context,state);

        if (includedFilePath == null || includedFilePath.getType() != ValueType.STRING) {
            throw new IllegalArgumentException(DIRECTIVE_PREFIX+"include needs a string expression as a file path");
        }

        try {
            state.openFile(context.getSourceFile(includedFilePath.asString()));
        }catch(IOException ex){
            throw new RuntimeException("Can't open a file to include ["+includedFilePath.asString()+']',ex);
        }
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
