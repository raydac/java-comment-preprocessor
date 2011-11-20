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
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

/**
 * The class implements the //#ifdefined directive handler
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class IfDefinedDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "ifdefined";
    }

    @Override
    public String getReference() {
        return "works similar //#if but needs only a variable name to check that it has been defined";
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.VARNAME;
    }
    
    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context, final PreprocessingState state) {
        if (state.isDirectiveCanBeProcessed()){
            if (string.isEmpty()) {
                throw new IllegalArgumentException(DIRECTIVE_PREFIX+"ifdefined needs a variable");
            }
            state.pushIf(true);
            final boolean definitionFlag = context.findVariableForName(string,state) != null;
            if (!definitionFlag){
                state.getPreprocessingFlags().add(PreprocessingFlag.IF_CONDITION_FALSE);
            }
        }else{
            state.pushIf(false);
        }
 
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
