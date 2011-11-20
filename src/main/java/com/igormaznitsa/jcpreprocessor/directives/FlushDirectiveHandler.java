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
import java.io.File;
import java.io.IOException;

/**
 * The class implements the //#flush directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class FlushDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "flush";
    }

    @Override
    public String getReference() {
        return "flushes the current accumulated text buffer content into the file";
    }

    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context, final PreprocessingState state) {
        if (!context.isFileOutputDisabled()) {
            final File outFile = context.makeDestinationFile(state.getRootFileInfo().getDestinationFilePath());
            try {
                state.saveBuffersToFile(outFile);
                state.resetPrinters();
            } catch (IOException ex) {
                throw new RuntimeException("IO exception during execution", ex);
            }
        }
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
