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
 * The class implements the //#_endif directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class GlobalEndIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "_endif";
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public String getReference() {
        return "ends the current global //#_if..//#_else..//#_endif construction";
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
    public AfterDirectiveProcessingBehaviour execute(String string, PreprocessorContext configurator, PreprocessingState state) {
        if (state.isIfStackEmpty()) {
            throw new IllegalStateException(DIRECTIVE_PREFIX+"_endif without "+DIRECTIVE_PREFIX+"_if detected");
        }

        if (!state.isDirectiveCanBeProcessed() && state.isAtActiveIf()) {
            state.getPreprocessingFlags().remove(PreprocessingFlag.IF_CONDITION_FALSE);
        } 
        
        state.popIf();
        
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
