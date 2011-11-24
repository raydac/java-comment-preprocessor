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
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

/**
 * The class implements the //#else directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ElseDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "else";
    }

    @Override
    public String getReference() {
        return "a part of a "+DIRECTIVE_PREFIX+"if.."+DIRECTIVE_PREFIX+"endif structure, it inverts the condition flag";
    }

    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
        final PreprocessingState state = context.getPreprocessingState();
        if (state.isIfStackEmpty()) {
            throw new IllegalStateException(getFullName()+" without "+DIRECTIVE_PREFIX+"if detected");
        }

        if (state.isAtActiveIf()) {
            if (state.getPreprocessingFlags().contains(PreprocessingFlag.IF_CONDITION_FALSE)){
                state.getPreprocessingFlags().remove(PreprocessingFlag.IF_CONDITION_FALSE);
            } else {
                state.getPreprocessingFlags().add(PreprocessingFlag.IF_CONDITION_FALSE);
            }
        }
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
    
    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

}
