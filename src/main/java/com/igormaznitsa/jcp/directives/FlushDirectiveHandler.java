/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
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
    return "flush text buffers to disk and clear the buffers";
  }

  @Override
  public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();
    if (!context.isFileOutputDisabled()) {
      final File outFile = context.createDestinationFileForPath(state.getRootFileInfo().getDestinationFilePath());
      try {
        if (context.isVerbose()){
          context.logForVerbose("Flush buffers into file '"+outFile+'\'');
        }
        state.saveBuffersToFile(outFile, context.isRemoveComments());
        state.resetPrinters();
      }
      catch (IOException ex) {
        throw context.makeException("Can't flush text buffers", ex);
      }
    }
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
