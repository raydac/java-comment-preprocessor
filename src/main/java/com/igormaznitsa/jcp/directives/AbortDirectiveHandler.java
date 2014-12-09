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

import com.igormaznitsa.jcp.containers.PreprocessingFlag;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The class implements the //#abort directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class AbortDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  public String getName() {
    return "abort";
  }

  @Override
  public String getReference() {
    return "abort preprocessing and show the line tail as message (allows macroses)";
  }

  @Override
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.TAIL;
  }

  @Override
  public AfterDirectiveProcessingBehaviour execute(final String rawTail, final PreprocessorContext context) {
    final String normal = (!rawTail.isEmpty() && Character.isSpaceChar(rawTail.charAt(0))) ? rawTail.substring(1) : rawTail;
    final String message = "ABORT: "+PreprocessorUtils.processMacroses(normal, context);
    if (context.isVerbose()) {
      context.logForVerbose(message);
    }
    else {
      context.logInfo(message);
    }
    context.getPreprocessingState().getPreprocessingFlags().add(PreprocessingFlag.ABORT_PROCESSING);
    return AfterDirectiveProcessingBehaviour.READ_NEXT_LINE;
  }
}
