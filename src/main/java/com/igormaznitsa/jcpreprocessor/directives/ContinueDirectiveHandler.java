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
import com.igormaznitsa.jcpreprocessor.containers.TextFileDataContainer;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

/**
 * The class implements the //#continue directive handler
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ContinueDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "continue";
    }

    @Override
    public String getReference() {
        return "it continues current "+DIRECTIVE_PREFIX+"while iteration";
    }

    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext configurator, final PreprocessingState state) {
        if (state.isWhileStackEmpty()) {
            throw new IllegalStateException(getFullName()+" without "+DIRECTIVE_PREFIX+"while detected");
        }
        
        final TextFileDataContainer whileContainer = state.peekWhile();
        state.popAllIFUntilContainerWithFile(whileContainer);
        state.popWhile();
        state.goToString(whileContainer.getNextStringIndex());
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
