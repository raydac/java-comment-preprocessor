package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import java.io.File;
import java.io.FileOutputStream;
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
        return null;
    }

    @Override
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        final File outFile = configurator.makeDestinationFile(state.getFileReference().getDestinationFilePath());
        try {
            state.saveBuffersToFile(outFile);
            state.reinitOutBuffers();
        } catch (IOException ex) {
            throw new RuntimeException("IO exception during execution", ex);
        }
        return DirectiveBehaviourEnum.PROCESSED;
    }
}
