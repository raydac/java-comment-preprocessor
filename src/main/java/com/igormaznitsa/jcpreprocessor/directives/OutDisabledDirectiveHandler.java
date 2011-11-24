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
 * The class implements the //#- directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class OutDisabledDirectiveHandler  extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "-";
    }

    @Override
    public String getReference() {
        return "allows to switch off the text output, text after the directive will not be placed into inside buffers";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.NONE;
    }
    
    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
        context.getPreprocessingState().getPreprocessingFlags().add(PreprocessingFlag.TEXT_OUTPUT_DISABLED);
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
    
    
}
