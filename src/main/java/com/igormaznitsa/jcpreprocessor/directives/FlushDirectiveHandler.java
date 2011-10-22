package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FlushDirectiveHandler  extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "flush";
    }

    @Override
    public boolean hasExpression() {
        return false;
    }

    @Override
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) throws IOException {
        final File outFile = configurator.makeDestinationFile(state.getFileReference().getDestinationFilePath());
        
        state.saveBuffersToFile(outFile);
        state.reinitOutBuffers();
        
        return DirectiveBehaviour.NORMAL;
    }
    
    
}
