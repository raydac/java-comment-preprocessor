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
 * The class implements the //#_else directive handler
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class GlobalElseDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "_else";
    }

    @Override
    public String getReference() {
        return "inverts the conditional flag for the current global //#_if..//#_else..//#_endif construction";
    }

    @Override
    public boolean isGlobalPhaseAllowed() {
        return true;
    }

    @Override
    public boolean isPreprocessingPhaseAllowed() {
        return false;
    }
    
    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext configurator, final PreprocessingState state) {
        if (state.isIfStackEmpty()) {
            throw new IllegalStateException(DIRECTIVE_PREFIX+"_else without "+DIRECTIVE_PREFIX+"_if detected");
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
