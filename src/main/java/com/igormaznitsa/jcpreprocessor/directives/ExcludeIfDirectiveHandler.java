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

public class ExcludeIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "excludeif";
    }

    @Override
    public String getReference() {
        return "it excludes the file from the preprocessing list if the expression is true";
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
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.BOOLEAN;
    }

    @Override
    public AfterProcessingBehaviour execute(String string, PreprocessingState state, PreprocessorContext context) {
        state.pushExcludeIfData(state.getRootFileInfo(), string, state.peekFile().getNextStringIndex()-1);
        return AfterProcessingBehaviour.PROCESSED;
    }
}
