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

/**
 * The class implements the //#prefix[+|-] directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PrefixDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  public String getName() {
    return "prefix";
  }

  @Override
  public String getReference() {
    return "allows either to switch on (+) or switch off (-) the mode when all texts are printed into the prefix buffer";
  }

  @Override
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.ONOFF;
  }

  @Override
  public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();
    if (!string.isEmpty()) {
      switch (string.charAt(0)) {
        case '+': {
          state.setPrinter(PreprocessingState.PrinterType.PREFIX);
        }
        break;
        case '-': {
          state.setPrinter(PreprocessingState.PrinterType.NORMAL);
        }
        break;
        default:
          throw new IllegalArgumentException("Unsupported parameter");
      }
      return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
    throw new IllegalArgumentException(DIRECTIVE_PREFIX + "prefix needs a parameter");
  }
}
