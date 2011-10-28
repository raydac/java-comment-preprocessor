package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import java.io.File;
import java.io.IOException;

public class FlushDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "flush";
    }

    @Override
    public boolean hasExpression() {
        return false;
    }

    @Override
    public String getReference() {
        return "it flushes the current text buffer state in the file";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        final File outFile = configurator.makeDestinationFile(state.getRootFileInfo().getDestinationFilePath());
        try {
            state.saveBuffersToFile(outFile);
            state.resetPrinters();
        } catch (IOException ex) {
            throw new RuntimeException("IO exception during execution", ex);
        }
        return AfterProcessingBehaviour.PROCESSED;
    }
}
