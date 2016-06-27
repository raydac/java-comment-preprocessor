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

import javax.annotation.Nonnull;

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * The class implements the //#noautoflush directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class NoAutoFlushHandler extends AbstractDirectiveHandler {

  @Override
  @Nonnull
  public String getName() {
    return "noautoflush";
  }

  @Override
  @Nonnull
  public String getReference() {
    return "disable autoflush for text buffers in the end of file processing";
  }

  @Override
  @Nonnull
  public AfterDirectiveProcessingBehaviour execute(@Nonnull final String string, @Nonnull final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();
    if (context.isVerbose()) {
      context.logForVerbose("AutoFlush for file has been disabled");
    }
    assertNotNull("The File stack is empty!", state.peekFile()).disableAutoFlush();
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
